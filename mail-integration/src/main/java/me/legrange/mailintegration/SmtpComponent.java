package me.legrange.mailintegration;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.logging.WithLogging;

/**
 *
 * @author gideon
 */
public class SmtpComponent extends Component<Service, SmtpConfig> implements WithLogging {

    private SmtpConfig conf;

    public SmtpComponent(Service service) {
        super(service);
    }

    @Override
    public void start(SmtpConfig conf) throws ComponentException {
        this.conf = conf;
    }

    @Override
    public String getName() {
        return "smtp";
    }

    public void sendMail(final String recipientEmail, final String subject, final String emailContent) throws MessagingException, UnsupportedEncodingException {

        final Properties props = getMailProperties();

        final Session session = Session.getInstance(props, new javax.mail.Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(conf.getSmtpUsername(), conf.getSmtpPassword());
            }
        });

        final MimeMessage mail = new MimeMessage(session);

        final Multipart mailParts = new MimeMultipart();

        final MimeBodyPart contentBodyPart = new MimeBodyPart();
        contentBodyPart.setContent(emailContent, "text/html");
        mailParts.addBodyPart(contentBodyPart);

//        if (attachments != null) {
//            for (final File file : attachments) {
//                final MimeBodyPart fileBodyPart = new MimeBodyPart();
//                fileBodyPart.setContent(emailContent, "text/html");
//                final DataSource ds = new FileDataSource(file);
//                fileBodyPart.setDataHandler(new DataHandler(ds));
//                fileBodyPart.setFileName(file.getName());
//                fileBodyPart.setDisposition(MimeBodyPart.INLINE);
//                fileBodyPart.setHeader("Content-ID", "<" + file.getName().substring(0, file.getName().lastIndexOf('.')) + ">");
//                mailParts.addBodyPart(fileBodyPart);
//            }
//        }
        mail.setFrom(new InternetAddress(conf.getFromEmail(), "Test"));
        mail.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        mail.setSubject(subject);
        mail.setContent(mailParts);
        System.out.println("Sending ");
        System.out.println(conf);
        Transport.send(mail);
        System.out.println("Sent");
    }

    private Properties getMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", conf.getSmtpPort());
        props.put("mail.smtp.host", conf.getSmtpHost());
        return props;
    }

}
