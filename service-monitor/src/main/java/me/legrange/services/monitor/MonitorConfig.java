package me.legrange.services.monitor;

import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author gideon
 */
public final class MonitorConfig {

    private boolean enabled = true;
    @NotBlank(message="The monitor URL path must not be empty")
    private String path = "/monitors";

    public MonitorConfig() {
    }

    public static MonitorConfig disabled() {
        var conf = new MonitorConfig();
        conf.enabled = false;
        return conf;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
