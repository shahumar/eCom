package lab.org.microservices.communication.mail.service;

import lab.org.microservices.communication.mail.EmailDetails;

public interface EmailService {

    String sendSimpleEmail(EmailDetails details);

    String sendEmailWithAttachments(EmailDetails details);
}
