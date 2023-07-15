package me.legrange.service.redis;

import jakarta.validation.constraints.NotBlank;

public class JedisConfig {

    @NotBlank(message = "The Redis host must be specified")
    private String hostname;

    private int port = 6379;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
