package com.zxj.utils;

import com.zxj.dto.UserDTO;
import com.zxj.entity.User;

/**
 * UserHolder provides a ThreadLocal to operate user information
 */
public class UserHolder {

    /**
     * define a static final ThreadLocal object to operate user information
     */
    private static final ThreadLocal<UserDTO> threadLocal = new ThreadLocal<>();

    /**
     * save user information to ThreadLocal
     */
    public static void saveUser(UserDTO user){
        threadLocal.set(user);
    }

    /**
     * get user information from ThreadLocal
     */
    public static UserDTO getUser(){
        return threadLocal.get();
    }

    /**
     * remove user information from ThreadLocal
     */
    public static void removeUser(){
        threadLocal.remove();
    }
}
