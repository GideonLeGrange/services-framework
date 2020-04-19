package me.legrange.service.redis;

import me.legrange.service.ComponentNotFoundException;
import me.legrange.service.WithComponent;
import redis.clients.jedis.Jedis;

public interface WithJedis extends WithComponent {

    default Jedis redis() throws ComponentNotFoundException {
        return getComponent(JedisComponent.class).jedis();
    }
}
