package za.co.adept.services.logging;

import javax.validation.constraints.NotNull;
import za.co.adept.log.Level;


/**
 *
 * @author gideon
 */
public class LoggingConfig {
    
    @NotNull(message="The log level must be specified")
    private Level level;
    @NotNull(message="A file logger must be configured")
    private FileLoggerConfig fileLogger;
    private FileLoggerConfig numberedExceptionLogger;

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public FileLoggerConfig getFileLogger() {
        return fileLogger;
    }

    public void setFileLogger(FileLoggerConfig fileLogger) {
        this.fileLogger = fileLogger;
    }

    public FileLoggerConfig getNumberedExceptionLogger() {
        return numberedExceptionLogger;
    }

    public void setNumberedExceptionLogger(FileLoggerConfig numberedExceptionLogger) {
        this.numberedExceptionLogger = numberedExceptionLogger;
    }

    
}
