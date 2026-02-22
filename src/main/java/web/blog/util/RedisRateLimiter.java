package web.blog.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.naming.Context;
import javax.naming.InitialContext;

public class RedisRateLimiter {
    private static JedisPool pool;

    public static final int ARTICLE_CREATE_LIMIT = 10;
    public static final int ARTICLE_UPDATE_LIMIT = 100;
    public static final int ARTICLE_DELETE_LIMIT = 20;
    public static final int IMAGE_UPLOAD_LIMIT = 50;
    public static final int WINDOW_SECONDS = 3600;

    static {
        try {
            Context ctx = new InitialContext();
            String host = (String) ctx.lookup("java:comp/env/redis.host");
            Integer port = (Integer) ctx.lookup("java:comp/env/redis.port");

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(20);
            config.setMaxIdle(10);
            config.setMinIdle(5);
            config.setTestOnBorrow(true);

            pool = new JedisPool(config, host, port, 5000);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Redis connection pool", e);
        }
    }

    public static boolean isAllowed(String userId, String operation,
                                    int maxRequests, int windowSeconds) {
        String key = "ratelimit:" + userId + ":" + operation;

        try (Jedis jedis = pool.getResource()) {
            Long count = jedis.incr(key);

            if (count == 1) {
                jedis.expire(key, windowSeconds);
            }

            return count <= maxRequests;
        } catch (Exception e) {
            throw new RuntimeException("Rate limiter unavailable", e);
        }
    }

    public static int getRemaining(String userId, String operation, int maxRequests) {
        String key = "ratelimit:" + userId + ":" + operation;

        try (Jedis jedis = pool.getResource()) {
            String value = jedis.get(key);
            if (value == null) {
                return maxRequests;
            }

            long count = Long.parseLong(value);
            return (int) Math.max(0, maxRequests - count);
        } catch (Exception e) {
            throw new RuntimeException("Rate limiter unavailable", e);
        }
    }

    public static long getResetTime(String userId, String operation) {
        String key = "ratelimit:" + userId + ":" + operation;

        try (Jedis jedis = pool.getResource()) {
            Long ttl = jedis.ttl(key);
            if (ttl != null && ttl > 0) {
                return ttl;
            }
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("Rate limiter unavailable", e);
        }
    }
}
