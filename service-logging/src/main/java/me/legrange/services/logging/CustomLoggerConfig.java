package me.legrange.services.logging;

import javax.validation.constraints.NotBlank;

public class CustomLoggerConfig {

    @NotBlank(message = "If using a custom logger, the class name must be specified")
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
