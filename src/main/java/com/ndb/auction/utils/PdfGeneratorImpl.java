package com.ndb.auction.utils;

import java.io.FileOutputStream;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfGeneratorImpl implements PdfGenerator {

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void generatePdfFile(String templateName, Map<String, Object> data, String pdfFileName) {
        Context context = new Context();
        context.setVariables(data);
        var htmlContent = templateEngine.process(templateName, context);

        try {
            FileOutputStream fileOS = new FileOutputStream(pdfFileName);
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(fileOS, false);
            renderer.finishPDF();   
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    
}
