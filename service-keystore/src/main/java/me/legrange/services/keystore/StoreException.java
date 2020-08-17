package me.legrange.services.keystore;

import me.legrange.service.ComponentException;

public class StoreException extends ComponentException  {

    public StoreException(String message) {
        super(message);
    }

    public StoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
