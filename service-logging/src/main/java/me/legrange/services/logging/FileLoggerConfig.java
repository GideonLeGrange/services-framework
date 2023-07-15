package me.legrange.services.logging;

import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author gideon
 */
public class FileLoggerConfig {

    @NotBlank(message =  "A filename must be specified")
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    
}
