package io.github.ray.wall.aop;

import org.springframework.stereotype.Component;

@Component("hello")
public class HelloBean implements IHello {

    @FlowLimit(qps = 10)
    @Override
    public void sayHello() {
        System.out.println("hello");
    }
}
