package com.example.smarttourism.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "target_type")
    private String targetType; // 填 ATTRACTION (景点) 或 HOTEL (酒店)

    @Column(name = "user_name")
    private String userName;

    private Double rating;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    // 🔥 新增：用来临时存放查出来的景点/酒店名字
    // @Transient 意思是：这个字段不存数据库，只在 Java 代码运行时作为临时属性使用
    @Transient
    private String targetName;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }
}