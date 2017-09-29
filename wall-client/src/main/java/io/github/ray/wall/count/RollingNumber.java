package io.github.ray.wall.count;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

import io.github.ray.wall.utils.MillsTimeCache;

/**
 *  基于时间窗口的计数器
 */
public class RollingNumber {

    private final int windowInMilliseconds; //时间窗口
    private final int numOfBuckets; //分段数
    private final int bucketSizeInMillseconds; //每个段的窗口
    private final BucketCircularArray buckets; //环形数组

    private ReentrantLock newBucketLock = new ReentrantLock(); //通常我们不加锁，但也没办法

    public RollingNumber(int windowInMilliseconds, int numOfBuckets){
        this.windowInMilliseconds = windowInMilliseconds;
        this.numOfBuckets = numOfBuckets;

        if (windowInMilliseconds % numOfBuckets != 0) {
            throw new IllegalArgumentException("The windowInMilliseconds must divide equally into numOfBuckets. For example 1000/10 is ok, 1000/11 is not.");
        }
        this.bucketSizeInMillseconds = windowInMilliseconds / numOfBuckets;

        buckets = new BucketCircularArray(numOfBuckets);
    }

    Bucket getCurrentBucket(){
        long currentTime = MillsTimeCache.currentTimeMillis();
        Bucket currentBucket = buckets.peekLast();
        if(currentBucket != null && currentTime < currentBucket.windowStart + this.bucketSizeInMillseconds){
            //这个段还在时间窗口内
            return currentBucket;
        }
        //该段不能用, 让一个线程去尝试转换到下一个段，其他线程就先操作这个段吧（有误差，为了性能）
        if(newBucketLock.tryLock()){
            try{
                if(currentBucket == null){
                    Bucket bucket = new Bucket(currentTime);
                    buckets.addLast(bucket);
                    return bucket;
                }else {
                    //去找一个有效的,循环去掉过期的
                    for(int i = 0; i < numOfBuckets; i++){
                        Bucket lastBucket = buckets.peekLast();
                        if (currentTime < lastBucket.windowStart + this.bucketSizeInMillseconds) {
                            return lastBucket;
                        } else if (currentTime - (lastBucket.windowStart + this.bucketSizeInMillseconds) > windowInMilliseconds) {
                            //整个时间窗口都过期了
                            reset();
                            return getCurrentBucket();
                        }
                        else{
                            //创建一个新的段，注意时间窗口
                            buckets.addLast(new Bucket(lastBucket.windowStart + this.bucketSizeInMillseconds));
                        }
                    }
                    // i = numOfBuckets - 1的时候addLast进去的在for里面不能返回，所以在这里返回
                    return buckets.peekLast();
                }
            } finally {
                newBucketLock.unlock();
            }
        } else {
            //其他线程只能先统计到该段了
            currentBucket = buckets.peekLast();
            if(currentBucket != null){
                return currentBucket;
            }else { //第一次
                try {
                    Thread.sleep(5);
                } catch (Exception e) {
                    // ignore
                }
                return getCurrentBucket();
            }
        }
    }

    private void reset() {
        buckets.clear();
    }

    public void incr() {
        getCurrentBucket().getAdder().increment();
    }

    public void incr(long value) {
        getCurrentBucket().getAdder().add(value);
    }

    /**
     * 获取时间窗口的计数
     * @return
     */
    public long getRollingSum(){
        Bucket lastBucket = getCurrentBucket();
        if(lastBucket == null){
            return 0;
        }
        long sum = 0;
        for(Bucket b : buckets){
            sum += b.getAdder().sum();
        }
        return sum;
    }


    /**
     * 环形数组
     */
    static class BucketCircularArray implements Iterable<Bucket> {

        private final AtomicReference<ListState> state;
        private final int dataLength;
        private final int numBuckets;

        @Override
        public Iterator<Bucket> iterator() {
            return Collections.unmodifiableList(Arrays.asList(getArray())).iterator();
        }


        BucketCircularArray(int size){
            AtomicReferenceArray<Bucket> _buckets = new AtomicReferenceArray<Bucket>(size + 1); // + 1 as extra room for the add/remove;
            state = new AtomicReference<ListState>(new ListState(_buckets, 0, 0));
            dataLength = _buckets.length();
            numBuckets = size;
        }

        public void clear() {
            while (true) {
                ListState current = state.get();
                ListState newState = current.clear();
                if (state.compareAndSet(current, newState)) {
                    return;
                }
            }
        }

        public void addLast(Bucket o){
            ListState currentState = state.get();
            ListState newState = currentState.addBucket(o);
            if(state.compareAndSet(currentState, newState)){
                return;
            }else{
                //并发冲突，调用者解决
                return;
            }
        }

        public Bucket getLast() {
            return peekLast();
        }

        public Bucket peekLast() {
            return state.get().tail();
        }

        public int size() {
            return state.get().size;
        }

        private Bucket[] getArray() {
            return state.get().getArray();
        }

        /**
         * 环形数组真正实现类,不可变类，线程安全, 需要观察下GC情况
         *
         */
        private class ListState {
            private final AtomicReferenceArray<Bucket> data; //考虑多线程可见性, 使用AtomicReferenceArray
            private final int size;
            private final int tail;
            private final int head;

            private ListState(AtomicReferenceArray<Bucket> data, int head, int tail){
                this.head = head;
                this.tail = tail;
                if(head == 0 && tail == 0){
                    size = 0;
                } else{
                    this.size = (tail + dataLength - head) % dataLength;
                }
                this.data = data;
            }

            public Bucket tail() {
                if(size == 0){
                    return null;
                }else{
                    return data.get(convert(size - 1));
                }
            }

            //线程安全 ??
            private Bucket[] getArray() {
                ArrayList<Bucket> array = new ArrayList<Bucket>();
                for (int i = 0; i < size; i++) {
                    array.add(data.get(convert(i)));
                }
                return array.toArray(new Bucket[array.size()]);
            }

            private int convert(int index) {
                return (index + head) % dataLength;
            }

            private ListState incrementTail(){
                if(size == numBuckets){ // 时间轮完全推，覆盖掉一个段
                    return new ListState(data, (head + 1) % dataLength, (tail + 1) % dataLength);
                } else {
                    return new ListState(data, head, (tail + 1) % dataLength);
                }
            }

            public ListState clear() {
                return new ListState(new AtomicReferenceArray<Bucket>(dataLength), 0, 0);
            }

            public ListState addBucket(Bucket b) {
                data.set(tail, b);
                return incrementTail();
            }
        }
    }


    /**
     * 段
     */
    static class Bucket {
        final long windowStart; //窗口开始时间
        final LongAdder adder; //采用jdk8的计数器，减少cas冲突

        Bucket(long startTime){
            this.windowStart = startTime;
            adder = new LongAdder();
        }

        long get(){
            return adder.sum();
        }

        LongAdder getAdder(){
            return adder;
        }
    }
}
