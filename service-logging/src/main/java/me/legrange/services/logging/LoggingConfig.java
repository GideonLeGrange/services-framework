package me.legrange.services.logging;

import javax.validation.constraints.NotNull;
import me.legrange.log.Level;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author gideon
 */
public class LoggingConfig {
    
    @NotNull(message="The log level must be specified")
    private Level level;
    private ConsonleLoggerConfig consoleLogger;
    private FileLoggerConfig fileLogger;
    private NumberedLoggerConfig numberedExceptionLogger;
    private Map<String, Level> levels = new HashMap();

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

    public NumberedLoggerConfig getNumberedExceptionLogger() {
        return numberedExceptionLogger;
    }

    public void setNumberedExceptionLogger(NumberedLoggerConfig numberedExceptionLogger) {
        this.numberedExceptionLogger = numberedExceptionLogger;
    }

    public ConsonleLoggerConfig getConsoleLogger() {
        return consoleLogger;
    }

    public void setConsoleLogger(ConsonleLoggerConfig consoleLogger) {
        this.consoleLogger = consoleLogger;
    }

    public Map<String, Level> getLevels() {
        return levels;
    }

    public void setLevels(Map<String, Level> levels) {
        this.levels = levels;
    }
}
