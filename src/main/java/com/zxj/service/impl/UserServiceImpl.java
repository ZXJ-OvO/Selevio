package com.zxj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxj.entity.User;
import com.zxj.mapper.UserMapper;
import com.zxj.service.IUserService;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
