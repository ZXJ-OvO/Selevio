package com.zxj.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zxj.dto.Result;
import com.zxj.entity.Shop;
import com.zxj.mapper.ShopMapper;
import com.zxj.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxj.utils.CacheClient;
import com.zxj.utils.RedisData;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.zxj.utils.RedisConstants.*;


@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheClient cacheClient;

    /**
     * query shop by id
     */
    @Override
    public Result queryById(Long id) {
        // Cache Penetration : add null value cache target
        /*        Shop shop = cacheClient.queryWithPassThrough(
                        CACHE_SHOP_KEY,
                        id,
                        Shop.class,
                        this::getById,
                        CACHE_SHOP_TTL,
                        TimeUnit.MINUTES
                );*/

        // Cache Breakdown : add mutex lock
        Shop shop = cacheClient.queryWithLogicalExpire(
                CACHE_SHOP_KEY,
                id,
                Shop.class,
                this::getById,
                CACHE_SHOP_TTL,
                TimeUnit.MINUTES
        );

        if (shop == null){
            return Result.fail("shop does not exist!");
        }

        // 8. return the result
        return Result.ok(shop);
    }

    /*    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);*/

    /*
            public  Shop queryWithLogicalExpire(Long id){
                String key = CACHE_SHOP_KEY + id;
                // 1. query shop cache from redis
                String shopJson = stringRedisTemplate.opsForValue().get(key);

                // 2. estimate whether the cache exists
                if (StrUtil.isBlank(shopJson)) {
                    // 3. cache does not exists, return the null
                    return null;
                }

                // 4. cache exists, parse the cache data to Shop object
                RedisData redisData = JSONUtil.toBean(shopJson, RedisData.class);
                Shop shop = JSONUtil.toBean((JSONObject) redisData.getData(), Shop.class);
                LocalDateTime expireTime = redisData.getExpireTime();
                // 5. estimate whether the cache is expired
                if(expireTime.isAfter(LocalDateTime.now())){
                    // 6. cache is not expired, return the cache
                    return shop;
                }

                // 7. cache is expired, rebuild cache
                // 8. rebuild cache get mutex lock
                String lockKey = LOCK_SHOP_KEY + id;
                boolean isLock = tryLock(lockKey);

                // 9. estimate whether get the lock successfully
                if (isLock){
                    // 11. success to get the lock, open independent thread to rebuild cache
                    // todo double check expire time
                        expireTime = redisData.getExpireTime();
                    if(expireTime.isAfter(LocalDateTime.now())){
                        // 6. cache is not expired, return the cache
                        return shop;
                    }

                    CACHE_REBUILD_EXECUTOR.submit(() -> {
                        try {
                            // rebuild cache
                            this.saveShop2Redis(id,20L);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        } finally {
                            // release lock
                            releaseLock(lockKey);
                        }
                    });
                }

                // 10. fail to get the lock, return the outdated cache

                return shop;

            }
        */

    /**
     * query shop by id solve cache breakdown
     */
    /*    public  Shop queryWithMutex(Long id){
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

        }*/

    /**
     * query shop by id solve cache penetration
     */
    /*
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
    */

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
    /*
        private boolean tryLock(String key){
            Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
            return BooleanUtil.isTrue(flag);
        }
    */

    /**
     * release lock
     */
    /*    private void releaseLock(String key) {
            stringRedisTemplate.delete(key);
        }*/

    /**
     * imtate logic expire
     */
    public void saveShop2Redis(Long id,Long expireSeconds) throws Exception {
        // 1. search shop data;
        Shop shop = getById(id);

        Thread.sleep(200);

        // 2. Encapsulation logic expiration time
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));

        // 3. write to redis
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(redisData));

    }
}
