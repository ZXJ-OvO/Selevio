package com.zxj.service.impl;

import com.zxj.entity.Blog;
import com.zxj.mapper.BlogMapper;
import com.zxj.service.IBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

}
