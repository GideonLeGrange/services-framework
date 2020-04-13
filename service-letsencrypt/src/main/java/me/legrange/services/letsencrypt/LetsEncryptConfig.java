package me.legrange.services.letsencrypt;

import javax.validation.constraints.NotBlank;

public class LetsEncryptConfig {

    @NotBlank(message = "The Let's Encrypt data directory needs to be specified")
    private String dataDirectory;
    @NotBlank(message = "The domain for which to manage certificates")
    private String domain;
    @NotBlank(message = "The Let's Encrypt URL must be specified")
    private String letsEncryptUrl = "acme://letsencrypt.org/staging";
    @NotBlank(message = "The organizartion name to use in certificates")
    private String organization;

    public String getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLetsEncryptUrl() {
        return letsEncryptUrl;
    }

    public void setLetsEncryptUrl(String letsEncryptUrl) {
        this.letsEncryptUrl = letsEncryptUrl;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
