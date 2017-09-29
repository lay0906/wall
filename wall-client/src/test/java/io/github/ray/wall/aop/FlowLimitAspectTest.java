package io.github.ray.wall.aop;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FlowLimitAspectTest {
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("app.xml");

        //IHello hello = ctx.getBean(HelloBean.class); //加了aop为什么会失败?

        IHello hello = (IHello)ctx.getBean("hello");


        for(int i = 0; i < 10; i++){
            hello.sayHello();
        }
        try{
            hello.sayHello();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        Thread.sleep(1000);
        System.out.println("==========");
        for(int i = 0; i < 10; i++){
            hello.sayHello();
        }
        Thread.sleep(1000);
        hello.sayHello();
    }
}
