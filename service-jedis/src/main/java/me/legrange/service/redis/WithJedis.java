package me.legrange.service.redis;

import me.legrange.service.ComponentNotFoundException;
import me.legrange.service.WithComponent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public interface WithJedis extends WithComponent {

    default JedisPool jedisPool() throws ComponentNotFoundException {
        return getComponent(JedisComponent.class).jedisPool();
    }
}
