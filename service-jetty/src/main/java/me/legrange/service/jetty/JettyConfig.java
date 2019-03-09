package me.legrange.service.jetty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author gideon
 */
public class JettyConfig {
    
    @NotNull @Min(value=1, message="Jetty port must be between 1 an 65536")
    @Max(value=65535, message="Jetty port must be between 1 an 65536")
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
}
