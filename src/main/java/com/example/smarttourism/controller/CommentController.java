package com.example.smarttourism.controller;

import com.example.smarttourism.entity.Comment;
import com.example.smarttourism.entity.Hotel;
import com.example.smarttourism.entity.ScenicSpot;
import com.example.smarttourism.repository.CommentRepository;
import com.example.smarttourism.repository.HotelRepository;
import com.example.smarttourism.repository.ScenicSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin("*")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    // 🔥 新增：注入景点和酒店的数据库操作对象
    @Autowired
    private ScenicSpotRepository scenicSpotRepository;

    @Autowired
    private HotelRepository hotelRepository;

    // 1. 发表评价接口
    @PostMapping
    public Map<String, Object> addComment(@RequestBody Comment comment) {
        Map<String, Object> res = new HashMap<>();
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            res.put("code", 400);
            res.put("msg", "评价内容不能为空");
            return res;
        }
        commentRepository.save(comment);
        res.put("code", 200);
        res.put("msg", "评价发布成功！");
        return res;
    }

    // 2. 获取某个商品的所有评价接口
    @GetMapping("/{type}/{id}")
    public Map<String, Object> getComments(@PathVariable String type, @PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        List<Comment> comments = commentRepository.findByTargetIdAndTargetTypeOrderByCreateTimeDesc(id, type);
        res.put("code", 200);
        res.put("data", comments);
        return res;
    }

    // 🔥 3. 管理员接口：获取全站所有的评价（带商品名称实名制）
    @GetMapping("/all")
    public Map<String, Object> getAllComments() {
        Map<String, Object> res = new HashMap<>();
        List<Comment> comments = commentRepository.findAll();

        // 🚀 核心逻辑：遍历每一条评价，根据 ID 去查真实的名字
        for (Comment c : comments) {
            String name = "未知商品";
            if ("ATTRACTION".equals(c.getTargetType())) {
                // 如果是景点，去景点表查
                Optional<ScenicSpot> spot = scenicSpotRepository.findById(c.getTargetId());
                if (spot.isPresent()) {
                    name = spot.get().getName();
                }
            } else if ("HOTEL".equals(c.getTargetType())) {
                // 如果是酒店，去酒店表查
                Optional<Hotel> hotel = hotelRepository.findById(c.getTargetId());
                if (hotel.isPresent()) {
                    name = hotel.get().getName();
                }
            }
            // 把查到的名字塞进临时字段里，传给前端
            c.setTargetName(name);
        }

        res.put("code", 200);
        res.put("data", comments);
        return res;
    }

    // 🔥 4. 管理员接口：强制删除违规评价
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteComment(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        try {
            commentRepository.deleteById(id);
            res.put("code", 200);
            res.put("msg", "违规评价已删除");
        } catch (Exception e) {
            res.put("code", 500);
            res.put("msg", "删除失败，评价可能不存在");
        }
        return res;
    }
}