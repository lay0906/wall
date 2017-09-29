package io.github.ray.wall;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.github.ray.wall.limiter.DefaultRateLimiter;
import io.github.ray.wall.limiter.RateLimiter;

/**
 * 对外API
 */
public class Wall {
    private static ConcurrentMap<String, RateLimiter> limters = new ConcurrentHashMap<String, RateLimiter>();

    public static void addRateLimiter(String key, int limit){
        RateLimiter rateLimiter = limters.get(key);
        if(rateLimiter == null) {
            synchronized (limters) {
                if (limters.get(key) == null)
                    limters.put(key, new DefaultRateLimiter(key, limit));
            }
        }
    }

    public static void throwPass(String key){
        RateLimiter rateLimiter = limters.get(key);
        if(rateLimiter != null){
            rateLimiter.throwPass();
        }
    }
}
