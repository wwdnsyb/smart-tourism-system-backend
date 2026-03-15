package com.example.smarttourism;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching; // 🔥 1. 引入缓存注解包

@SpringBootApplication
@MapperScan("com.example.smarttourism.mapper")
@EnableCaching // 🔥 2. 加上这个注解，正式开启全站 Redis 缓存！
public class SmartTourismApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartTourismApplication.class, args);
    }
}