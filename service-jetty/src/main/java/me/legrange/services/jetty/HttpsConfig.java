package me.legrange.services.jetty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public final class HttpsConfig {
    @Min(value=1, message="Jetty HTTPS port must be between 1 an 65536")
    @Max(value=65535, message="Jetty HTTPS port must be between 1 an 65536")
    private int port;

    @NotBlank
    private String keystorePath;

    @NotBlank
    private String keystorePassword;


    public int getPort() {
        return port;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }
}
