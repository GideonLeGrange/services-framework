
module service.rabbitmq {
    requires com.rabbitmq.client;
    requires service.server;
    requires service.logging;
    requires java.validation;

    exports me.legrange.services.rabbitmq;
}
