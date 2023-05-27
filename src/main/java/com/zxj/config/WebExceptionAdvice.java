package com.zxj.config;

import com.zxj.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Web层异常统一处理
 */
@Slf4j
@RestControllerAdvice
public class WebExceptionAdvice {

    /**
     * 处理所有业务异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        log.error(e.toString(), e);
        return Result.fail("服务器异常");
    }
}
