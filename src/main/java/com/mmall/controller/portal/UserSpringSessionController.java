package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//定义为controller
@Controller
//定义为响应
@RequestMapping("/user/springsession")
public class UserSpringSessionController {//增强侵入性
    @Autowired
    private IUserService iUserService;

    /**
     * 登录
     *
     * @param username
     * @param password
     * @param session
     * @return
     */
//    将请求指定为post
    @RequestMapping(value = "login.do", method = RequestMethod.GET)
//     通过jackson插件自动将返回值自动化为json对象
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session,
                                      HttpServletResponse httpServletResponse) {
//        int i = 0;
//        int j = 100/i;
        //service->mybatis->dao
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
//            CookieUtil.writeLoginToken(httpServletResponse, session.getId());
//            RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()), Const.RedisCacheExtime.REDIS_SESSION_TIME);
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 退出登录
     *
     * @param httpServletRequest,httpServletResponse
     * @return
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
//        String loginToken = CookieUtil.readLoginCookie(httpServletRequest);
//        CookieUtil.delLoginCookie(httpServletRequest, httpServletResponse);
//        RedisShardedPoolUtil.del(loginToken);
        session.removeAttribute(Const.CURRENT_USER);//只需将session删除掉
        return ServerResponse.createBySuccess();
    }


    /**
     * 获取用户信息m
     *
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "getUserInfo.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session, HttpServletRequest httpServletRequest) {
//        String loginToken = CookieUtil.readLoginCookie(httpServletRequest);
//        if (StringUtils.isEmpty(loginToken)) {
//            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
//        }
//        String UserJsonStr = RedisShardedPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(UserJsonStr, User.class);
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
    }


}
