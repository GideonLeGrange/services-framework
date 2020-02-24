package me.legrange.services.postgresql;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Configuration object - RabbitMQ setup
 *
 * @author gideon
 */
public class PostgresqlConfig {

    @NotBlank(message = "The Postgresql username is required")
    private String username;
    @NotNull(message = "The Postgresql password must be specified")
    private String password;
    @NotBlank(message = "The Postgresql database URL is required")
    private String url;

    @Min(value = 1, message = "Postgresql retry time must be between 1 and 300 seconds")
    @Max(value = 300, message = "Postgresql retry time must be between 1 and 300 seconds")
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
