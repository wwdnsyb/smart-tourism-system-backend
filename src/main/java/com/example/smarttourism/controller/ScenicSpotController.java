package com.example.smarttourism.controller;

import com.example.smarttourism.entity.ScenicSpot;
import com.example.smarttourism.service.ScenicSpotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * 景点管理控制器
 * 提供标准 RESTful 接口供前端调用
 */
@RestController
@RequestMapping("/api/scenic-spots")
@CrossOrigin(origins = "*") // 🔥 核心修改：允许前端跨域访问 (必加！)
public class ScenicSpotController {

    private final ScenicSpotService scenicSpotService;

    // 推荐使用构造器注入，比 @Autowired 更安全
    public ScenicSpotController(ScenicSpotService scenicSpotService) {
        this.scenicSpotService = scenicSpotService;
    }

    /** 查询全部景点 */
    @GetMapping
    public List<ScenicSpot> list() {
        return scenicSpotService.findAll();
    }

    /** 按 ID 查询单个景点 */
    @GetMapping("/{id}")
    public ResponseEntity<ScenicSpot> get(@PathVariable Long id) {
        return scenicSpotService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** 新增景点 */
    @PostMapping
    public ResponseEntity<ScenicSpot> create(@RequestBody ScenicSpot scenicSpot) {
        ScenicSpot created = scenicSpotService.create(scenicSpot);
        // 返回 201 Created 状态码，并在 Header 中包含新资源的 URI
        return ResponseEntity.created(URI.create("/api/scenic-spots/" + created.getId())).body(created);
    }

    /** 更新景点信息 */
    @PutMapping("/{id}")
    public ResponseEntity<ScenicSpot> update(@PathVariable Long id, @RequestBody ScenicSpot scenicSpot) {
        try {
            ScenicSpot updated = scenicSpotService.update(id, scenicSpot);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            // 如果 Service 里抛出找不到 ID 的异常，这里返回 404
            return ResponseEntity.notFound().build();
        }
    }

    /** 删除景点 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scenicSpotService.deleteById(id);
        // 删除成功返回 204 No Content
        return ResponseEntity.noContent().build();
    }
}