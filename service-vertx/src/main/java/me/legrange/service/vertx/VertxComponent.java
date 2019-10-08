package me.legrange.service.vertx;

import io.vertx.core.Vertx;
import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;

/**
 *
 * @author matt-vm
 */
public class VertxComponent extends Component<Service, VertxConfig> {

    private Vertx vertx;

    public VertxComponent(Service service) {
        super(service);
    }

    @Override
    public void start(VertxConfig config) throws ComponentException {
        vertx = Vertx.vertx();
    }

    public Vertx vertx() {
        return vertx;
    }

    @Override
    public String getName() {
        return "vertx";
    }

}
