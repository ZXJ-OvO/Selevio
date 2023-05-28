package com.zxj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxj.dto.LoginFormDTO;
import com.zxj.dto.Result;
import com.zxj.entity.User;

import javax.servlet.http.HttpSession;


public interface IUserService extends IService<User> {

    /**
     * send phone verification code
     */
    Result sendCode(String phone, HttpSession session);

    /**
     * login
     * @param loginForm login parameters, including phone number, verification code; or phone number, password
     */
    Result login(LoginFormDTO loginForm, HttpSession session);
}
