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
import static com.zxj.utils.RedisConstants.CACHE_SHOP_KEY;

@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

/*
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    *//**
     * query shop type from redis
     *//*
    @Override
    public Result queryShopTypes() {
        // 1. Query shop type cache from Redis
        List<String> shopTypeList = stringRedisTemplate.opsForList().range(CACHE_SHOPTYPE_KEY,0,-1);

        // 2. Check if the cache exists
        if (!shopTypeList.isEmpty()) {
            // 3. If the cache exists, return the cached data
            List<ShopType> typeList = new ArrayList<>();
            for (String s : shopTypeList) {
                typeList.add(JSONUtil.toBean(s, ShopType.class));
            }

            return Result.ok(typeList);
        }

        // 4. If the cache does not exist, query from the database
        List<ShopType> typeList = query().orderByAsc("sort").list();

        // 5. If the database query result is null, return an error message
        if (typeList.isEmpty()) {
            return Result.fail("No data found");

        }

        // 6. If the database query result is not null, write the result to Redis
        for (ShopType shopType : typeList) {
            String s = JSONUtil.toJsonStr(shopType);
            shopTypeList.add(s);
        }
        stringRedisTemplate.opsForList().rightPushAll(CACHE_SHOP_KEY, shopTypeList);

        // 6. Return the result
        return Result.ok(typeList);

    }*/

}
