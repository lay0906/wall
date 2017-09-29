package io.github.ray.wall.count;

public class RollingNumberTest {
    public static void main(String[] args) throws InterruptedException {
        RollingNumber rollingNumber = new RollingNumber(1000, 5);

        rollingNumber.incr();
        System.out.println(rollingNumber.getRollingSum());

        Thread.sleep(200);
        rollingNumber.incr();
        System.out.println(rollingNumber.getRollingSum());

        Thread.sleep(200);
        rollingNumber.incr();
        System.out.println(rollingNumber.getRollingSum());

        Thread.sleep(200);
        rollingNumber.incr();
        System.out.println(rollingNumber.getRollingSum());



        Thread.sleep(200);
        rollingNumber.incr();
        System.out.println(rollingNumber.getRollingSum());



        Thread.sleep(200);
        System.out.println("i:" + rollingNumber.getRollingSum());

        Thread.sleep(200);
        System.out.println("i:" + rollingNumber.getRollingSum());
    }
}
