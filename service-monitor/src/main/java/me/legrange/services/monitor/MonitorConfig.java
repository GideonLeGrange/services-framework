package me.legrange.services.monitor;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author gideon
 */
public class MonitorConfig {
    
    @NotBlank(message="The monitor URL path must not be empty")
    private String path = "/monitors";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
}
