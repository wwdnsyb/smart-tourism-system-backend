package com.example.smarttourism.controller;

import com.example.smarttourism.entity.User;
import com.example.smarttourism.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*") // 允许前端跨域访问
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 1. 注册接口
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> res = new HashMap<>();

        // 先查一下用户名是不是被占用了
        User existUser = userRepository.findByUsername(user.getUsername());
        if (existUser != null) {
            res.put("code", 400);
            res.put("msg", "该用户名已存在");
            return res;
        }

        // 没占用，保存到数据库
        user.setRole("USER"); // 默认为普通用户
        userRepository.save(user);

        res.put("code", 200);
        res.put("msg", "注册成功");
        return res;
    }

    // 2. 登录接口
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> res = new HashMap<>();

        // 去数据库查这个人
        User dbUser = userRepository.findByUsername(user.getUsername());

        // 如果查不到，或者密码不对
        if (dbUser == null || !dbUser.getPassword().equals(user.getPassword())) {
            res.put("code", 400);
            res.put("msg", "用户名或密码错误");
            return res;
        }

        // 登录成功
        res.put("code", 200);
        res.put("msg", "登录成功");
        res.put("data", dbUser); // 把用户信息返回给前端保存
        return res;
    }

    // ---------------------------------------------------------
    // 🔥 3. 新增：修改密码接口
    // ---------------------------------------------------------
// ---------------------------------------------------------
    // 🔥 修改密码接口：增加三重校验逻辑
    // ---------------------------------------------------------
    @PutMapping("/password")
    public Map<String, Object> updatePassword(@RequestBody Map<String, String> params) {
        Map<String, Object> res = new HashMap<>();

        String username = params.get("username");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        // 1. 非空检查
        if (username == null || oldPassword == null || newPassword == null || oldPassword.isEmpty()) {
            res.put("code", 400);
            res.put("msg", "参数不完整");
            return res;
        }

        // 2. 根据用户名查询数据库中“最真实”的那个用户
        User dbUser = userRepository.findByUsername(username);

        if (dbUser == null) {
            res.put("code", 400);
            res.put("msg", "用户不存在");
            return res;
        }

        // 🚨 核心 Bug 修复点：强制对比数据库密码和用户输入的旧密码
        // 注意：这里用 .equals()，必须完全一致
        if (!dbUser.getPassword().equals(oldPassword)) {
            System.out.println("密码校验失败！数据库中是: " + dbUser.getPassword() + "，你输入的是: " + oldPassword);
            res.put("code", 400);
            res.put("msg", "原密码输入错误，修改失败！");
            return res;
        }

        // 3. 校验通过，设置新密码并强制刷入数据库
        dbUser.setPassword(newPassword);
        userRepository.saveAndFlush(dbUser); // 🚨 关键：saveAndFlush 保证立即写进磁盘

        res.put("code", 200);
        res.put("msg", "密码修改成功，请重新登录");
        return res;
    }

    // 🔥 真实修改个人资料接口 (同步强化版)
    @PutMapping("/profile")
    public Map<String, Object> updateProfile(@RequestBody User updatedUser) {
        Map<String, Object> res = new HashMap<>();

        // 1. 验证 ID
        if (updatedUser.getId() == null) {
            res.put("code", 400);
            res.put("msg", "更新失败：缺少用户 ID");
            return res;
        }

        // 2. 从数据库查询最原始的、完整的用户信息（包含密码）
        java.util.Optional<User> optionalUser = userRepository.findById(updatedUser.getId());

        if (optionalUser.isPresent()) {
            User dbUser = optionalUser.get();

            // 🚨 关键：我们只修改名字、邮箱、手机和头像
            // 绝不执行 dbUser.setPassword(...)，这样数据库里的原密码就能稳稳保住！

            // 3. 改名重复校验
            if (!dbUser.getUsername().equals(updatedUser.getUsername())) {
                User existUser = userRepository.findByUsername(updatedUser.getUsername());
                if (existUser != null) {
                    res.put("code", 400);
                    res.put("msg", "用户名已被占用");
                    return res;
                }
            }

            // 4. 只覆盖这四个字段
            dbUser.setUsername(updatedUser.getUsername());
            dbUser.setEmail(updatedUser.getEmail());
            dbUser.setPhone(updatedUser.getPhone());

            // 过滤掉带 random 的头像
            String newAvatar = updatedUser.getAvatar();
            if (newAvatar != null && newAvatar.contains("random")) {
                dbUser.setAvatar(null);
            } else {
                dbUser.setAvatar(newAvatar);
            }

            // 5. 保存回数据库（由于 dbUser 是从数据库查出来的，它的 password 字段还是原来的值）
            User savedUser = userRepository.save(dbUser);

            res.put("code", 200);
            res.put("msg", "资料更新成功");
            res.put("data", savedUser); // 把包含原密码的完整对象给前端，防止前端后续报错
        } else {
            res.put("code", 400);
            res.put("msg", "找不到该用户");
        }
        return res;
    }
}