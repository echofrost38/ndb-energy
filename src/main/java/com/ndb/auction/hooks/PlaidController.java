package com.ndb.auction.hooks;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plaid")
public class PlaidController extends BaseController {
    
    @PostMapping("/deposit")
    public void handleDeposit(HttpServletRequest request) {
        
    }

}
