package com.example.smarttourism.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "scenic_spot")
public class ScenicSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // 景点名称

    @Column(name = "image_url") // 对应数据库 image_url
    private String imageUrl;

    private BigDecimal price;   // 🔥 修正：使用 BigDecimal 对应数据库 decimal

    private Double rating;      // 🔥 新增：评分 (数据库里有)

    private String address;     // 🔥 修正：数据库叫 address，不要用 location

    @Column(columnDefinition = "TEXT")
    private String description; // 简介

    @Column(name = "open_time") // 对应数据库 open_time
    private String openTime;

    private String category;    // 🔥 新增：分类 (数据库里有，前端筛选要用)

    // 注意：数据库里没有 content 字段，所以我删掉了，否则会报错
}