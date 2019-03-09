package me.legrange.service.monitor;

/**
 * A measurement that contains a string value 
 * @author gideon
 */
public final class StringMeasurement extends Measurement<String> {
    
    public StringMeasurement(String name, Status status, String value) {
        super(name, status, value);
    }
    
}
