package me.legrange.services.logging;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author gideon
 */
public class FileLoggerConfig {
    
    @NotBlank(message="The file logger needs to have a log file name specified")
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    
}
