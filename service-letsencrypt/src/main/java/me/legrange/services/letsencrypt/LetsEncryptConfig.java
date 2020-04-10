package me.legrange.services.letsencrypt;

import javax.validation.constraints.NotBlank;

public class LetsEncryptConfig {

    @NotBlank(message = "The Let's Encrypt data directory needs to be specified")
    private String dataDirectory;
    @NotBlank(message = "The domain for which to manage certificates")
    private String domain;

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
}
