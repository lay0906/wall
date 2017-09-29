package io.github.ray.wall;

import io.github.ray.wall.Wall;

public class WallTest {

    public static void main(String[] args) throws InterruptedException {
        Wall.addRateLimiter("test", 100);
        for(int i = 1; i <=100; i++){
            test(i);
        }
        test(101);

        Thread.sleep(50);
        test(102);

        Thread.sleep(1000);
        test(103);
    }

    private static void test(int i){
       if(throwPassWrap("test"))
        System.out.println(i);
    }

    private static  boolean throwPassWrap(String key){
        try{
            Wall.throwPass("test");
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}
