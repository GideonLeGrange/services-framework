package me.legrange.services.monitor;

/**
 *
 * @author gideon
 * @param <T> Type of the measurement 
 */
public abstract class Measurement<T> {
    
    private final String name; 
    private final Status status;
    private final T value;

    public Measurement(String name, Status status, T value) {
        this.name = name;
        this.status = status;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public T getValue() {
        return value;
    }
    
}
