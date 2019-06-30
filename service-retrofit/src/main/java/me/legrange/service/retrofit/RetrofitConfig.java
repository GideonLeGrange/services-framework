package me.legrange.service.retrofit;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author matt-vm
 */
public class RetrofitConfig {

    @NotBlank(message = "Please provide a base URL")
    private String baseUrl;

    @NotNull(message = "Please set the auth mode")
    private AuthMode authMode;

    @NotNull(message = "Please set if you would like to enable http logging")
    private boolean enableHttpLogging;

    private String autorizationToken;

    private String basicAuthUser;

    private String basicAuthPassword;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isEnableHttpLogging() {
        return enableHttpLogging;
    }

    public void setEnableHttpLogging(boolean enableHttpLogging) {
        this.enableHttpLogging = enableHttpLogging;
    }

    public String getAutorizationToken() {
        return autorizationToken;
    }

    public void setAutorizationToken(String autorizationToken) {
        this.autorizationToken = autorizationToken;
    }

    public AuthMode getAuthMode() {
        return authMode;
    }

    public void setAuthMode(AuthMode authMode) {
        this.authMode = authMode;
    }

    public String getBasicAuthUser() {
        return basicAuthUser;
    }

    public void setBasicAuthUser(String basicAuthUser) {
        this.basicAuthUser = basicAuthUser;
    }

    public String getBasicAuthPassword() {
        return basicAuthPassword;
    }

    public void setBasicAuthPassword(String basicAuthPassword) {
        this.basicAuthPassword = basicAuthPassword;
    }

    public enum AuthMode {
        NONE,
        AUTH_TOKEN,
        BASIC_AUTH
    }
}
