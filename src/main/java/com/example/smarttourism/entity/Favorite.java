package com.example.smarttourism.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "favorite")
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    // 🔥 核心修改：不再叫 attractionId，改叫 targetId，因为可能是酒店也可能是景点
    @Column(name = "target_id")
    private Long targetId;

    // 🔥 核心新增：用于区分是酒店还是景点 ("HOTEL" 或 "SPOT")
    @Column(name = "target_type")
    private String targetType;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String image;

    private Double price;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}