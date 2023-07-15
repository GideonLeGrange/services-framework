package me.legrange.services.influxdb;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Configuration object - RabbitMQ setup
 *
 * @author gideon
 */
public class InfluxDbConfig {

    @NotBlank(message = "The InfluxDB username is required")
    private String username;
    @NotNull(message = "The InfluxDB password must be specified")
    private String password;
    @NotBlank(message = "The InfluxDB database URL is required")
    private String url;

    @Min(value = 1, message = "MySQL retry time must be between 1 and 300 seconds")
    @Max(value = 300, message = "MySQL retry time must be between 1 and 300 seconds")
    @NotNull
    private Integer retryTime = 10;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(Integer retryTime) {
        this.retryTime = retryTime;
    }
}
