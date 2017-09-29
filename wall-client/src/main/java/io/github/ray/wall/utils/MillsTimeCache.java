package io.github.ray.wall.utils;

import java.util.concurrent.TimeUnit;

/**
 * 避免System.currentTimeMillis()的调用，虽然该调用已经不是syscall，但是能避免就避免吧,测试效果还可以
 *
 */
public class MillsTimeCache implements Runnable{
    private static volatile long currentTimeMillis;
    static {
        currentTimeMillis = System.currentTimeMillis();
        Thread deamon = new Thread(new MillsTimeCache());
        deamon.setDaemon(true);
        deamon.setName("time tick thread");
        deamon.start();
    }

    @Override
    public void run() {
        while(true){
            currentTimeMillis = System.currentTimeMillis();
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
        }
    }

    public static long currentTimeMillis(){
        return currentTimeMillis;
    }
}
