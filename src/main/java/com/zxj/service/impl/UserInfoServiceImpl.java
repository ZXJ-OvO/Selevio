package com.zxj.service.impl;

import com.zxj.entity.UserInfo;
import com.zxj.mapper.UserInfoMapper;
import com.zxj.service.IUserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

}
