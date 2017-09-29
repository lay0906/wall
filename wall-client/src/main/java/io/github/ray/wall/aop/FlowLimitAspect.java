package io.github.ray.wall.aop;

import io.github.ray.wall.Wall;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class FlowLimitAspect {

    @Before(value = "execution(@FlowLimit * *.*(..)) && @annotation(flowLimit)")
    public void beforeExec(JoinPoint joinPoint, FlowLimit flowLimit){
        String key = flowLimit.key();
        if(key.length() == 0){
            key = joinPoint.getSignature().toString();
        }
        Wall.addRateLimiter(key, flowLimit.qps());
        Wall.throwPass(key);
    }
}
