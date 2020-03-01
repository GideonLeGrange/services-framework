package me.legrange.services.jdbc;

public class ConnectionPoolException extends RuntimeException {

    public ConnectionPoolException(String message, Throwable cause) {
        super(message, cause);
    }
}
