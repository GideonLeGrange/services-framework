package me.legrange.service.gson;

import me.legrange.service.ComponentException;

/**
 *
 * @author gideon
 */
public class GsonException extends ComponentException {

    public GsonException(String message) {
        super(message);
    }

    public GsonException(String message, Throwable cause) {
        super(message, cause);
    }

}
