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
    @PutMapping("/password")
    public Map<String, Object> updatePassword(@RequestBody Map<String, String> params) {
        Map<String, Object> res = new HashMap<>();

        // 从前端传来的 JSON 中提取参数
        String username = params.get("username");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        if (username == null || oldPassword == null || newPassword == null) {
            res.put("code", 400);
            res.put("msg", "参数不完整");
            return res;
        }

        // 根据用户名去数据库查人
        User dbUser = userRepository.findByUsername(username);

        // 如果人不存在，或者原密码输入错误
        if (dbUser == null || !dbUser.getPassword().equals(oldPassword)) {
            res.put("code", 400);
            res.put("msg", "原密码错误");
            return res;
        }

        // 验证通过，设置新密码
        dbUser.setPassword(newPassword);
        // JPA 的 save 方法：如果有 ID 存在，执行的就是 Update 更新操作！
        userRepository.save(dbUser);

        res.put("code", 200);
        res.put("msg", "密码修改成功");
        return res;
    }
}