package me.legrange.services.letsencrypt;

public class LetsEcryptException extends Throwable {
    public LetsEcryptException(String message) {
        super(message);
    }

    public LetsEcryptException(String message, Throwable cause) {
        super(message, cause);
    }
}
