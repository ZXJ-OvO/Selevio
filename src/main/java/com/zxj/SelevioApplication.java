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
