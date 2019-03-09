package me.legrange.service.rabbitmq;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Configuration object - RabbitMQ setup
 * @author gideon
 */
public class RabbitMqConfig {
    
    @NotBlank(message = "The RabbitMQ username must be specified")
    private String username;
    @NotBlank(message = "The RabbitMQ password must be specified")
    private String password;
    @NotBlank(message = "The RabbitMQ server host name must be specified")
    private String hostname;
    @NotBlank(message = "The RabbitMQ server virtual host name must be specified (default '/')")
    private String virtualHost = "/";
    @Min(value=1, message="The RabbitMQ server must have a port in the range 1 to 65535") 
    @Max(value=65535,message="The RabbitMQ server must have a port in the range 1 to 65535")
    @NotNull
    private Integer port;
    @Min(value=1, message="The RabbitMQ retry time must be in the range of 1 to 900 seconds") 
    @Max(value=900,message="The RabbitMQ retry time must be in the range of 1 to 900 seconds")
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

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(Integer retryTime) {
        this.retryTime = retryTime;
    }
    
    
    
}
