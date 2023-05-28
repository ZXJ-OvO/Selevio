package com.zxj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxj.dto.Result;
import com.zxj.entity.User;

import javax.servlet.http.HttpSession;


public interface IUserService extends IService<User> {

    /**
     * send phone verification code
     */
    Result sendCode(String phone, HttpSession session);
}
