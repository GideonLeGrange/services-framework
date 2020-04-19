package me.legrange.service.redis;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import redis.clients.jedis.Jedis;

/** A component that provides access to Redis using the Jedis library. */
public class JedisComponent extends Component<Service, JedisConfig> {

    private  Jedis jedis;
    public JedisComponent(Service service) {
        super(service);
    }

    @Override
    public void start(JedisConfig config) throws ComponentException {
         jedis = new Jedis(config.getHostname());
    }

    @Override
    public String getName() {
        return "jedis";
    }

    Jedis jedis() {
        return jedis;
    }
}
