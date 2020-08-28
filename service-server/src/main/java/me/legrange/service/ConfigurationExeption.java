package me.legrange.service;

public class ConfigurationExeption extends Exception{
    public ConfigurationExeption(String message) {
        super(message);
    }

    public ConfigurationExeption(String message, Throwable cause) {
        super(message, cause);
    }
}
