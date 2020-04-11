package me.legrange.services.jetty;

import javax.validation.constraints.NotBlank;

public class SslConfig  {

    @NotBlank(message = "The SSL key store file must be specified")
    private String keyStoreFile;
    @NotBlank(message = "The SSL key store password must be specified")
    private String keyStorePassword;

    public String getKeyStoreFile() {
        return keyStoreFile;
    }

    public void setKeyStoreFile(String keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }
}
