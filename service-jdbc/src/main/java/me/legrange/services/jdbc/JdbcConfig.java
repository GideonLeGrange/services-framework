package me.legrange.services.jdbc;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Configuration object
 *
 * @author gideon
 */
public final class JdbcConfig {

    @NotBlank(message = "The SQL dialect is required")
    private String dialect = "MYSQL";
    @NotBlank(message = "The SQL username is required")
    private String username;
    @NotNull(message = "The SQL password must be specified")
    private String password;
    @NotBlank(message = "The SQL database URL is required")
    private String url;
    @Min(value = 1, message = "SQL retry time must be between 1 and 300 seconds")
    @Max(value = 300, message = "SQL retry time must be between 1 and 300 seconds")
    private int retryTime = 10;
    @Min(value = 0, message = "SQL retry attempts must be between 0 and 10 times")
    @Max(value = 10, message = "SQL retry time must be between 0 and 10 times")
    @NotNull
    private int retryAttempts = 5;

    private int connectionPoolSize = 10;

    public JdbcConfig() {
    }

    public JdbcConfig(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public Integer getRetryTime() {
        return retryTime;
    }

    public String getDialect() {
        return dialect;
    }

    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }
}
