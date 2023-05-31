package com.zxj.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.zxj.utils.RedisConstants.*;
import static com.zxj.utils.SystemConstants.USER_NICK_NAME_PREFIX;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * send phone verification code
     */
    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 1. verify phone number
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2. if phone number is invalid, send error message
            return Result.fail("Invalid phone number format!");
        }

        // 3. if phone number is valid, generator verification code
        String code = RandomUtil.randomNumbers(6);

        // 4. save verification code to redis, key is phone number, value is verification code
        stringRedisTemplate.opsForValue().set(
                LOGIN_CODE_KEY + phone, code,
                LOGIN_CODE_TTL + RandomUtil.randomLong(-5,5),
                TimeUnit.MINUTES);

        // 5. send verification code to phone number
        // TODO imitate sending verification code to phone number by SMS service provider such as Aliyun
        log.debug("SMS verification code sent successfully, code: {}", code);

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
            return Result.fail("Invalid phone number format!");
        }

        // 3. verify verification code
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        String code = loginForm.getCode();
        if (cacheCode == null || !cacheCode.equals(code)) {
            // 4. mismatch, return error message
            return Result.fail("Verification code error!");
        }

        // 5. match, select * from tb_user where phone = ?
        User user = query().eq("phone", phone).one();

        // 6. judge whether the user is existing
        if (user == null) {
            // 7. if user is not existing, create the new user and save to database
            user = createUserWithPhone(phone);
        }

        // 8. save user information to redis todo

        // 8.1 generate random token as login certificate
        String token = UUID.randomUUID().toString(true);

        // 8.2 Convert user object to HashMap type
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(), CopyOptions.create().
                setIgnoreNullValue(true).
                setFieldValueEditor((fieldName, fieldValue) ->fieldValue.toString()
                ));

        // 8.3 store into redis
        String tokenKey = LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);

        // 8.4 set expiration time
        stringRedisTemplate.expire(tokenKey
                , LOGIN_USER_TTL + RandomUtil.randomLong(-5,5)
                , TimeUnit.MINUTES);

        // 9. return success message
        return Result.ok(token);
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
