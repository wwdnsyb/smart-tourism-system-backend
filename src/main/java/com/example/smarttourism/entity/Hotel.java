package com.example.smarttourism.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "hotel")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
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