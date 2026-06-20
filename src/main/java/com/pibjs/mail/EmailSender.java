package com.pibjs.mail;

import com.pibjs.config.EmailConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class EmailSender {

    private final JavaMailSender mailSender;

    private String to;
    private String subject;
    private String body;
    private File attachment;
    private List<InternetAddress> recipients;

    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public EmailSender to(String to) {
        this.to = to;
        this.recipients = parseRecipients(to);
        return this;
    }

    public EmailSender withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public EmailSender withMessage(String body) {
        this.body = body;
        return this;
    }

    public EmailSender attach(String fileDir) {
        this.attachment = new File(fileDir);
        return this;
    }

    public void send(EmailConfig config) {
        try {
            validateEmailState();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            configureMessage(helper, config);

            if (attachment != null) {
                helper.addAttachment(attachment.getName(), attachment);
            }

            mailSender.send(message);
            log.info("Email sent to {} with the subject '{}'", to, subject);
            reset();
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending the email", e);
        }
    }

    private void validateEmailState() {
        if (to == null || subject == null || body == null || recipients == null) {
            throw new IllegalStateException("Email configuration incomplete. Required fields: to, subject, body");
        }
    }

    private void configureMessage(MimeMessageHelper helper, EmailConfig config) throws MessagingException {
        helper.setFrom(config.getUsername());
        helper.setTo(recipients.toArray(new InternetAddress[0]));
        helper.setSubject(subject);
        helper.setText(body, true);
    }

    private void reset() {
        this.to = null;
        this.subject = null;
        this.body = null;
        this.attachment = null;
        this.recipients = null;
    }

    private List<InternetAddress> parseRecipients(String to) {
        List<InternetAddress> recipientsList = new ArrayList<>();

        String[] emails = to.replaceAll("\\s", "").split(";");

        for (String email : emails) {
            if (!email.isEmpty()) {
                try {
                    recipientsList.add(new InternetAddress(email));
                } catch (AddressException e) {
                    log.error("Invalid email address: {}", email, e);
                    throw new RuntimeException("Invalid email address: " + email, e);
                }
            }
        }

        return recipientsList;
    }
}
