package me.legrange.service.redis;

import javax.validation.constraints.NotBlank;

public class JedisConfig {

    @NotBlank(message="The Redis host must be specified")
    private String hostname;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
