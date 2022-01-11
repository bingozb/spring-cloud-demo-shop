package com.bhnote.config;

import com.bhnote.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author bingo
 * @date 2022/1/6
 */
@Configuration
@Slf4j
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns(
                        "/api/user/*/**",
                        "/api/address/*/**"
                )
                .excludePathPatterns(
                        "/api/user/*/register",
                        "/api/user/*/login"
                );
    }
}