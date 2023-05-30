package com.zxj.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxj.dto.Result;
import com.zxj.entity.ShopType;
import com.zxj.mapper.ShopTypeMapper;
import com.zxj.service.IShopTypeService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.zxj.utils.RedisConstants.CACHE_SHOPTYPE_KEY;

@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {


/*    @Resource
    private StringRedisTemplate stringRedisTemplate;

    *//**
     * query shop type from redis
     *//*
    @Override
    public String queryShopTypes() {
        // 1. Query shop type cache from Redis
        String json = stringRedisTemplate.opsForValue().get(CACHE_SHOPTYPE_KEY);

        // 2. Check if the cache exists
        if (json != null) {
            // 3. If the cache exists, return the cached data
            return json;
        }

        // 4. If the cache does not exist, query from the database
        List typeList = list();
        String jsonStr = JSONUtil.toJsonStr(typeList);

        // 5. If the database query result is not null, write the result to Redis
        if (jsonStr != null) {
            stringRedisTemplate.opsForValue().set(CACHE_SHOPTYPE_KEY, jsonStr);
        }

        // 6. Return the result
        return jsonStr;

    }*/

}
