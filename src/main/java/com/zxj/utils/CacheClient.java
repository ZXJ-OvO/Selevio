package com.zxj.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.zxj.utils.RedisConstants.*;

/**
 * Cache three problems to be solved:
 * cache penetration: null
 * cache breakdown: random expire time
 * cache avalanche: lock
 */
@Component
@Slf4j
public class CacheClient {


    private final StringRedisTemplate stringRedisTemplate;

    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * set cache
     */
    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
        // set logical expire time
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));

        // set redis cache
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    /**
     * cache penetration util tool
     * @param keyPrefix cache key prefix
     * @param id cache key data id
     * @param type method return type
     * @param dbFallback db fallback function
     * @param time cache time
     * @param unit cache time unit
     * @return R
     * @param <R> function parameter type
     * @param <ID> function return type
     */
    public <R, ID> R queryWithPassThrough(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {

        String key = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(key);

        if (StrUtil.isNotBlank(json)) {
            return JSONUtil.toBean(json, type);
        }

        if (json != null) {
            return null;
        }

        R r = dbFallback.apply(id);

        if (r == null) {
            stringRedisTemplate.opsForValue().set(
                    CACHE_SHOP_KEY + id,
                    "",
                    CACHE_NULL_TTL + RandomUtil.randomLong(-5, 5),
                    TimeUnit.MINUTES);
            return null;
        }

        this.set(key, r, time, unit);

        return r;

    }

    /**
     * thread pool
     */
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    /**
     * cache breakdown util tool
     * @param keyPrefix cache key prefix
     * @param id cache key data id
     * @param type method return type
     * @param dbFallback db fallback function
     * @param time cache time
     * @param unit cache time unit
     * @return R
     * @param <R> function parameter type
     * @param <ID> function return type
     */
    public <R, ID> R queryWithLogicalExpire(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback,Long time, TimeUnit unit){
        String key = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(key);

        if (StrUtil.isBlank(json)) {
            return null;
        }
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        R r = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        LocalDateTime expireTime = redisData.getExpireTime();
        if(expireTime.isAfter(LocalDateTime.now())){
            return r;
        }
        String lockKey = LOCK_SHOP_KEY + id;
        boolean isLock = tryLock(lockKey);
        if (isLock){
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    R r1 = dbFallback.apply(id);
                    this.setWithLogicalExpire(key, r1, time, unit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    releaseLock(lockKey);
                }
            });
        }
        return r;
    }

    /**
     * get lock
     */
    private boolean tryLock(String key){
        return BooleanUtil.isTrue(stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS));
    }

    /**
     * release lock
     */
    private void releaseLock(String key) {
        stringRedisTemplate.delete(key);
    }

}
