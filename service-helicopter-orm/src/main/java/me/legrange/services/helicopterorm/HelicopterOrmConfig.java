package me.legrange.services.helicopterorm;

import me.legrange.config.Configuration;

/**
 *
 * @author matt-vm
 */
public class HelicopterOrmConfig extends Configuration {

    private boolean createMissingTables = false;
    private boolean rollbackOnUncommittedClose = false;
    private boolean useUnionAll = false;

    public boolean isCreateMissingTables() {
        return createMissingTables;
    }

    public void setCreateMissingTables(boolean createMissingTables) {
        this.createMissingTables = createMissingTables;
    }

    public boolean isRollbackOnUncommittedClose() {
        return rollbackOnUncommittedClose;
    }

    public void setRollbackOnUncommittedClose(boolean rollbackOnUncommittedClose) {
        rollbackOnUncommittedClose = rollbackOnUncommittedClose;
    }

    public boolean isUseUnionAll() {
        return useUnionAll;
    }

    public void setUseUnionAll(boolean useUnionAll) {
        this.useUnionAll = useUnionAll;
    }
}
