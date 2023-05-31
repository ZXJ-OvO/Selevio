package com.zxj.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.zxj.dto.UserDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.zxj.utils.RedisConstants.LOGIN_USER_KEY;
import static com.zxj.utils.RedisConstants.LOGIN_USER_TTL;

/**
 * this interceptor is used to refresh token, In logic level before the LoginInterceptor
 */
public class RefreshTokenInterceptor implements HandlerInterceptor {

    /**
     * The interceptor is executed before the Spring container is initialized,
     * so it must not be managed by Spring,
     * so autowiring cannot be used, and the constructor needs to be used for injection.
     */
    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * preHandle method is executed before the controller method is executed
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. Get the token in the request header
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            return true;
        }

        // 2. Get users in redis based on tokens
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(LOGIN_USER_KEY + token);

        // 3. check if user is existing
        if (userMap.isEmpty()) {
            return true;
        }

        // 4. Convert query Hash data to UserDto object
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);

        // 5. if user is existing, save user information to ThreadLocal
        UserHolder.saveUser(userDTO);

        // 6. refresh token expiration time
        stringRedisTemplate.expire(LOGIN_USER_KEY + token,
                LOGIN_USER_TTL + RandomUtil.randomLong(-5,5),
                TimeUnit.MINUTES);

        // 7. release the request
        return true;
    }

    /**
     * afterCompletion method is executed after the rendering is completed and before the view is returned to the client
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // remove user information from ThreadLocal
        UserHolder.removeUser();
    }
}
