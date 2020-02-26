package me.legrange.services.scheduler;

/**
 *
 * @author matthewl
 */
public class JobNotFoundException extends Exception {

    public JobNotFoundException(String message) {
        super(message);
    }
}
