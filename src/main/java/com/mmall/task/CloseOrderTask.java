package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.common.RedisShardedPool;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: huki-konghui
 * @Date: 2019/5/8 14:20
 * @Version 1.0
 */
@Component
@Slf4j
public class CloseOrderTask {

    @Autowired
    private IOrderService iOrderService;

    @Scheduled(cron ="0 */1 * * * ?")
    //每一分钟执行一次,关闭两个小时之前开启的订单，但是为付款的订单
    public void closeOrderTaskV1(){
        log.info("关闭订单定时任务启动");
        int hour =Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
        iOrderService.closeOrder(hour);
        log.info("关闭订单定时任务结束");
    }


    public void closeOrderTaskV2(){
        log.info("关闭订单定时任务启动");
        long lockTimeOut =Long.parseLong(PropertiesUtil.getProperty("lock.timeout","5000"));
        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis() + lockTimeOut));
        if(setnxResult != null && setnxResult.intValue() ==1){
            //如果返回值为1，代表设置成功，获取锁
            this.closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }else{
            log.info("没有获取分布式锁：{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }
        log.info("关闭订单定时任务结束");
    }

    private void closeOrder(String lockName){
        RedisShardedPoolUtil.expire(lockName,50);//有效期50秒，防止死锁
        log.info("获取{}，ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        int hour =Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
        iOrderService.closeOrder(hour);
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        log.info("释放{}，ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        log.info("=============================");
    }
}
