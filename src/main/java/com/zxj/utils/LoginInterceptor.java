package com.zxj.utils;

import com.zxj.dto.UserDTO;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Login interceptor
 */
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * preHandle method is executed before the controller method is executed
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. get session
        HttpSession session = request.getSession();

        // 2. get user from session
        Object user = session.getAttribute("user");

        // 3. check if user is existing
        if (user == null) {
            // 4. if user is not existing, intercept the request
            response.setStatus(401);
            return false;
        }

        // 5. if user is existing, save user information to ThreadLocal
        UserHolder.saveUser((UserDTO) user);

        // 6. release the request
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
