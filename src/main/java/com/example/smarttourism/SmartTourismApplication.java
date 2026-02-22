package com.example.smarttourism;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.smarttourism.mapper")
public class SmartTourismApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartTourismApplication.class, args);
    }
}

