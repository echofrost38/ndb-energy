package com.ndb.auction.hooks;

import javax.servlet.http.HttpServletRequest;

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
    // @GetMapping(value="/transactions")
    // public ResponseEntity<Resource> downloadTransactionsAsPDF (HttpServletRequest request) {
    //     // get params from request

    //     // generate pdf and get file path

    //     // create file resource
    //     InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        
    //     // return file
    //     return ResponseEntity.ok()
    //         .contentLength(contentLength)
    //         .contentType()
    //         .body(resource);
    // }

    @GetMapping(value = "/{id}")    
    public ResponseEntity<byte[]> downloadTransactionAsPDF(@PathVariable("id") int id, HttpServletRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        
        // get types
        var transactionType = getString(request, "tx", true);
        var paymentType = getString(request, "payment", true);
        var pdfPath = pdfGenerationService.generatePdfForSingleTransaction(id, userId, transactionType, paymentType);

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

}
