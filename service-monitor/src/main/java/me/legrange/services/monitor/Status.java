package me.legrange.services.monitor;

/**
 *
 * @author gideon
 */
public enum Status {
    OK(1), WARNING(2), ERROR(3);
    
    private final int statusCode;
    
    private Status(int statusCode){
        this.statusCode = statusCode;
    }
    
    public int getStatusCode(){
        return statusCode;
    }
}
