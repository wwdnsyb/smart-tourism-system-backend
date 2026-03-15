package com.example.smarttourism.controller;

import com.example.smarttourism.entity.User;
import com.example.smarttourism.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate; // 🔥 注入 Redis

    private static final String USER_CACHE_KEY = "user:profile:";
    private static final ObjectMapper mapper = new ObjectMapper();

    // 1. 注册
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> res = new HashMap<>();
        User existUser = userRepository.findByUsername(user.getUsername());
        if (existUser != null) {
            res.put("code", 400);
            res.put("msg", "该用户名已存在");
            return res;
        }
        user.setRole("USER");
        userRepository.save(user);
        res.put("code", 200);
        res.put("msg", "注册成功");
        return res;
    }

    // 2. 登录 (加入 Redis 缓存)
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> res = new HashMap<>();
        User dbUser = userRepository.findByUsername(user.getUsername());

        if (dbUser == null || !dbUser.getPassword().equals(user.getPassword())) {
            res.put("code", 400);
            res.put("msg", "用户名或密码错误");
            return res;
        }

        // 🔥 登录成功，同步到 Redis
        try {
            String userJson = mapper.writeValueAsString(dbUser);
            redisTemplate.opsForValue().set(USER_CACHE_KEY + dbUser.getId(), userJson, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            System.err.println("Redis 写入失败");
        }

        res.put("code", 200);
        res.put("msg", "登录成功");
        res.put("data", dbUser);
        return res;
    }

    // 3. 修改密码 (强制清理缓存)
    @PutMapping("/password")
    public Map<String, Object> updatePassword(@RequestBody Map<String, String> params) {
        Map<String, Object> res = new HashMap<>();
        String username = params.get("username");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        User dbUser = userRepository.findByUsername(username);
        if (dbUser == null || !dbUser.getPassword().equals(oldPassword)) {
            res.put("code", 400);
            res.put("msg", "原密码错误");
            return res;
        }

        dbUser.setPassword(newPassword);
        userRepository.saveAndFlush(dbUser);

        // 🔥 核心：删除缓存，强制重新登录
        redisTemplate.delete(USER_CACHE_KEY + dbUser.getId());

        res.put("code", 200);
        res.put("msg", "密码修改成功，请重新登录");
        return res;
    }

    // 🔥 4. 修改资料 (加入头像防崩拦截 + 缓存清理)
    @PutMapping("/profile")
    public Map<String, Object> updateProfile(@RequestBody User updatedUser) {
        Map<String, Object> res = new HashMap<>();

        if (updatedUser.getId() == null) {
            res.put("code", 400);
            res.put("msg", "更新失败：缺少用户 ID");
            return res;
        }

        java.util.Optional<User> optionalUser = userRepository.findById(updatedUser.getId());

        if (optionalUser.isPresent()) {
            User dbUser = optionalUser.get();

            // 用户名重复校验
            if (!dbUser.getUsername().equals(updatedUser.getUsername())) {
                User existUser = userRepository.findByUsername(updatedUser.getUsername());
                if (existUser != null) {
                    res.put("code", 400);
                    res.put("msg", "用户名已被占用");
                    return res;
                }
            }

            // 更新字段
            dbUser.setUsername(updatedUser.getUsername());
            dbUser.setEmail(updatedUser.getEmail());
            dbUser.setPhone(updatedUser.getPhone());

            // 🚨 核心逻辑：头像拦截！
            String newAvatar = updatedUser.getAvatar();
            // 如果链接包含 picsum（国外随机图）或 random，或者为空
            if (newAvatar == null || newAvatar.contains("picsum") || newAvatar.contains("random") || newAvatar.isEmpty()) {
                // 强制换成国内稳定的 ElementUI 官方默认头像链接
                dbUser.setAvatar("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");
            } else {
                dbUser.setAvatar(newAvatar);
            }

            User savedUser = userRepository.save(dbUser);

            // 🔥 同步清理 Redis 缓存
            redisTemplate.delete(USER_CACHE_KEY + savedUser.getId());
            System.out.println("Redis 已清理缓存，用户资料已更新：" + savedUser.getUsername());

            res.put("code", 200);
            res.put("msg", "资料更新成功");
            res.put("data", savedUser);
        } else {
            res.put("code", 400);
            res.put("msg", "找不到该用户");
        }
        return res;
    }

    // 5. 后台列表
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 6. 注销用户 (清理缓存)
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        try {
            userRepository.deleteById(id);
            // 🔥 清理 Redis
            redisTemplate.delete(USER_CACHE_KEY + id);
            res.put("code", 200);
            res.put("msg", "用户注销成功");
        } catch (Exception e) {
            res.put("code", 500);
            res.put("msg", "注销失败");
        }
        return res;
    }
}