package com.mmall.util;

import com.google.common.collect.Lists;
import com.mmall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @Author: huki-konghui
 * @Date: 2019/5/6 11:02
 * @Version 1.0
 */
@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //对象的所有字段全部列入
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);
        //取消默认转换timestamps形式,不要把date转换成timestamps(时间戳)
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        //忽略空Bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        //所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));
        //忽略在json字符串中存在，但是在java对象中不存在对应属性的情况，防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> String obj2String(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("parse object to String error", e);
            return null;
        }
    }

    public static <T> String obj2StringPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("parse object to String error", e);
            return null;
        }
    }

    //第一个T表示将此方法声明为泛型方法，第二个是返回值类型，第三个是class 的类型
    public static <T> T string2Obj(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
        } catch (IOException e) {
            log.error("parse String to Object error", e);
            return null;
        }
    }
    //泛型集合反序列化专用
    public static <T> T string2Obj(String str, TypeReference<T> typeReference){
        if (StringUtils.isEmpty(str) || typeReference == null) {
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class)? (T) str : objectMapper.readValue(str, typeReference));
        } catch (IOException e) {
            log.error("parse String to Object error", e);
            return null;
        }
    }
    //多集合泛型类的反序列化
    public static <T> T string2Obj(String str,Class<?>collectionClass,Class<?>...elementsClass){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,elementsClass);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (IOException e) {
            log.error("parse String to Object error", e);
            return null;
        }
    }

//    public static void main(String[] args) {
//        User u1 = new User();
//        u1.setId(1);
//        u1.setEmail("1233331@qq.com");
//        String user1Json = JsonUtil.obj2String(u1);
//
//        String user1PrettyJson = JsonUtil.obj2StringPretty(u1);
//
//        log.info("user1Json:{}", user1Json);
//
//        log.info("user1PrettyJson:{}", user1PrettyJson);
//
//        User user = JsonUtil.string2Obj(user1Json,User.class);
//        List<User> userList = Lists.newArrayList();
//        userList.add(u1);
//        userList.add(user);
//        String userListStr = JsonUtil.obj2String(userList);
//
//        List<User> userListObj2 = JsonUtil.string2Obj(userListStr,List.class,User.class);
//
//        System.out.println("end");
//    }

}
