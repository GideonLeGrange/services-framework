package me.legrange.services.logging;

import java.util.function.Supplier;
import me.legrange.log.Log;
import me.legrange.service.WithComponent;

/**
 *
 * @author gideon
 */
public interface WithLogging extends WithComponent {

    /**
     * Log critical event.
     *
     * @param fmt The message format string
     * @param args The message arguments
     */
    default void critical(String fmt, Object... args) {
        Log.critical(fmt, args);
    }

    /**
     * Log an exception as a critical error.
     *
     * @param ex The exception
     */
    default void critical(Throwable ex) {
        Log.critical(ex);
    }

    /**
     * Log an exception as a critical error.
     *
     * @param ex The exception
     * @param fmt The message format string
     * @param args The message arguments
     */
    default void critical(Throwable ex, String fmt, Object... args) {
        Log.critical(ex, fmt, args);
    }

    /**
     * Log error event.
     *
     * @param fmt The message format string
     * @param args The message arguments
     */
    default void error(String fmt, Object... args) {
        Log.error(fmt, args);
    }

    /**
     * Log an exception as an error.
     *
     * @param ex The exception
     */
    default void error(Throwable ex) {
        Log.error(ex);
    }

    /**
     * Log an exception as an error.
     *
     * @param ex The exception
     * @param fmt The message format string
     * @param args The message arguments
     */
    default void error(Throwable ex, String fmt, Object... args) {
        Log.error(ex, fmt, args);
    }

    /**
     * Log warning event.
     *
     * @param fmt The message format string
     * @param args The message arguments
     */
    default void warning(String fmt, Object... args) {
        Log.warning(fmt, args);
    }

    /**
     * Log an exception as a warning.
     *
     * @param ex The exception
     */
    default void warning(Throwable ex) {
        Log.warning(ex);
    }

    /**
     * Log an exception as a warning.
     *
     * @param ex The exception
     * @param fmt The message format string
     * @param args The message arguments
     */
    default void warning(Throwable ex, String fmt, Object... args) {
        Log.warning(ex, fmt, args);
    }

    /**
     * Log informational event.
     *
     * @param fmt The message format string
     * @param args The message arguments
     */
    default void info(String fmt, Object... args) {
        Log.info(fmt, args);
    }

    /**
     * Log debug event.
     *
     * @param fmt The message format string
     * @param args The message arguments
     */
    default void debug(String fmt, Object... args) {
        Log.debug(fmt, args);
    }

    /**
     * Log a debug event, while avoiding any expensive code unless the logging
     * occurs. Use this when part of the building is expensive operations.
     *
     * @param msg A supplier that will provide the debug message when required.
     */
    default void debug(Supplier<String> msg) {
        Log.debug(msg);
    }

}
