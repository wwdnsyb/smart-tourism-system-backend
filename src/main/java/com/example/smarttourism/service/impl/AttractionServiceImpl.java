package com.example.smarttourism.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.smarttourism.entity.Attraction;
import com.example.smarttourism.mapper.AttractionMapper;
import com.example.smarttourism.service.AttractionService;
import org.springframework.stereotype.Service;

@Service
public class AttractionServiceImpl implements AttractionService {

    private final AttractionMapper attractionMapper;

    public AttractionServiceImpl(AttractionMapper attractionMapper) {
        this.attractionMapper = attractionMapper;
    }

    @Override
    public IPage<Attraction> pageList(long current, long size) {
        Page<Attraction> page = new Page<>(current, size);
        return attractionMapper.selectPage(page, null);
    }

    @Override
    public Attraction getById(Long id) {
        return attractionMapper.selectById(id);
    }
}
