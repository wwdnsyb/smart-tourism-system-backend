package com.example.smarttourism.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.smarttourism.common.Result;
import com.example.smarttourism.entity.Attraction;
import com.example.smarttourism.service.AttractionService;
import org.springframework.web.bind.annotation.*;

/**
 * 景点 RESTful 接口，路径 /api/attractions，返回统一包裹在 Result 中
 */
@RestController
@RequestMapping("/api/attractions")
public class AttractionController {

    private final AttractionService attractionService;

    public AttractionController(AttractionService attractionService) {
        this.attractionService = attractionService;
    }

    /**
     * 分页查询景点列表
     * GET /api/attractions/list?current=1&size=10
     */
    @GetMapping("/list")
    public Result<IPage<Attraction>> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        IPage<Attraction> page = attractionService.pageList(current, size);
        return Result.success(page);
    }

    /**
     * 根据 ID 查询景点详情
     * GET /api/attractions/{id}
     */
    @GetMapping("/{id}")
    public Result<Attraction> getById(@PathVariable Long id) {
        Attraction attraction = attractionService.getById(id);
        if (attraction == null) {
            return Result.error(404, "景点不存在");
        }
        return Result.success(attraction);
    }
}
