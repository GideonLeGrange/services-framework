package me.legrange.service;

public class ComponentNotFoundException extends RuntimeException {

    public ComponentNotFoundException(String message) {
        super(message);
    }

    public ComponentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
