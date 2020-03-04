package me.legrange.service.mongo;

import javax.validation.constraints.NotNull;

/**
 * @author GideonLeGrange
 */
public class MongoConfig {

    @NotNull(message = "Host is required")
    private String host;

    @NotNull(message = "Port is required")
    private int port;

    @NotNull(message = "Users database is required")
    private String usersDatabase;

    @NotNull(message = "Username is required")
    private String username;

    @NotNull(message = "Password is required")
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsersDatabase() {
        return usersDatabase;
    }

    public void setUsersDatabase(String usersDatabase) {
        this.usersDatabase = usersDatabase;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
