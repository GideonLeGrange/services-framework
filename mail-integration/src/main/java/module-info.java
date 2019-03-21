
module service.mail {

    requires service.logging;
    requires mail;
    requires service.server;
    
    exports me.legrange.mailintegration;
}
