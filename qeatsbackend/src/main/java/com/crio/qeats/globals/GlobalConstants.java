package com.crio.qeats.globals;

import redis.clients.jedis.JedisPool;

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
  }


  /**
   * Checks is cache is intiailized and available.
   * TIP: This would generally mean checking via {@link JedisPool}
   * @return true / false if cache is available or not.
   */
  public static boolean isCacheAvailable() {

     return false;
  }

  /**
   * Destroy the cache.
   * TIP: This is useful if cache is stale or while performing tests.
   */
  public static void destroyCache() {
  }
}
