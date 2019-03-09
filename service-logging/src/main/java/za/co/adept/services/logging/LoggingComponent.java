package za.co.adept.services.logging;

import static java.lang.String.format;
import za.co.adept.log.logger.FileLogger;
import za.co.adept.log.Log;
import za.co.adept.log.Logger;
import za.co.adept.log.logger.LoggerException;
import za.co.adept.log.logger.NumberedExceptionLogger;
import za.co.adept.services.Component;
import za.co.adept.services.ComponentException;
import za.co.adept.services.Service;

/**
 *
 * @author gideon
 */
public class LoggingComponent extends Component<LoggingConfig, Service> {

    public LoggingComponent(Service service) {
        super(service);
    }

    public LoggingComponent() {
    }

    @Override
    public void start(LoggingConfig config) throws ComponentException {
        try {
            Log.setDefaultLevel(config.getLevel());
            Logger logger = null;
            if (config.getFileLogger() != null) {
                logger = startFileLogger(config.getFileLogger());
            }
            if (logger == null) {
                throw new ComponentException(format("No primary logger defined. Check your configuration!"));
            }
            if (config.getNumberedExceptionLogger() != null) {
                logger = startNumberedExceptionLogger(logger, config.getNumberedExceptionLogger());
            }
            Log.setDefaultLogger(logger);
        } catch (LoggerException ex) {
            throw new ComponentException(format("Error setting up logging: %s", ex.getMessage()), ex);
        }
    }

    @Override
    public String getName() {
        return "logging";
    }
    
    @Override
    public Class<WithLogging> getWithClass() {
        return WithLogging.class;
    }

    private Logger startFileLogger(FileLoggerConfig flc) throws LoggerException {
        return new FileLogger(flc.getFileName());
    }

    private Logger startNumberedExceptionLogger(Logger logger, FileLoggerConfig flc) throws LoggerException {
        return new NumberedExceptionLogger(logger, flc.getFileName());
    }
}
