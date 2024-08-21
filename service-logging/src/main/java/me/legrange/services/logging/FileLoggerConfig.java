package me.legrange.services.logging;

import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author gideon
 */
public final class FileLoggerConfig {

    @NotBlank(message =  "A filename must be specified")
    private String fileName;
    private boolean useEmoji;


    public String getFileName() {
        return fileName;
    }

    public boolean isUseEmoji() {
        return useEmoji;
    }

}
