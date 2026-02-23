package com.example.smarttourism.repository;

import com.example.smarttourism.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 作用：自动生成 SQL "SELECT * FROM user WHERE username = ?"
    User findByUsername(String username);
}