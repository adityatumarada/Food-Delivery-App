package com.crio.qeats.globals;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.validation.constraints.NotNull;
import java.time.Duration;

public class GlobalConstants {

    // TODO: CRIO_TASK_MODULE_REDIS
    // The Jedis client for Redis goes through some initialization steps before you can
    // start using it as a cache.
    // Objective:
    // Some methods are empty or partially filled. Make it into a working implementation.
    public static final String REDIS_HOST = "localhost";
    public static final int REDIS_PORT = 6379;

    // Amount of time after which the redis entries should expire.
    public static final int REDIS_ENTRY_EXPIRY_IN_SECONDS = 3600;

    private static JedisPool jedisPool;

    /**
     * Initializes the cache to be used in the code.
     * TIP: Look in the direction of `JedisPool`.
     */
    public static void initCache() {
        jedisPool = new JedisPool(jedisPoolConfig(),REDIS_HOST, REDIS_PORT);

    }

    public static JedisPoolConfig jedisPoolConfig() {

        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);

        return poolConfig;
    }


    /**
     * Checks is cache is intiailized and available.
     * TIP: This would generally mean checking via {@link JedisPool}
     *
     * @return true / false if cache is available or not.
     */
    public static boolean isCacheAvailable() {

            try (Jedis jedis = jedisPool.getResource()) {

                if(jedis.isConnected()) {
                    return true;
                }
            }catch ( Throwable t){
        }
        return false;
    }

    /**
     * Destroy the cache.
     * TIP: This is useful if cache is stale or while performing tests
     */
    public static void destroyCache() {
        if(jedisPool!=null)
        jedisPool.getResource().flushAll();
    }


    public static JedisPool getJedisPool() {
        if (jedisPool!=null)
        return jedisPool;
        initCache();
        return jedisPool;
    }

    public static void setJedisPool(JedisPool abc) {
        jedisPool = abc;
    }
}
