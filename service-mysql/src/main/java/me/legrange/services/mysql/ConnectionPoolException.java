package me.legrange.services.mysql;

public class ConnectionPoolException extends RuntimeException {

    public ConnectionPoolException(String message, Throwable cause) {
        super(message, cause);
    }
}
