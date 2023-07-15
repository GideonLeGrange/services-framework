package me.legrange.service.redis;

import jakarta.validation.constraints.NotBlank;

public class MapDbConfig {

    @NotBlank(message = "The MapDB database must be defined")
    private String databaseFile;
    private boolean memoryMapped = false;


    public String getDatabaseFile() {
        return databaseFile;
    }

    public void setDatabaseFile(String databaseFile) {
        this.databaseFile = databaseFile;
    }

    public boolean isMemoryMapped() {
        return memoryMapped;
    }

    public void setMemoryMapped(boolean memoryMapped) {
        this.memoryMapped = memoryMapped;
    }
}
