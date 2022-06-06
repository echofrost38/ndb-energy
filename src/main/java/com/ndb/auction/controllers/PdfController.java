package com.ndb.auction.controllers;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import com.google.zxing.WriterException;
import com.ndb.auction.hooks.BaseController;
import com.ndb.auction.service.PdfGenerationService;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/download/pdf")
public class PdfController extends BaseController {
    
    @Autowired
    private PdfGenerationService pdfGenerationService;

    // download transaction content pdf
    @GetMapping(value="/transactions")
    public ResponseEntity<byte[]> downloadTransactionsAsPDF (HttpServletRequest request) throws WriterException, IOException, URISyntaxException {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        // get params from request
        var from = getLong(request, "from");
        var to = getLong(request, "to");
        
        // generate pdf and get file path
        var pdfPath = pdfGenerationService.generatePdfForMultipleTransactions(userId, from, to);

        var file = new FileSystemResource(pdfPath);
        try {
            var content = new byte[(int)file.contentLength()];
            IOUtils.read(file.getInputStream(), content);
            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION, 
                    String.format("attachment; filename=\"%s\"", pdfPath)
                )
                .contentLength(file.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}")    
    public ResponseEntity<byte[]> downloadTransactionAsPDF(@PathVariable("id") int id, HttpServletRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        
        // get types
        var transactionType = getString(request, "tx", true);
        var paymentType = getString(request, "payment", true);
        String pdfPath = "";
        try {
            pdfPath = pdfGenerationService.generatePdfForSingleTransaction(id, userId, transactionType, paymentType);
        } catch (Exception e1) {
            e1.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        try {
            var file = new FileSystemResource(pdfPath);
            var content = new byte[(int)file.contentLength()];
            IOUtils.read(file.getInputStream(), content);
            var pdfFile = new java.io.File(pdfPath);
            pdfFile.delete();
            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION, 
                    String.format("attachment; filename=\"%s\"", pdfPath)
                )
                .contentLength(file.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } 
    }

}
