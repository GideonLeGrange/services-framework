package me.legrange.service.monitor;

/**
 * A measurement that contains a int value 
 * @author gideon
 */
public final class IntegerMeasurement extends Measurement<Integer> {
    
    public IntegerMeasurement(String name, Status status, Integer value) {
        super(name, status, value);
    }
    
}
