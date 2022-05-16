package com.example.redisdemo;

import org.junit.jupiter.api.Test;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class RedisdemoApplicationTests {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    void testRedisson() {
        redissonClient.getBucket("hello").set("bug");
        String test = (String) redissonClient.getBucket("hello").get();
        System.out.println(test);
    }

    @Test
    void test1() {
        RBucket<Object> testBucket = redissonClient.getBucket("test5");
        testBucket.set("yuan", 5, TimeUnit.MINUTES);
        System.out.println(testBucket.get());
    }

    @Test
    void test2() {
        Iterable<String> keys = redissonClient.getKeys().getKeys();
        keys.forEach(key -> {
            System.out.println("key:" + key);
        });
    }

    @Test
    void test3() throws InterruptedException {
        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("countDownLatch");
        countDownLatch.trySetCount(20);
        RBucket<String> bucket8 = redissonClient.getBucket("bucket8");
        bucket8.set("yuankang ");
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        String oldValue = bucket8.get();
        System.out.println("旧值：" + oldValue);
        for (int i = 0; i < 20; i++) {
            executorService.submit(() -> {
                if (bucket8.compareAndSet(oldValue, "yuankang " + 2)) {
                    System.out.println("线程" + Thread.currentThread().getId() + "更新了bucket的值");
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        System.out.println("更新后的桶对象为：" + bucket8.get());
    }

    @Test
    void test4() {
        RBitSet simpleBitSet = redissonClient.getBitSet("simpleBitSet");
        simpleBitSet.set(0, true);
        simpleBitSet.set(1813, false);
        System.out.println(simpleBitSet.get(1));
    }

    @Test
    void test5() {
        RBloomFilter<Object> phoneListFilter = redissonClient.getBloomFilter("phoneList");
        //初始化布隆过滤器：预计元素为100000000L,误差率为3%
        phoneListFilter.tryInit(100000000L, 0.03);
        //将号码10086插入到布隆过滤器中
        phoneListFilter.add("10086");
        System.out.println(phoneListFilter.contains("123456"));
        System.out.println(phoneListFilter.contains("10086"));
    }


}
