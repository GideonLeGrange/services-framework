package me.legrange.services.monitor;

/**
 * A measurement that contains a long value 
 * @author gideon
 */
public final class LongMeasurement extends Measurement<Long> {
    
    public LongMeasurement(String name, Status status, Long value) {
        super(name, status, value);
    }
    
}
