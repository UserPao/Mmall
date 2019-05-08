package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: huki-konghui
 * @Date: 2019/5/6 15:37
 * @Version 1.0
 */
@Slf4j
public class CookieUtil {
    //之前为.开头的domain，现在改成这样是解决tomcat8.5版本的问题
    private final static String COOKIE_DOMAIN = "happymmall.com";

    private final static String COOKIE_NAME = "mmall_login_token";

    public static String readLoginCookie(HttpServletRequest request) {
        Cookie[] cks = request.getCookies();
        if (cks != null) {
            for (Cookie ck : cks) {
                log.info("read CookieName:{},CookieValue:{}", ck.getName(), ck.getValue());
                if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
                    log.info("return cookieName:{},cookieValue:{}", ck.getName(), ck.getValue());
                    return ck.getValue();
                }
            }
        }
        return null;
    }

    //X::domain=".happymmall.com"
    //a:A.happymmall.com            cookie:domain=A.happymmall.com;path="/"
    //b:B.happymmall.com            cookie:domain=B.happymmall.com;path="/"
    //c:C.happymmall.com/test/cc    cookie:domain=A.happymmall.com;path="/test/cc"
    //d:A.happymmall.com/test/dd    cookie:domain=A.happymmall.com;path="/test/dd"
    //e:A.happymmall.com/test       cookie:domain=A.happymmall.com;path="/test"

    public static void writeLoginToken(HttpServletResponse response, String token) {
        Cookie ck = new Cookie(COOKIE_NAME, token);
        ck.setDomain(COOKIE_DOMAIN);
        ck.setPath("/");//代表根目录，
        ck.setHttpOnly(true);//防止脚本攻击
        //单位是秒
        //如果不设置，则cookie不会写入硬盘，而是写入内存，只在当前页面有效。
        ck.setMaxAge(60 * 60 * 24 * 365);//当前表示一年。如果是-1，则表示为永久的
        log.info("write cookieName:{},cookieValue:{}", ck.getName(), ck.getValue());
        response.addCookie(ck);
    }

    public static void delLoginCookie(HttpServletRequest request,HttpServletResponse response) {
        Cookie[] cks = request.getCookies();
        if (cks != null) {
            for (Cookie ck : cks) {
                if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");
                    ck.setMaxAge(0);//设置成0代表删除此cookie。
                    log.info("del cookieName:{},cookieValue:{}", ck.getName(), ck.getValue());
                    response.addCookie(ck);
                    return;
                }
            }
        }
    }


}
