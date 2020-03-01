package me.legrange.services.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

class ConnectionPool {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    ConnectionPool(JdbcConfig conf) {
        config.setJdbcUrl(conf.getUrl());
        config.setUsername(conf.getUsername());
        config.setPassword(conf.getPassword());
        ds = new HikariDataSource(config);
    }

    Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}
