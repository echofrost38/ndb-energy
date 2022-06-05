package com.ndb.auction.utils;

import java.io.IOException;
import java.util.Map;

import com.google.zxing.WriterException;

public interface PdfGenerator {
    void generatePdfFile(String templateName, Map<String, Object> data, String pdfFileName);
    void generateQRCodeImage(String text, int width, int height) throws WriterException, IOException;
}
