package lab.org.microservices.communication.mail;

public class EmailDetails {

    private String receipient;
    private String messageBody;
    private String subject;
    private String attachment;

    public EmailDetails() {
    }

    public EmailDetails(String receipient, String messageBody, String subject, String attachment) {
        this.receipient = receipient;
        this.messageBody = messageBody;
        this.subject = subject;
        this.attachment = attachment;
    }

    public String getReceipient() {
        return receipient;
    }

    public void setReceipient(String receipient) {
        this.receipient = receipient;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}
