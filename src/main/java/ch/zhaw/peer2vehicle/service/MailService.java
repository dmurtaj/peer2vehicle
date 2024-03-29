package ch.zhaw.peer2vehicle.service;

import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ch.zhaw.peer2vehicle.model.Mail;

@Service
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);
    private final static String FROM_MAIL = "peer2vehicle@outlook.com";

    /**
     * Setups the mail configuraiton.
     * 
     * @return configured mail sender.
     */
    @PreAuthorize("isAuthenticated()")
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.office365.com");
        mailSender.setPort(587);
        mailSender.setUsername(FROM_MAIL);
        mailSender.setPassword("P2V123zhaw");
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        return mailSender;
    }

    @PreAuthorize("isAuthenticated()")
    public boolean sendMail(Mail mail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_MAIL);
            message.setTo(mail.getTo());
            message.setSubject(mail.getSubject());
            message.setText(mail.getMessage());
            var emailSender = getJavaMailSender();
            emailSender.send(message);
            return true;
        } catch (Exception e) {
            logger.error("Error sending the mail", e);
        }
        return false;
    }
}