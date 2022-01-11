package com.bhnote.interceptor;

import com.bhnote.enums.BizCodeEnum;
import com.bhnote.model.LoginUser;
import com.bhnote.utils.HttpServletResponseUtil;
import com.bhnote.utils.JsonData;
import com.bhnote.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 *
 * @author bingo
 * @date 2022/1/6
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        String accessToken = request.getHeader("token");
        LoginUser loginUser = JwtUtil.checkToken(accessToken);
        if (loginUser == null) {
            HttpServletResponseUtil.responseJson(response, JsonData.buildError(BizCodeEnum.ACCOUNT_UNLOGIN));
            return false;
        }
        threadLocal.set(loginUser);
        return true;
    }

    @Override
    public void postHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        threadLocal.remove();
    }
}