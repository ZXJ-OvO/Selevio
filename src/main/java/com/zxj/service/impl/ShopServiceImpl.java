package com.zxj.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.zxj.dto.Result;
import com.zxj.entity.Shop;
import com.zxj.mapper.ShopMapper;
import com.zxj.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;

import static com.zxj.utils.RedisConstants.*;


@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * query shop by id
     */
    @Override
    public Result queryById(Long id) {
        // Cache Penetration : add null value cache target
        // Shop shop = queryWithPassThrough(id);

        // Cache Breakdown : add mutex lock
        Shop shop = queryWithMutex(id);
        if (shop == null){
            return Result.fail("shop does not exist!");
        }

        // 8. return the result
        return Result.ok(shop);
    }

    /**
     * query shop by id solve cache mutex
     */
    public  Shop queryWithMutex(Long id){
        // 1. query shop cache from redis
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);

        // 2. estimate whether the cache exists
        if (StrUtil.isNotBlank(shopJson)) {
            // 3. cache exists, return the cache
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return shop;
        }

        // 4. estimate whether hit the null value cache target
        if (shopJson != null) {
            // 5. hit the null value cache target, return error message
            return null;
        }

        String lockKey = "lock:shop" + id;
        Shop shop = null;
        try {
            // 6. rebuild cache
            // 6.1 get mutex lock
            boolean isLock = tryLock(lockKey);

            // 6.2 estimate whether get the lock successfully
            if (!isLock) {
                // 6.3 fail to get the lock, sleep and retry
                Thread.sleep(50);
                return queryWithMutex(id);
            }

            // 6.4 success to get the lock
            // 6.5 check shop cache from redis again
            String recheck = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);
            if (StrUtil.isNotBlank(recheck)) {
                // 3. cache exists, return the cache
                shop = JSONUtil.toBean(recheck, Shop.class);
                return shop;
            }
            // 6.6 no cache, query from database
            shop = getById(id);
            Thread.sleep(200);

            // 7. if the database query result is null, return error message
            if (shop == null) {
                // 8. write the null value to redis
                stringRedisTemplate.opsForValue().set(
                        CACHE_SHOP_KEY + id,
                        "",
                        CACHE_NULL_TTL + RandomUtil.randomLong(-5, 5),
                        TimeUnit.MINUTES);
                return null;
            }

            // 9. if the database query result exists, write the result to redis
            stringRedisTemplate.opsForValue().set(
                    CACHE_SHOP_KEY + id,
                    JSONUtil.toJsonStr(shop),
                    CACHE_SHOP_TTL + RandomUtil.randomLong(-5,5),
                    TimeUnit.MINUTES);
        }catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            // 10. release the mutex lock
            releaseLock(lockKey);
        }

        // 11. return the result
        return shop;

    }

    /**
     * query shop by id solve cache penetration
     */
    public  Shop queryWithPassThrough(Long id){
        // 1. query shop cache from redis
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);

        // 2. estimate whether the cache exists
        if (StrUtil.isNotBlank(shopJson)) {
            // 3. cache exists, return the cache
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return shop;
        }

        // 4. estimate whether hit the null value cache target
        if (shopJson != null) {
            // 5. hit the null value cache target, return error message
            return null;
        }

        // 6. cache does not exist, query from database
        Shop shop = getById(id);

        // 7. if the database query result is null, return error message
        if (shop == null) {
            // 8. write the null value to redis
            stringRedisTemplate.opsForValue().set(
                    CACHE_SHOP_KEY + id,
                    "",
                    CACHE_NULL_TTL + RandomUtil.randomLong(-5, 5),
                    TimeUnit.MINUTES);
            return null;
        }

        // 9. if the database query result exists, write the result to redis
        stringRedisTemplate.opsForValue().set(
                CACHE_SHOP_KEY + id,
                JSONUtil.toJsonStr(shop),
                CACHE_SHOP_TTL + RandomUtil.randomLong(-5,5),
                TimeUnit.MINUTES);

        // 10. return the result
        return shop;

    }

    /**
     * update shop info
     */
    @Override
    @Transactional
    public Result update(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("shop id is null");
        }

        // 1. update shop info in database
        updateById(shop);

        // 2. delete shop cache in redis
        stringRedisTemplate.delete(CACHE_SHOP_KEY + id);

        return Result.ok();
    }

    /**
     * get lock
     */
    private boolean tryLock(String key){
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    /**
     * release lock
     */
    private void releaseLock(String key) {
        stringRedisTemplate.delete(key);
    }
}
