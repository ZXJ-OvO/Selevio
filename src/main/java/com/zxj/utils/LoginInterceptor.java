package com.zxj.utils;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Login interceptor
 */
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * preHandle method is executed before the controller method is executed
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. estimate whether needing to intercept the request (the local thread whether existing user information)
        if (UserHolder.getUser() == null) {
            // 2. none, intercept and set the response status code to 401
            response.setStatus(401);

            // 3. intercept
            return false;
        }

        // 4. existing, release the request
        return true;
    }

}
