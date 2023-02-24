package me.legrange.services.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static java.lang.String.format;

public final class ConnectionPool {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    ConnectionPool(JdbcConfig conf) {
        config.setJdbcUrl(conf.getUrl());
        config.setUsername(conf.getUsername());
        config.setPassword(conf.getPassword());
        config.setMaximumPoolSize(conf.getConnectionPoolSize());
        ds = new HikariDataSource(config);
    }

    public Connection getConnection() throws ConnectionPoolException {
        try {
            return ds.getConnection();
        }
        catch (SQLException ex) {
            throw new ConnectionPoolException(format("Error getting SQL connection from connection pool (%s)", ex.getMessage()),ex);
        }
    }

    public void close() {
        ds.close();
    }

}
