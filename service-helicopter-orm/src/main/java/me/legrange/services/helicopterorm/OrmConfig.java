package me.legrange.services.helicopterorm;

import javax.validation.constraints.NotNull;
import me.legrange.config.Configuration;
import me.legrange.services.mysql.MySqlConfig;

/**
 *
 * @author matt-vm
 */
public class OrmConfig extends Configuration {

    @NotNull(message = "MySQL Config is required")
    private MySqlConfig mysql;

    public MySqlConfig getMysql() {
        return mysql;
    }

    public void setMysql(MySqlConfig mysql) {
        this.mysql = mysql;
    }

}
