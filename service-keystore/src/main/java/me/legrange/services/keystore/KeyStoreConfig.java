package me.legrange.services.keystore;

import javax.validation.constraints.NotBlank;

public class KeyStoreConfig {

    @NotBlank(message = "The key store file must be specified")
    private String fileName;
    @NotBlank(message = "The key store password must be specified")
    private String password;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
