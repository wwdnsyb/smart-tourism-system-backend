package com.example.smarttourism.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable; // 🔥 1. 导入序列化接口包
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "hotel")
public class Hotel implements Serializable { // 🔥 2. 加上 implements Serializable

    private static final long serialVersionUID = 1L; // 🔥 3. 加上序列化版本号，防止后续修改字段报错

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // 🔥 新增：用来接收前端传来的“五星级/豪华”等星级分类数据
    private String category;

    private String address;
    private BigDecimal price; // 金额建议用 BigDecimal

    // 🔥 重点：这两个字段是地图打点的关键
    private Double latitude;  // 纬度
    private Double longitude; // 经度

    @Column(name = "image_url")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double rating;
}