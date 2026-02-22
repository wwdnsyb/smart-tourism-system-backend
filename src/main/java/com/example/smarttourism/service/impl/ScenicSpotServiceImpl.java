package com.example.smarttourism.service.impl;

import com.example.smarttourism.entity.ScenicSpot;
import com.example.smarttourism.repository.ScenicSpotRepository;
import com.example.smarttourism.service.ScenicSpotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ScenicSpotServiceImpl implements ScenicSpotService {

    private final ScenicSpotRepository scenicSpotRepository;

    public ScenicSpotServiceImpl(ScenicSpotRepository scenicSpotRepository) {
        this.scenicSpotRepository = scenicSpotRepository;
    }

    @Override
    public ScenicSpot create(ScenicSpot scenicSpot) {
        scenicSpot.setId(null);
        return scenicSpotRepository.save(scenicSpot);
    }

    @Transactional
    @Override
    public ScenicSpot update(Long id, ScenicSpot scenicSpot) {
        ScenicSpot existing = scenicSpotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ScenicSpot not found, id=" + id));

        // 更新基本信息
        existing.setName(scenicSpot.getName());
        existing.setImageUrl(scenicSpot.getImageUrl());
        existing.setPrice(scenicSpot.getPrice());
        existing.setDescription(scenicSpot.getDescription());

        // 🔥 修复点 1：location 改成了 address
        existing.setAddress(scenicSpot.getAddress());

        // 🔥 修复点 2：删掉了 content 相关代码 (因为数据库里没这个字段了)
        // existing.setContent(scenicSpot.getContent()); // ❌ 删掉这行

        // 🔥 修复点 3：补上新加的字段 (评分、开放时间、分类)
        existing.setRating(scenicSpot.getRating());
        existing.setOpenTime(scenicSpot.getOpenTime());
        existing.setCategory(scenicSpot.getCategory());

        return scenicSpotRepository.save(existing);
    }
    @Override
    public void deleteById(Long id) {
        scenicSpotRepository.deleteById(id);
    }

    @Override
    public Optional<ScenicSpot> findById(Long id) {
        return scenicSpotRepository.findById(id);
    }

    @Override
    public List<ScenicSpot> findAll() {
        return scenicSpotRepository.findAll();
    }
}

