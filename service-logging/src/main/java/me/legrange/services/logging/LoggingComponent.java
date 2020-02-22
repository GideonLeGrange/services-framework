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
            FileLoggerConfig nelc = config.getNumberedExceptionLogger();
            if (nelc  != null) {
                logger = startNumberedExceptionLogger(logger, nelc);
            }
            Log.info("Switching logging to file %s with default level %s", config.getFileLogger().getFileName(), config.getLevel());
            Log.setDefaultLogger(logger);
            Log.setDefaultLevel(config.getLevel());
            Log.info("Logging to file %s with default level %s", config.getFileLogger().getFileName(), config.getLevel());
            if (nelc != null) {
                Log.info("Exception logging to file %s", nelc.getFileName());
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

    private Logger startNumberedExceptionLogger(Logger logger, FileLoggerConfig flc) throws LoggerException {

        if (flc.getFileName() != null) {
            return new NumberedExceptionLogger(logger, flc.getFileName());
        }
        return new NumberedExceptionLogger(logger, new PrintWriter(System.err));
    }
}
