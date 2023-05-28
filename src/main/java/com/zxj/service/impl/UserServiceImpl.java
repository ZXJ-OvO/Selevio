package com.zxj.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxj.dto.LoginFormDTO;
import com.zxj.dto.Result;
import com.zxj.dto.UserDTO;
import com.zxj.entity.User;
import com.zxj.mapper.UserMapper;
import com.zxj.service.IUserService;
import com.zxj.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.zxj.utils.SystemConstants.USER_NICK_NAME_PREFIX;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    /**
     * send phone verification code
     */
    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 1. verify phone number
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2. if phone number is invalid, send error message
            return Result.fail("手机号格式错误！");
        }

        // 3. if phone number is valid, generator verification code
        String code = RandomUtil.randomNumbers(6);

        // 4. save verification code to session
        session.setAttribute("code", code);

        // 5. send verification code to phone number
        // TODO imitate sending verification code to phone number by SMS service provider such as Aliyun
        log.debug("发送短信验证码成功，验证码{}", code);

        // 6. return success message
        return Result.ok();

    }

    /**
     * login
     */
    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        // 1. verify phone number
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2. if phone number is invalid, send error message
            return Result.fail("手机号格式错误！");
        }

        // 2. verify verification code
        Object cacheCode = session.getAttribute("code");
        String code = loginForm.getCode();
        if (cacheCode == null || !cacheCode.toString().equals(code)) {
            // 3. mismatch, return error message
            return Result.fail("验证码错误！");
        }

        // 4. match, select * from tb_user where phone = ?
        User user = query().eq("phone", phone).one();

        // 5. judge whether the user is existing
        if (user == null) {
            // 6. if user is not existing, create the new user and save to database
            user = createUserWithPhone(phone);
        }

        // 7. save user information to session
        session.setAttribute("user", BeanUtil.copyProperties(user, UserDTO.class));

        /*
            The principle of session is the same as that of cookie.
            Each session has its own unique id, which is automatically written into the cookie after making a request.
            So there is no need to return the login certificate.
         */
        return Result.ok();
    }

    /**
     * create user with phone number
     */
    private User createUserWithPhone(String phone) {
        // 1. create user object
        User user = new User();
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));

        // 2. save user to database
        save(user);

        return user;
    }
}
