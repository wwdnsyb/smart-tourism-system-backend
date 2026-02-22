package com.example.smarttourism.service.impl;

import com.example.smarttourism.entity.User;
import com.example.smarttourism.repository.UserRepository;
import com.example.smarttourism.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // ---------------------------------------------------------
    // 🔥 新增：修改密码的具体业务逻辑
    // ---------------------------------------------------------
    @Override
    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        // 1. 根据用户名从数据库中查询用户
        User user = userRepository.findByUsername(username);

        // 2. 检查用户是否存在
        if (user == null) {
            return false;
        }

        // 3. 校验旧密码是否正确
        if (!user.getPassword().equals(oldPassword)) {
            return false; // 旧密码错误，拒绝修改
        }

        // 4. 校验通过，设置新密码并更新到数据库
        user.setPassword(newPassword);
        userRepository.save(user); // JPA 的 save 包含了更新操作

        return true; // 修改成功
    }
}