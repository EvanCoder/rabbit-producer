package com.example.redisson;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Evan
 * @create 2021/2/27 20:51
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = RedissonTest.class)
@Slf4j
public class RedissonTest {

    @Test
    public void test() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");

        RedissonClient client = Redisson.create(config);

        log.info("Enter redis");
        RLock lock = client.getLock("test");
        try {
            lock.lock(20, TimeUnit.SECONDS);
            log.info("Access redis lock");
            Thread.sleep(10000);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            //lock.lock 表示线程等待超过设置时间，锁即取消等待
            //lock.unlock 执行源码会判断当前线程有没有获取到锁，如果没获取到锁就解锁会
            //抛出 new IllegalMonitorStateException();
            lock.unlock();
            log.info("Unlock");
        }
    }


    public static void main(String[] args) throws Exception {
//        testIP();

        Date date = new Date();
        System.out.println(date.getTime());

    }

    public void testUUID(){
        String uid = UUID.randomUUID().toString();
        System.out.println(uid);
        System.out.println(uid.hashCode());
    }

    public static void testIP() throws Exception{
        Enumeration en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()){
            NetworkInterface i = (NetworkInterface) en.nextElement();
            for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements(); ){
                InetAddress addr = (InetAddress) en2.nextElement();
                if (addr.isLoopbackAddress()){
                    continue;
                }
                if (addr instanceof Inet4Address){
                    System.out.println("ip4," + addr);
                }
                if (addr instanceof Inet6Address){
                    System.out.println("ip6," + addr);
                }
            }
        }
    }

}
