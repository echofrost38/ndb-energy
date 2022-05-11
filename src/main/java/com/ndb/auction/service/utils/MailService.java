package com.ndb.auction.service.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.ndb.auction.dao.oracle.balance.CryptoBalanceDao;
import com.ndb.auction.dao.oracle.user.UserDetailDao;
import com.ndb.auction.models.user.User;
import com.ndb.auction.payload.WithdrawRequest;
import com.ndb.auction.service.TokenAssetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

@Service
public class MailService {
	
	private JavaMailSender javaMailSender;
	
	private final Configuration configuration;

    private UserDetailDao userDetailDao;

    private TokenAssetService tokenAssetService;

    private CryptoBalanceDao balanceDao;
	
	@Autowired
    public MailService(Configuration configuration, JavaMailSender javaMailSender) {
   	 	this.configuration = configuration;
        this.javaMailSender = javaMailSender;
    }
	
	public void sendVerifyEmail(User user, String code, String template) throws MessagingException, IOException, TemplateException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject("Nyyu Account Verification");
        helper.setTo(user.getEmail());
        String emailContent = getEmailContent(user, code, template);
        helper.setText(emailContent, true);
        javaMailSender.send(mimeMessage);
    }

    private String getEmailContent(User user, String code, String template) throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
//        model.put("user", user);
        model.put("code", code);
        configuration.getTemplate(template).process(model, stringWriter);
        return stringWriter.getBuffer().toString();
    }

    public void sendNormalEmail(User user, String subject, String text) throws MessagingException, IOException, TemplateException  {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject(subject);
        helper.setTo(user.getEmail());
        String emailContent = getEmailContent(user, text, "AlertEmail.ftlh");
        helper.setText(emailContent, true);
        javaMailSender.send(mimeMessage);
    }

    public void sendBackupEmail(List<User> users, String... paths) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject("Backup report");
        helper.setText("Backed up database tables", true);
        for (var user : users) {
            helper.addTo(user.getEmail());
        }

        for (var path : paths) {
            var file = new java.io.File(path);
            helper.addAttachment(path, file);
        }
        javaMailSender.send(mimeMessage);
    }

    private String fillWithdrawRequestEmail(String template, WithdrawRequest contents) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, TemplateException, IOException {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("withdrawType", contents.getWithdrawType());
        configuration.getTemplate(template).process(model, stringWriter);
        return stringWriter.getBuffer().toString();
    }

    public void sendWithdrawRequestNotifyEmail(
        List<User> superUsers, User requester, String type, double withdrawAmount, String currency, String destination, String bankMeta
    ) throws MessagingException, TemplateNotFoundException, MalformedTemplateNameException, ParseException, TemplateException, IOException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject("Withdraw Request");
        
        // getting required information
        String avatarName = requester.getAvatar().getPrefix() + "." + requester.getAvatar().getName();
        var userDetail = userDetailDao.selectByUserId(requester.getId());
        var fullName = userDetail.getFirstName() + " " + userDetail.getLastName();
        var tokenId = tokenAssetService.getTokenIdBySymbol(currency);
        var balance = balanceDao.selectById(requester.getId(), tokenId).getFree();

        // withdarw type message
        String typeMessage = "";
        if(type.equals("PayPal")) {
            typeMessage = "PayPal email";
        }

        // build withdraw request
        var withdrawRequest = new WithdrawRequest(
            type, avatarName, requester.getEmail(), fullName, userDetail.getAddress(), 
            userDetail.getCountry(), balance, withdrawAmount, currency, typeMessage, destination, bankMeta);

        helper.setText(fillWithdrawRequestEmail("withdrawRequest.ftlh", withdrawRequest));
        for(var user: superUsers) {
            helper.addTo(user.getEmail());
        }
        javaMailSender.send(mimeMessage);
    }
}
