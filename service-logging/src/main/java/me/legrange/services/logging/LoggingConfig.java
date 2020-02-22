package me.legrange.services.logging;

import javax.validation.constraints.NotNull;
import me.legrange.log.Level;


/**
 *
 * @author gideon
 */
public class LoggingConfig {
    
    @NotNull(message="The log level must be specified")
    private Level level;
    private ConsonleLoggerConfig consoleLogger;
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

    public ConsonleLoggerConfig getConsoleLogger() {
        return consoleLogger;
    }

    public void setConsoleLogger(ConsonleLoggerConfig consoleLogger) {
        this.consoleLogger = consoleLogger;
    }
}
