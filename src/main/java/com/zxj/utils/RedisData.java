package com.zxj.utils;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * logic expired time
 */
@Data
public class RedisData {
    private LocalDateTime expireTime;
    private Object data;
}
