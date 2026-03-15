package com.example.smarttourism.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable; // 🔥 必须导入
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user")
// 🔥 核心点：实现 Serializable 接口，这是 Redis 存储对象的“准入证”
public class User implements Serializable {

    // 🔥 建议显式声明版本号（防止改代码后 Redis 反序列化失败）
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    @Column(nullable = false)
    private String password;

    private String phone;

    private String role;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    private String email;

    private String avatar;

    @PrePersist
    public void prePersist() {
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        if (this.role == null) {
            this.role = "USER";
        }
    }
}