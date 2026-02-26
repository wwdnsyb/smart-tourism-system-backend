package com.example.smarttourism.entity;

import lombok.Data;
import javax.persistence.*; // 如果是 Spring Boot 3+，这里可能是 jakarta.persistence
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    // 🔥 新增：密码 (必须有，否则无法登录)
    @Column(nullable = false)
    private String password;

    // 🔥 新增：手机号 (注册时用)
    private String phone;

    // 🔥 新增：角色 (区分 USER 和 ADMIN)
    private String role;

    // 🔥 新增：创建时间 (对应数据库 create_time)
    @Column(name = "create_time")
    private LocalDateTime createTime;

    // 自动填充创建时间
    @PrePersist
    public void prePersist() {
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        if (this.role == null) {
            this.role = "USER"; // 默认角色
        }
    }
    private String email;
    private String avatar;
}