package me.legrange.services.logging;

import me.legrange.log.Log;
import me.legrange.log.Logger;
import me.legrange.log.logger.ConsoleLogger;
import me.legrange.log.logger.FileLogger;
import me.legrange.log.logger.LoggerException;
import me.legrange.log.logger.NumberedExceptionLogger;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

import static java.lang.String.format;

/**
 * @author gideon
 */
public final class LoggingComponent extends Component<Service<?>, LoggingConfig> {

    private Logger logger;

    public LoggingComponent(Service<?> service) {
        super(service);
    }


    @Override
    public void start(LoggingConfig config) throws ComponentException {
        try {
            logger = null;
            if (config.getFileLogger() != null) {
                logger = startFileLogger(config.getFileLogger());
            } else if (config.getConsoleLogger() != null) {
                logger = startConsoleLogger(config.getConsoleLogger());
            } else {
                throw new ComponentException("No primary logger defined. Check your configuration!");
            }
            NumberedLoggerConfig nelc = config.getNumberedExceptionLogger();
            if (nelc != null) {
                logger = startNumberedExceptionLogger(logger, nelc);
            }
            CustomLoggerConfig clc = config.getCustomLogger();
            if (clc != null) {
                logger = startCustomLogger(logger, clc);
            }
            if (!logger.getClass().equals(ConsoleLogger.class)) {
                Log.info("Switching logging to %s with default level %s", logger.getName(), config.getLevel());
            }
            Log.setDefaultLogger(logger);
            Log.setDefaultLevel(config.getLevel());
            if (!logger.getClass().equals(ConsoleLogger.class)) {
                Log.info("Logging to %s with default level %s", logger.getName(), config.getLevel());
            }
            if (!config.getLevels().isEmpty()) {
                Log.info("Setting up log levels for packages/name spaces");
                for (String name : config.getLevels().keySet()) {
                    Log.setLevel(name, config.getLevels().get(name));
                }
            }
        } catch (LoggerException ex) {
            throw new ComponentException(format("Error setting up logging: %s", ex.getMessage()), ex);
        }
    }

    @Override
    public String getName() {
        return "logging";
    }

    /** Get the logger being used.
     *
     * @return The logger
     */
    Logger getLogger() {
        return logger;
    }

    private Logger startFileLogger(FileLoggerConfig flc) throws LoggerException {
        return new FileLogger(flc.getFileName(), flc.isUseEmoji());
    }

    private Logger startConsoleLogger(ConsoleLoggerConfig conf) throws LoggerException {
        return new ConsoleLogger(conf.isUseEmoji());
    }

    private Logger startNumberedExceptionLogger(Logger logger, NumberedLoggerConfig flc) throws LoggerException {
        if (flc.getFileName() != null) {
            return new NumberedExceptionLogger(logger, flc.getFileName());
        }
        return new NumberedExceptionLogger(logger, new PrintWriter(System.err));
    }

    /**
     * Start a custom logger of the type specified with the given logger chain
     *
     * @param logger The logger we have
     * @param clc    The config
     * @return The new logger chained to the one we had
     */
    private Logger startCustomLogger(Logger logger, CustomLoggerConfig clc) throws LoggerException {
        try {
            Class<?> type = Class.forName(clc.getClassName());
            if (!Logger.class.isAssignableFrom(type)) {
                throw new LoggerException(format("Class %s does not implement a logger", type.getSimpleName()));
            }
            return (Logger) type.getConstructor(new Class[]{Logger.class}).newInstance(logger);
        } catch (ClassNotFoundException e) {
            throw new LoggerException(format("Cannot find class '%s' for custom logger. Check your configuration", clc.getClassName()));
        } catch (NoSuchMethodException e) {
            throw new LoggerException(format("Logger class '%s' does not have a constructor that accepts a Logger", clc.getClassName()));
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new LoggerException(format("Logger class '%s' fails on start (%s)", clc.getClassName(), e.getMessage()));
        }
    }
}
