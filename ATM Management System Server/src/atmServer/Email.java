package atmServer;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Email {

    private static Message mailSender = null;

    public static void initializeMailSender() {

        try {
            final String mailID = "atmmanagerserver@gmail.com";
            final String password = "atm.server";

            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");


            Session session = Session.getInstance(properties,
                    new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(mailID, password);
                        }
                    }
            );

            mailSender = new MimeMessage(session);
            mailSender.setFrom(new InternetAddress(mailID));

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    synchronized public static void sendMail(String to, String subject, String text) {

        Thread t = new Thread(new MailThread(mailSender, to, subject, text));
        t.start();
    }
}


class MailThread implements Runnable {
    private Message message;
    private String to;

    MailThread(Message m, String to, String subject, String text) {
        message = m;
        try {
            message.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
            this.to = to;

            ((MimeMessage) message).setSubject(subject);
            ((MimeMessage) message).setText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();

        }


    }


}