package com.ndb.auction.resolver.payment.withdarw;

import com.ndb.auction.exceptions.BalanceException;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.exceptions.UserSuspendedException;
import com.ndb.auction.models.withdraw.PaypalWithdraw;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;
import com.ndb.auction.service.utils.MailService;
import com.ndb.auction.service.utils.TotpService;
import com.ndb.auction.service.withdraw.PaypalWithdrawService;
import freemarker.template.TemplateException;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Component
public class PaypalWithdrawResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    
	@Autowired
	protected PaypalWithdrawService paypalWithdrawService;

    @Autowired
    protected TotpService totpService;

    @Autowired
    private MailService mailService;


    @PreAuthorize("isAuthenticated()")
    public String generateWithdraw() {
        var userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getEmail();
        var user = userService.getUserByEmail(email);
        var code = totpService.getWithdrawCode(email);

        // send email
        try {
            mailService.sendVerifyEmail(user, code, "withdraw.ftlh");
        } catch (Exception e) {
        }

        return "Success";
    }

    // Create paypal withdraw request!
    /**
     * 
     * @param email receiver email address
     * @param target target currency
     * @param amount crypto amount to withdraw
     * @param sourceToken crypto token to withdraw
     * @return
     * @throws MessagingException
     */
    @PreAuthorize("isAuthenticated()")
    public PaypalWithdraw paypalWithdrawRequest(
        String email, 
        String target, 
        double amount, // amount in source token
        String sourceToken, 
        String code
    ) throws MessagingException {
        var userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        var userEmail = userDetails.getEmail();
        var user = userService.getUserById(userId);
        if (user.getIsSuspended()) {
            String msg = messageSource.getMessage("user_suspended", null, Locale.ENGLISH);
            throw new UserSuspendedException(msg);
        }

        // check withdraw code
        if (!totpService.checkWithdrawCode(userEmail, code)) {
            String msg = messageSource.getMessage("invalid_twostep", null, Locale.ENGLISH);
            throw new BalanceException(msg, "code");
        }

        // check source token balance
        double sourceBalance = internalBalanceService.getFreeBalance(userId, sourceToken);
        if (sourceBalance < amount) {
            String msg = messageSource.getMessage("insufficient", null, Locale.ENGLISH);
            throw new BalanceException(msg, "amount");
        }

        // KYC check
        var kycStatus = shuftiService.kycStatusCkeck(userId);
        if(!kycStatus) {
            String msg = messageSource.getMessage("no_kyc", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "userId");
        }
        
        // get crypto price
        double cryptoPrice = 0.0;
        if(sourceToken.equals("USDT")) {
            cryptoPrice = 1.0;
        } else {
            cryptoPrice = thirdAPIUtils.getCryptoPriceBySymbol(sourceToken);
        }
        
        double totalUSD = amount * cryptoPrice;
        double fiatAmount = 0.0;
        if(target.equals("USD")) {
            fiatAmount = totalUSD;
        } else {
            double fiatPrice = thirdAPIUtils.getCurrencyRate(target);
            fiatAmount = totalUSD * fiatPrice;
        }

        double fee = getPaypalWithdrawFee(userId, fiatAmount);
        double withdrawAmount = fiatAmount - fee;

        // send request
        var m = new PaypalWithdraw(userId, target, withdrawAmount, fee, sourceToken, cryptoPrice, amount, null, null, email);
        var res = (PaypalWithdraw) paypalWithdrawService.createNewWithdrawRequest(m);
        var superUsers = userService.getUsersByRole("ROLE_SUPER");
        try {
            mailService.sendWithdrawRequestNotifyEmail(superUsers, user, "PayPal", sourceToken, amount, target, email, null);
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    // confirm paypal withdraw
    @PreAuthorize("hasRole('ROLE_SUPER')")
    public int confirmPaypalWithdraw(int id, int status, String deniedReason, String code) throws Exception {
        if(!totpService.checkWithdrawConfirmCode(code)) {
            String msg = messageSource.getMessage("invalid_twostep", null, Locale.ENGLISH);
            throw new BalanceException(msg, "2FA");
        }
        return paypalWithdrawService.confirmWithdrawRequest(id, status, deniedReason);
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<PaypalWithdraw> getPaypalWithdrawByUser(int showStatus) {
        var userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (List<PaypalWithdraw>) paypalWithdrawService.getWithdrawRequestByUser(userId, showStatus);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    @SuppressWarnings("unchecked")
    public List<PaypalWithdraw> getPaypalWithdrawByUserByAdmin(int userId) {
        return (List<PaypalWithdraw>) paypalWithdrawService.getWithdrawRequestByUser(userId, 1);
    }


    @PreAuthorize("hasRole('ROLE_SUPER')")
    @SuppressWarnings("unchecked")
    public List<PaypalWithdraw> getPaypalWithdrawByStatus(int userId, int status) {
        return (List<PaypalWithdraw>) paypalWithdrawService.getWithdrawRequestByStatus(userId, status);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    @SuppressWarnings("unchecked")
    public List<PaypalWithdraw> getAllPaypalWithdraws() {
        return (List<PaypalWithdraw>) paypalWithdrawService.getAllWithdrawRequests();
    }

    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public List<PaypalWithdraw> getPaypalPendingWithdrawRequests() {
        var userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (List<PaypalWithdraw>) paypalWithdrawService.getAllPendingWithdrawRequests(userId);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    @SuppressWarnings("unchecked")
    public List<PaypalWithdraw> getPaypalPendingWithdrawRequestsByAdmin() {
        return (List<PaypalWithdraw>) paypalWithdrawService.getAllPendingWithdrawRequests();
    }

    @PreAuthorize("isAuthenticated()")
    public PaypalWithdraw getPaypalWithdrawById(int id, int showStatus) {
        var userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return (PaypalWithdraw) paypalWithdrawService.getWithdrawRequestById(id, userId, showStatus);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    public PaypalWithdraw getPaypalWithdrawByIdByAdmin(int id) {
        return (PaypalWithdraw) paypalWithdrawService.getWithdrawRequestById(id, 1);
    }

    @PreAuthorize("isAuthenticated()")
    public int changePayPalWithdrawShowStatus(int id, int showStatus) {
        return paypalWithdrawService.changeShowStatus(id, showStatus);
    }
}
