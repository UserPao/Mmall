package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: huki-konghui
 * @Date: 2019/5/7 10:39
 * @Version 1.0
 */
public class RedisShardedPool {
    private static ShardedJedisPool pool;//shardedJedis 连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20")); //最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idel", "10"));  //最大的idel（空闲)状态的jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idel", "2"));//最小的idel（空闲)状态的jedis实例的个数
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));//在borrow一个jedis实例时，是否要进行验证操作，如果赋值为true，则得到的jedis实例一定是可以用的
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true")); //在return一个jedis实例时，是否要进行验证操作，如果赋值为true，则放回jedis连接池中的jedis实例一定是可以用的
    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));
    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(true);//连接耗尽时是否阻塞，false会抛出异常，true会阻塞直到超时，默认为true
        JedisShardInfo info1 = new JedisShardInfo(redis1Ip,redis1Port,1000 * 2);
//        info1.setPassword();//jedis有密码的情况
        JedisShardInfo info2 = new JedisShardInfo(redis2Ip,redis2Port,1000* 2);
        List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>(2);
        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);
        //分片策略Hashing.MURMUR_HASH（默认值）对应一致性算法，还有一个是MD5
        pool = new ShardedJedisPool(config,jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static {
        initPool();
    }

    public static ShardedJedis getJedis() {//默认添加160个虚拟节点
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis) {
        pool.returnResource(jedis);//源码中已经判断是否为空
    }

    public static void returnBrokenResource(ShardedJedis jedis) {
        pool.returnBrokenResource(jedis);//源码中已经判断是否为空
    }

    public static void main(String[] args) {
        ShardedJedis jedis = pool.getResource();
        for (int i = 0 ;i<10;i++){
            jedis.set("key" + i,"value" + i);

        }
        returnResource(jedis);
//        pool.destroy();
        System.out.println("end");

    }
}
