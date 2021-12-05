package com.ndb.auction.resolver;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.ndb.auction.exceptions.BadRequestException;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.KYCSetting;
import com.ndb.auction.payload.CryptoPayload;
import com.ndb.auction.service.SumsubService;
import com.ndb.auction.service.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class FinancialResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    // Deposit
    @PreAuthorize("isAuthenticated()")
    public CryptoPayload getDepositAddress() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userDetails.getId();

        

        return null;
    }

    // Direct Sale NDT Token
    @PreAuthorize("isAuthenticated()")
    public String directSale(double amount, double price, int whereTo) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userDetails.getId();
        
        if(amount <= 0 || price <= 0) {
            throw new BadRequestException("Amount and Price must be larger than 0.");
        }

        // price mean USD price. 
        double totalPrice = amount * price;

        List<KYCSetting> verifySettings = sumsubService.getKYCSettings();
        double kycThreshold = 0.0, amlThreshold = 0.0;
        for (KYCSetting setting : verifySettings) {
            if(setting.getKind().equals("KYC")) {
                kycThreshold = setting.getDirect();
            } else if (setting.getKind().equals("AML")) {
                amlThreshold = setting.getDirect();
            }
        }

        String response = "Failed";
        if(totalPrice < kycThreshold) {
            // don't need verification
            response = financialService.directSale(userId, amount, whereTo);
        } else if( totalPrice < amlThreshold) {
            // check kyc level
            if(sumsubService.checkVerificationStatus(userId, SumsubService.KYC)) {
                response = financialService.directSale(userId, amount, whereTo);
            } else {
                throw new UnauthorizedException("You must verify your identity to buy more than " + totalPrice + ".", "amount");
            }
        } else if( totalPrice >= amlThreshold) {
            // check aml level
            if(sumsubService.checkVerificationStatus(userId, SumsubService.AML)) {
                response = financialService.directSale(userId, amount, whereTo);
            } else {
                throw new UnauthorizedException("You must verify your proof of residence to buy more than " + totalPrice + ".", "amount");
            }
        }

        return response;
    }

    // Withdrawal
    @PreAuthorize("isAuthenticated()")
    public String withdrawal(String cryptoType, double amount) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userDetails.getId();
        
        // price converting
        double cryptoUnitPrice = cryptoService.getCryptoPriceBySymbol(cryptoType);
        double cryptoPrice = cryptoUnitPrice * amount;

        List<KYCSetting> verifySettings = sumsubService.getKYCSettings();
        double kycThreshold = 0.0, amlThreshold = 0.0;
        for (KYCSetting setting : verifySettings) {
            if(setting.getKind().equals("KYC")) {
                kycThreshold = setting.getWithdraw();
            } else if (setting.getKind().equals("AML")) {
                amlThreshold = setting.getWithdraw();
            }
        }

        if(cryptoPrice == 0 || amount < 0) {
            throw new BadRequestException("Withdraw amount must be largern than 0.");
        }

        if(cryptoPrice < kycThreshold) {
            // don't need verification

        } else if( cryptoPrice < amlThreshold) {
            // check kyc level
            if(sumsubService.checkVerificationStatus(userId, SumsubService.KYC)) {

            } else {
                throw new UnauthorizedException("You must verify your identity to withdraw more than " + cryptoPrice + ".", "amount");
            }
        } else if( cryptoPrice >= amlThreshold) {
            // check aml level
            if(sumsubService.checkVerificationStatus(userId, SumsubService.AML)) {

            } else {
                throw new UnauthorizedException("You must verify your proof of residence to withdraw more than " + cryptoPrice + ".", "amount");
            }
        }

        return "Sucess";    
    }
}
