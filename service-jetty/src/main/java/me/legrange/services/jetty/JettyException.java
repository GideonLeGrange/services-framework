package me.legrange.services.jetty;

import me.legrange.service.ComponentException;

public class JettyException extends ComponentException  {

    public JettyException(String message) {
        super(message);
    }

    public JettyException(String message, Throwable cause) {
        super(message, cause);
    }
}
