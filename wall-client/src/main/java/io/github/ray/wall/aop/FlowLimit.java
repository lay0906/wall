package io.github.ray.wall.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FlowLimit {
    /**
     * key 不指定取方法签名
     * @return
     */
    String key() default "";
    int qps() default 200;
}
