package com.example.smarttourism.controller;

import com.example.smarttourism.entity.Orders;
import com.example.smarttourism.entity.ScenicSpot;
import com.example.smarttourism.repository.OrdersRepository;
import com.example.smarttourism.repository.ScenicSpotRepository;
import com.example.smarttourism.service.ScenicSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 景点管理控制器
 * 提供标准 RESTful 接口供前端调用
 */
@RestController
@RequestMapping("/api/scenic-spots") // 路径保持你的不变
@CrossOrigin(origins = "*")
public class ScenicSpotController {

    private final ScenicSpotService scenicSpotService;

    // 🔥 新增：注入 Repository 用于推荐算法直接查询
    @Autowired
    private ScenicSpotRepository scenicSpotRepository;
    @Autowired
    private OrdersRepository ordersRepository;

    public ScenicSpotController(ScenicSpotService scenicSpotService) {
        this.scenicSpotService = scenicSpotService;
    }

    /* ==================================================
       保留你原有的标准 CRUD 接口 (完全没有改动)
       ================================================== */
    @GetMapping
    public List<ScenicSpot> list() {
        return scenicSpotService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScenicSpot> get(@PathVariable Long id) {
        return scenicSpotService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ScenicSpot> create(@RequestBody ScenicSpot scenicSpot) {
        ScenicSpot created = scenicSpotService.create(scenicSpot);
        return ResponseEntity.created(URI.create("/api/scenic-spots/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScenicSpot> update(@PathVariable Long id, @RequestBody ScenicSpot scenicSpot) {
        try {
            ScenicSpot updated = scenicSpotService.update(id, scenicSpot);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scenicSpotService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /* ==================================================
       🔥 核心新增：智能推荐算法接口
       ================================================== */
    @GetMapping("/recommend/{userName}")
    public Map<String, Object> getRecommendations(@PathVariable String userName) {
        Map<String, Object> res = new HashMap<>();
        List<ScenicSpot> recommendedList;

        try {
            // 1. 获取该用户的所有历史订单
            List<Orders> userOrders = ordersRepository.findByUserNameOrderByCreateTimeDesc(userName);
            // 2. 【冷启动策略】：新用户没下过单
            if (userOrders == null || userOrders.isEmpty()) {
                recommendedList = scenicSpotRepository.findTop4ByOrderByRatingDesc();
            }
            // 3. 【基于内容的推荐】：老用户，分析历史行为
            else {
                // 取出他最近下单的一个项目名
                String lastVisitedName = userOrders.get(userOrders.size() - 1).getHotelName();
                ScenicSpot lastSpot = scenicSpotRepository.findByName(lastVisitedName);

                if (lastSpot != null && lastSpot.getCategory() != null) {
                    // 推荐同分类的景点，并排除他去过的这个
                    recommendedList = scenicSpotRepository.findTop4ByCategoryAndNameNotOrderByRatingDesc(
                            lastSpot.getCategory(), lastVisitedName
                    );

                    // 如果同类景点不足 4 个，用热门景点兜底补齐
                    if (recommendedList.size() < 4) {
                        List<ScenicSpot> hotList = scenicSpotRepository.findTop4ByOrderByRatingDesc();
                        for(ScenicSpot hot : hotList) {
                            if(recommendedList.size() < 4 && !recommendedList.contains(hot)) {
                                recommendedList.add(hot);
                            }
                        }
                    }
                } else {
                    // 匹配不到详细信息，兜底推荐热门
                    recommendedList = scenicSpotRepository.findTop4ByOrderByRatingDesc();
                }
            }

            res.put("code", 200);
            res.put("data", recommendedList);
            res.put("msg", "智能推荐成功");
        } catch (Exception e) {
            res.put("code", 500);
            res.put("msg", "推荐系统运行异常: " + e.getMessage());
        }

        return res;
    }
}