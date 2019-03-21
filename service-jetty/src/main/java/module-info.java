
module service.jetty {
    requires javax.servlet.api;
    requires service.server;
    requires service.logging;

    exports me.legrange.services.jetty;
}
