package com.zxj.service.impl;

import com.zxj.entity.BlogComments;
import com.zxj.mapper.BlogCommentsMapper;
import com.zxj.service.IBlogCommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {

}
