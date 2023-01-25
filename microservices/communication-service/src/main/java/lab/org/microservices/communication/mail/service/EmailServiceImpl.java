package lab.org.microservices.communication.mail.service;

import lab.org.microservices.communication.mail.EmailDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailServiceImpl implements EmailService {

    public static final String SUCCESS = "Mail sent successfully ...";
    public static final String ERROR = "Error while sending Mail";

    @Autowired
    private JavaMailSender mailer;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public String sendSimpleEmail(EmailDetails details) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(sender);
            msg.setTo(details.getReceipient());
            msg.setText(details.getMessageBody());
            msg.setSubject(details.getSubject());
            mailer.send(msg);
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
    }

    @Override
    public String sendEmailWithAttachments(EmailDetails details) {
        MimeMessage msg = mailer.createMimeMessage();
        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(msg, true);
            messageHelper.setFrom(sender);
            messageHelper.setTo(details.getReceipient());
            messageHelper.setText(details.getMessageBody());
            messageHelper.setSubject(details.getSubject());

            FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));
            messageHelper.addAttachment(file.getFilename(), file);
            mailer.send(msg);
            return SUCCESS;
        } catch (MessagingException e) {
            e.printStackTrace();
            return ERROR;
        }
    }
}
