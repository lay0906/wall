package io.github.ray.wall.limiter;

import io.github.ray.wall.count.RollingNumber;
import io.github.ray.wall.exception.LimitException;

public class DefaultRateLimiter implements RateLimiter {

    private final String key; //资源名
    private final int limits; //qps
    private final RollingNumber counter;
    private final String errMsg ;

    public DefaultRateLimiter(String key, int limits){
        this.key = key;
        this.limits = limits;
        this.counter = new RollingNumber(1000, 10);
        errMsg = "resources[" + key + "] QPS exceeds limits[" + limits + "]";
    }

    @Override
    public boolean canPass() {
        counter.incr();
        if(counter.getRollingSum() > limits){
            return false;
        }
        return true;
    }

    @Override
    public void throwPass() {
        if(!canPass())
            throw new LimitException(errMsg + ", QPS[" + counter.getRollingSum() + "]");
    }
}
