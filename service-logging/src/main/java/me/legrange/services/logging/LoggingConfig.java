package me.legrange.services.logging;

import me.legrange.log.Level;

import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author gideon
 */
public final class LoggingConfig {
    
    @NotNull(message="The log level must be specified")
    private Level level = Level.INFO;
    private ConsoleLoggerConfig consoleLogger;
    private FileLoggerConfig fileLogger;
    private NumberedLoggerConfig numberedExceptionLogger;
    private CustomLoggerConfig customLogger;
    private Map<String, Level> levels = new HashMap<>();

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

    public ConsoleLoggerConfig getConsoleLogger() {
        return consoleLogger;
    }

    public void setConsoleLogger(ConsoleLoggerConfig consoleLogger) {
        this.consoleLogger = consoleLogger;
    }

    public Map<String, Level> getLevels() {
        return levels;
    }

    public void setLevels(Map<String, Level> levels) {
        this.levels = levels;
    }

    public CustomLoggerConfig getCustomLogger() {
        return customLogger;
    }

    public void setCustomLogger(CustomLoggerConfig customLogger) {
        this.customLogger = customLogger;
    }
}
