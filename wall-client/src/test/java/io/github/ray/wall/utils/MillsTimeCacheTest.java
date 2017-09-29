package io.github.ray.wall.utils;

public class MillsTimeCacheTest {

    private final static long ROUND = 1000000000L;
    private final static int COUNT = 5;


    public static void main(String[] args) {
        for(int i=0;i<COUNT;i++){
            long s = System.currentTimeMillis();
            for(long j=0; j<ROUND;j++){
                //long a = System.currentTimeMillis();
                long a = MillsTimeCache.currentTimeMillis();
            }
            System.out.println(System.currentTimeMillis() - s);
        }
    }
}
