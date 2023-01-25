package lab.org.microservices.communication;

import lab.org.microservices.communication.mail.EmailDetails;
import lab.org.microservices.communication.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import java.io.IOException;

@SpringBootApplication
public class CommunicationApplication implements CommandLineRunner {

    @Autowired
    private EmailService emailService;

    public static void main(String[] args) {
        SpringApplication.run(CommunicationApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Sending Email...");
        EmailDetails details = new EmailDetails("shahumar.pro@hotmail.com", "Test Body", "Test Email", "");
        String status = emailService.sendSimpleEmail(details);
        System.out.println(status);

    }
}
