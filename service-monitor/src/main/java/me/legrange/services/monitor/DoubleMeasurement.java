package me.legrange.services.monitor;

/**
 * A measurement that contains a double value 
 * @author gideon
 */
public final class DoubleMeasurement extends Measurement<Double> {
    
    public DoubleMeasurement(String name, Status status, Double value) {
        super(name, status, value);
    }
    
}
