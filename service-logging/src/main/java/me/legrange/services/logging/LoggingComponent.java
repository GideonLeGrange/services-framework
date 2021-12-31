package me.legrange.services.logging;

import static java.lang.String.format;
import me.legrange.log.Logger;
import me.legrange.log.Log;
import me.legrange.log.logger.ConsoleLogger;
import me.legrange.log.logger.FileLogger;
import me.legrange.log.logger.LoggerException;
import me.legrange.log.logger.NumberedExceptionLogger;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;

import java.io.PrintWriter;

/**
 *
 * @author gideon
 */
public class LoggingComponent extends Component<Service, LoggingConfig> {

    public LoggingComponent(Service service) {
        super(service);
    }


    @Override
    public void start(LoggingConfig config) throws ComponentException {
        try {
            Logger logger = null;
            if (config.getFileLogger() != null) {
                logger = startFileLogger(config.getFileLogger());
            }
            else if (config.getConsoleLogger() != null) {
                logger = startConsoleLogger(config.getConsoleLogger());
            }
            else {
                throw new ComponentException(format("No primary logger defined. Check your configuration!"));
            }
            NumberedLoggerConfig nelc = config.getNumberedExceptionLogger();
            if (nelc  != null) {
                logger = startNumberedExceptionLogger(logger, nelc);
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

    private Logger startFileLogger(FileLoggerConfig flc) throws LoggerException {
        return new FileLogger(flc.getFileName());
    }


    private Logger startConsoleLogger(ConsonleLoggerConfig clc) throws LoggerException {
        return new ConsoleLogger();
    }

    private Logger startNumberedExceptionLogger(Logger logger, NumberedLoggerConfig flc) throws LoggerException {

        if (flc.getFileName() != null) {
            return new NumberedExceptionLogger(logger, flc.getFileName());
        }
        return new NumberedExceptionLogger(logger, new PrintWriter(System.err));
    }
}
