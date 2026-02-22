package com.example.smarttourism.service;

import com.example.smarttourism.entity.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findById(Long id);

    // 新增：声明修改密码的方法
    boolean updatePassword(String username, String oldPassword, String newPassword);
}