package me.legrange.service;

public final class EnvironmentDetectionException extends ServiceException {

    public EnvironmentDetectionException(String message) {
        super(message);
    }

    public EnvironmentDetectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
