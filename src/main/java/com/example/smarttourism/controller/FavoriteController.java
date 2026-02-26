package com.example.smarttourism.controller;

import com.example.smarttourism.entity.Favorite;
import com.example.smarttourism.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin("*")
public class FavoriteController {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @GetMapping("/{userId}")
    public Map<String, Object> getMyFavorites(@PathVariable Long userId) {
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("data", favoriteRepository.findByUserId(userId));
        return res;
    }

    @PostMapping("/add")
    public Map<String, Object> addFavorite(@RequestBody Favorite favorite) {
        Map<String, Object> res = new HashMap<>();
        // 🔥 检查时带上类型
        Favorite exist = favoriteRepository.findByUserIdAndTargetIdAndTargetType(
                favorite.getUserId(), favorite.getTargetId(), favorite.getTargetType());

        if (exist != null) {
            res.put("code", 400);
            res.put("msg", "已经在收藏夹里啦");
            return res;
        }
        favoriteRepository.save(favorite);
        res.put("code", 200);
        res.put("msg", "收藏成功");
        return res;
    }

    @DeleteMapping("/remove")
    @Transactional // 🚨 这个注解必须有，否则删不掉
    public Map<String, Object> removeFavorite(
            @RequestParam Long userId,
            @RequestParam Long targetId,
            @RequestParam String targetType) { // 🔥 删除也要区分类型

        favoriteRepository.deleteByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("msg", "已取消收藏");
        return res;
    }
}