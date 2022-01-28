package com.ndb.auction.service.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.ndb.auction.models.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

@Service
public class MailService {
	
	private JavaMailSender javaMailSender;
	
	private final Configuration configuration;
	
	@Autowired
    public MailService(Configuration configuration, JavaMailSender javaMailSender) {
   	 	this.configuration = configuration;
        this.javaMailSender = javaMailSender;
    }
	
	public void sendVerifyEmail(User user, String code, String template) throws MessagingException, IOException, TemplateException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject("NDB Auction");
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
}
