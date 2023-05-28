package com.zxj.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxj.dto.Result;
import com.zxj.entity.User;
import com.zxj.mapper.UserMapper;
import com.zxj.service.IUserService;
import com.zxj.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

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
}
