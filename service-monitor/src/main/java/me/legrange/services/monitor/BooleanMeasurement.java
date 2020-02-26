package me.legrange.services.monitor;

/**
 * A measurement that contains a boolean value 
 * @author gideon
 */
public final class BooleanMeasurement extends Measurement<Boolean> {
    
    public BooleanMeasurement(String name, Status status, Boolean value) {
        super(name, status, value);
    }
    
}
