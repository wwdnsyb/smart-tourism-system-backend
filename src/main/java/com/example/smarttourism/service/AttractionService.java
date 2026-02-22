package com.example.smarttourism.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.smarttourism.entity.Attraction;

/**
 * 景点 Service
 */
public interface AttractionService {

    /**
     * 分页查询景点列表
     *
     * @param current 当前页（从 1 开始）
     * @param size    每页条数
     * @return 分页结果
     */
    IPage<Attraction> pageList(long current, long size);

    /**
     * 根据 ID 查询景点详情
     *
     * @param id 景点 ID
     * @return 景点实体，不存在则为 null
     */
    Attraction getById(Long id);
}
