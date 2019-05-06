package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author: huki-konghui
 * @Date: 2019/5/5 19:52
 * @Version 1.0
 */
public class RedisPool {
    private static JedisPool pool;//jedis 连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20")); //最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idel", "10"));  //最大的idel（空闲)状态的jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idel", "2"));//最小的idel（空闲)状态的jedis实例的个数
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));//在borrow一个jedis实例时，是否要进行验证操作，如果赋值为true，则得到的jedis实例一定是可以用的
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true")); //在return一个jedis实例时，是否要进行验证操作，如果赋值为true，则放回jedis连接池中的jedis实例一定是可以用的
    private static String redisIp = PropertiesUtil.getProperty("redis.ip");
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(true);//连接耗尽时是否阻塞，false会抛出异常，true会阻塞直到超时，默认为true
        pool = new JedisPool(config, redisIp, redisPort, 1000 * 2);
    }

    static {
        initPool();
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void returnResource(Jedis jedis) {
        pool.returnResource(jedis);//源码中已经判断是否为空
    }

    public static void returnBrokenResource(Jedis jedis) {
        pool.returnBrokenResource(jedis);//源码中已经判断是否为空
    }

    public static void main(String[] args) {
        Jedis jedis = pool.getResource();
        jedis.set("khkey","khvalue");
        returnResource(jedis);

        pool.destroy();//临时调用，销毁连池中的所有连接，平时写的时候不调用
        System.out.println("program is end ");
    }

}
