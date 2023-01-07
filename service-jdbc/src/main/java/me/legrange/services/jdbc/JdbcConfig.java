package me.legrange.services.jdbc;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Configuration object
 *
 * @author gideon
 */
public class JdbcConfig {

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
    @NotNull
    private Integer retryTime = 10;

    private int connectionPoolSize = 10;

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

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }
}
