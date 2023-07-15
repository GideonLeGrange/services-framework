package me.legrange.services.jetty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 *
 * @author gideon
 */
public class JettyConfig {
    
    @Min(value=1, message="Jetty port must be between 1 an 65536")
    @Max(value=65535, message="Jetty port must be between 1 an 65536")
    private int port;

    private boolean enableGzip = false;

    private String requestLog;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isEnableGzip() {
        return enableGzip;
    }

    public void setEnableGzip(boolean enableGzip) {
        this.enableGzip = enableGzip;
    }

    public String getRequestLog() {
        return requestLog;
    }

    public void setRequestLog(String requestLog) {
        this.requestLog = requestLog;
    }
}
