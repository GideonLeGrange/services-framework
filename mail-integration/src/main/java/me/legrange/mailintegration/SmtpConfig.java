package me.legrange.mailintegration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * Configuration object - RabbitMQ setup
 *
 * @author gideon
 */
public class SmtpConfig {

    @NotBlank(message = "The SMTP Mail Host is required")
    private String smtpHost;
    @NotBlank(message = "The SMTP Mail Username is required")
    private String smtpUsername;
    @NotBlank(message = "The SMTP Mail Password is required")
    private String smtpPassword;
    @Positive(message = "The SMTP Mail Port is required")
    private int smtpPort;
    @NotBlank(message = "The SMTP Mail From Email is required")
    private String fromEmail;

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public String getSmtpUsername() {
        return smtpUsername;
    }

    public void setSmtpUsername(String smtpUsername) {
        this.smtpUsername = smtpUsername;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    @Override
    public String toString() {
        return "SmtpConfig{" + "smtpHost=" + smtpHost + ", smtpUsername=" + smtpUsername + ", smtpPassword=" + smtpPassword + ", smtpPort=" + smtpPort + ", fromEmail=" + fromEmail + '}';
    }

}
