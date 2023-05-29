package com.zxj.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.zxj.dto.Result;
import com.zxj.entity.Shop;
import com.zxj.mapper.ShopMapper;
import com.zxj.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.zxj.utils.RedisConstants.CACHE_SHOP_KEY;


@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * query shop by id
     */
    @Override
    public Result queryById(Long id) {
        // 1. query shop cache from redis
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);

        // 2. estimate whether the cache exists
        if (StrUtil.isNotBlank(shopJson)) {
            // 3. if the cache exists, return the cache
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }

        // 4. if the cache does not exist, query from database
        Shop shop = getById(id);

        // 5. if the database query result is null, return error message
        if (shop == null) {
            return Result.fail("shop does not exist");
        }

        // 6. if the database query result is not null, write the result to redis
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(shop));

        // 7. return the result
        return Result.ok(shop);
    }
}
