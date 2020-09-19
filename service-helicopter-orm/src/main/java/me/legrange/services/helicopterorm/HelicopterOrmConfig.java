package me.legrange.services.helicopterorm;

import me.legrange.config.Configuration;

/**
 *
 * @author matt-vm
 */
public class HelicopterOrmConfig extends Configuration {

    private boolean createMissingTables = false;
    private boolean isRollbackOnUncommittedClose = false;

    public boolean isCreateMissingTables() {
        return createMissingTables;
    }

    public void setCreateMissingTables(boolean createMissingTables) {
        this.createMissingTables = createMissingTables;
    }

    public boolean isRollbackOnUncommittedClose() {
        return isRollbackOnUncommittedClose;
    }

    public void setRollbackOnUncommittedClose(boolean rollbackOnUncommittedClose) {
        isRollbackOnUncommittedClose = rollbackOnUncommittedClose;
    }
}
