
module service.mysql {
    requires java.sql;
    requires service.server;
    requires service.logging;
    requires java.validation;
    
    exports me.legrange.services.mysql;
}
