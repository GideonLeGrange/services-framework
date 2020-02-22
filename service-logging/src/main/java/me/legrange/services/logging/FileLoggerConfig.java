package me.legrange.services.logging;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author gideon
 */
public class FileLoggerConfig {
    
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    
}
