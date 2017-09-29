package io.github.ray.wall.limiter;

/**
 * 流控接口
 */
public interface RateLimiter {
    /**
     * 是否可以通过流控
     * @return
     */
    boolean canPass();

    /**
     * 超过流量限制直接抛出异常
     */
    void throwPass();
}
