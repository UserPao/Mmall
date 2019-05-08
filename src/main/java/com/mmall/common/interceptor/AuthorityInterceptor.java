package com.mmall.common.interceptor;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author: huki-konghui
 * @Date: 2019/5/7 18:21
 * @Version 1.0
 */
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse, Object handler)
            throws Exception {
        log.info("preHandle");
        HandlerMethod handlerMethod = (HandlerMethod)handler;

        String methodName= handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();
        //解析参数，具体参数的key和value
        StringBuffer requestBuffer = new StringBuffer();
        Map paramMap = httpServletRequest.getParameterMap();
        Iterator it = paramMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            String mapKey = (String)entry.getKey();
            String mapValue = StringUtils.EMPTY;
            //request参数的map，里边的value返回的是一个String[]数组
            Object obj = entry.getValue();
            if(obj instanceof  String[]){
                String [] strs = (String[])obj;
                mapValue = Arrays.toString(strs);
            }
            requestBuffer.append(mapKey).append("=").append(mapValue);
        }

        if(StringUtils.equals(className,"UserManageController") && StringUtils.equals(methodName,"login")){
            log.info("权限拦截器连接到请求，calssname:{},methodName:{}",className,methodName);
            //如果是拦截到登录请求。不打印参数，因为参数中有密码，全部会打印到日志中，防止日志泄露
            return true;
        }
        log.info("权限拦截器连接到请求，calssname:{},methodName:{},param:{}",className,methodName,requestBuffer.toString());
        User user = null;

        String loginToken = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isNotEmpty(loginToken)) {
            String UserJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(UserJsonStr, User.class);
        }
        if(user == null || (user.getRole().intValue() != Const.Role.ROLE_ADMIN)){
            // 返回false,即不会调用controller里边的方法
            httpServletResponse.reset();//这里要添加reset，否则会报异常，getWriter（）has already been called for this response。
            httpServletResponse.setCharacterEncoding("UTF-8");//设置编码，否则会乱码
            httpServletResponse.setContentType("application/json;charset=UTF-8");//设置返回值的类型，因为是json接口
            PrintWriter out = httpServletResponse.getWriter();
            //上传富文本由于控件的要求，要特殊处理返回值，这里面区分是否登录和是否有权限。
            if(user == null){

                if(StringUtils.equals(className,"ProductManageController") &&
                        StringUtils.equals(methodName,"richtextImgUpload")){//对富文本上传的单独处理
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","请登录管理员");
                    out.print(JsonUtil.obj2String(resultMap));
                }else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGONIN.getCode(),"拦截器拦截，用户未登录")));
                }
            }else{
                if(StringUtils.equals(className,"ProductManageController") &&
                        StringUtils.equals(methodName,"richtextImgUpload")){//对富文本上传的单独处理
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","无权限操作");
                    out.print(JsonUtil.obj2String(resultMap));
                }else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("用户无权限操作")));
                }
            }
            out.flush();
            out.close();//这里要先flush清空数据，然后在close掉
            return false;
        }
            return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse, Object o,
                           ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse, Object o,
                                Exception e) throws Exception {
        log.info("afterCompletion");
    }
}
