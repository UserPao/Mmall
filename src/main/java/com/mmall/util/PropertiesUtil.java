package com.mmall.util;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
/**
 * @Author: huki-konghui
 * @Date: 2019/3/28 10:57
 * @Version 1.0
 */
public class PropertiesUtil {
    private  static Logger  logger =  LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties props;

    static{//静态代码块，类加载的只执行一次，并且最先执行，顺序为static->普通代码块->构造代码块
        String fileName = "mmall.properties";
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8") {

            });
        } catch (IOException e) {
            logger.error("配置文件读取异常",e);
        }

    }
    public static String getProperty(String key){
        String value =props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            return  null;
        }
        return value.trim();
    }
    public static String getProperty(String key,String defaultValue){
        String value =props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            value = defaultValue;
        }
        return value.trim();
    }

}
