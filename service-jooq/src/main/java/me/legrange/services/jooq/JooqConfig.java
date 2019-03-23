package me.legrange.services.jooq;

import javax.validation.constraints.NotNull;
import me.legrange.services.mysql.MySqlConfig;

/**
 *
 * @author matt
 */
public class JooqConfig {

    @NotNull(message = "MySQL Config is required")
    private MySqlConfig mysql;

    public MySqlConfig getMysql() {
        return mysql;
    }

    public void setMysql(MySqlConfig mysql) {
        this.mysql = mysql;
    }

}
