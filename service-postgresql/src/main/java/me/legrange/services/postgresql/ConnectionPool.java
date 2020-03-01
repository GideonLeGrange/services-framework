package me.legrange.services.postgresql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

class ConnectionPool {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    ConnectionPool(PostgresqlConfig myConf) {
        config.setJdbcUrl(myConf.getUrl());
        config.setUsername(myConf.getUsername());
        config.setPassword(myConf.getPassword());
        ds = new HikariDataSource(config);
    }


    Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}
