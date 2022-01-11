package com.bhnote.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * feign远程调用时，通过拦截器封装请求头
 *
 * @author bingo
 * @date 2022/1/6
 */
@Configuration
public class RequestInterceptorConfig {

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor() {
        return template -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                template.header("token", request.getHeader("token"));
            }
        };
    }
}
