package com.zxj.controller;


import com.zxj.dto.LoginFormDTO;
import com.zxj.dto.Result;
import com.zxj.dto.UserDTO;
import com.zxj.entity.User;
import com.zxj.entity.UserInfo;
import com.zxj.service.IUserInfoService;
import com.zxj.service.IUserService;
import com.zxj.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * log in
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private IUserInfoService userInfoService;

    /**
     * send phone verification code
     */
    @PostMapping("code")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session) {
        return userService.sendCode(phone, session);
    }

    /**
     * login
     *
     * @param loginForm login parameters, including phone number, verification code; or phone number, password
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm, HttpSession session) {
        return userService.login(loginForm, session);
    }


    /**
     * 登出功能
     *
     * @return 无
     */
    @PostMapping("/logout")
    public Result logout() {
        // TODO 实现登出功能
        return Result.fail("功能未完成");
    }

    /**
     * get current user information
     */
    @GetMapping("/me")
    public Result me() {
        // LoginInterceptor.preHandle() has already put current user information into UserHolder
        UserDTO user = UserHolder.getUser();
        return Result.ok(user);
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long userId) {
        // 查询详情
        UserInfo info = userInfoService.getById(userId);
        if (info == null) {
            // 没有详情，应该是第一次查看详情
            return Result.ok();
        }
        info.setCreateTime(null);
        info.setUpdateTime(null);
        // 返回
        return Result.ok(info);
    }
}
