package com.zxj.config;

import com.zxj.utils.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * MVC configuration
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * add interceptors
     *
     * @param registry Interceptor registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // add login interceptor
        registry.addInterceptor(new LoginInterceptor(stringRedisTemplate))
                // exclude intercept path
                .excludePathPatterns(
                        "/user/login",
                        "/user/code",
                        "/blog/hot",
                        "/upload/**",
                        "/shop-type/**",
                        "/voucher/**",
                        "/shop/**"
                );
    }
}
