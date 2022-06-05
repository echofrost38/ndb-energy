package com.ndb.auction.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfGeneratorImpl implements PdfGenerator {

    @Autowired
    ResourceLoader resourceLoader;

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

            renderer.getFontResolver().addFontDirectory(resourceLoader.getResource("classpath:static/fonts/").getURL().getPath(), true);
            renderer.setDocumentFromString(htmlContent, String.valueOf(resourceLoader.getResource("classpath:static/fonts/").getURI()));
            renderer.layout();
            renderer.createPDF(fileOS, false);
            renderer.finishPDF();   
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        var qrCodeWriter = new QRCodeWriter();
        var bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        Path path = FileSystems.getDefault().getPath(resourceLoader.getResource("classpath:static/images/qr.png").getURI().getPath());
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }
    
}
