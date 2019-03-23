
module service.jooq {
    requires service.mysql;
    
    exports me.legrange.services.jooq;
    requires org.jooq;
    requires service.server;
}
