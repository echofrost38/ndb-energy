package com.ndb.auction.utils;

import java.util.Map;

public interface PdfGenerator {
    void generatePdfFile(String templateName, Map<String, Object> data, String pdfFileName);
}
