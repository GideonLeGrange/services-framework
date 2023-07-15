package me.legrange.services.rabbitmq;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Configuration object - RabbitMQ setup
 *
 * @author gideon
 */
public class RabbitMqConfig {

    @NotBlank(message = "The RabbitMQ username must be specified")
    private String username;
    @NotBlank(message = "The RabbitMQ password must be specified")
    private String password;
    @NotBlank(message = "The RabbitMQ server host name must be specified")
    private String hostname;
    private boolean secure = false;
    @NotBlank(message = "The RabbitMQ server virtual host name must be specified (default '/')")
    private String virtualHost = "/";
    @Min(value = 1, message = "The RabbitMQ server must have a port in the range 1 to 65535")
    @Max(value = 65535, message = "The RabbitMQ server must have a port in the range 1 to 65535")
    private int port = 5672;
    @Min(value = 1, message = "The RabbitMQ retry time must be in the range of 1 to 900 seconds")
    @Max(value = 900, message = "The RabbitMQ retry time must be in the range of 1 to 900 seconds")
    private int retryTime = 10;

    private boolean startOnRequest = false;

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

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(int retryTime) {
        this.retryTime = retryTime;
    }

    public boolean isStartOnRequest() {
        return startOnRequest;
    }

    public void setStartOnRequest(boolean startOnRequest) {
        this.startOnRequest = startOnRequest;
    }
}
