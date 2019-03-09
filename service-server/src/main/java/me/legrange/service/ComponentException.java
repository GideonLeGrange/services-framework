package me.legrange.service;

/**
 * Thrown by components if they want to signal a critical error. 
 * 
 * @author gideon
 */
public class ComponentException extends ServiceException {

    public ComponentException(String message) {
        super(message);
    }

    public ComponentException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
