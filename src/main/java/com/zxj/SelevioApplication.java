package com.zxj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.zxj.mapper")
@SpringBootApplication
public class SelevioApplication {

    public static void main(String[] args) {
        SpringApplication.run(SelevioApplication.class, args);
    }

}
/*
    principal:Choose the appropriate data type and take into account the valid duration.

    login sms code: String  key value is simple string
    login user info: Hash   key value is complex object

    double interceptor

    cache

 */