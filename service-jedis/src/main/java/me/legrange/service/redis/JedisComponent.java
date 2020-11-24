package me.legrange.service.redis;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * A component that provides access to Redis using the Jedis library.
 */
public class JedisComponent extends Component<Service, JedisConfig> {

    private JedisPool jedisPool;

    public JedisComponent(Service service) {
        super(service);
    }

    @Override
    public void start(JedisConfig config) throws ComponentException {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        jedisPool = new JedisPool(poolConfig, config.getHostname(), config.getPort());
    }

    @Override
    public String getName() {
        return "jedis";
    }

    JedisPool jedisPool() {
        return jedisPool;
    }
}
