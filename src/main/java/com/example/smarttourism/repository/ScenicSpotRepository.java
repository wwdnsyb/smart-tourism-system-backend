package com.example.smarttourism.repository;

import com.example.smarttourism.entity.ScenicSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScenicSpotRepository extends JpaRepository<ScenicSpot, Long> {

    // 1. 根据名字查景点（用来匹配订单里的项目名）
    ScenicSpot findByName(String name);

    // 2. 冷启动推荐：查询全站评分最高的 4 个景点
    List<ScenicSpot> findTop4ByOrderByRatingDesc();

    // 3. 相似推荐：查询同分类、且排除掉刚才去过的那个，按评分降序查 4 个
    List<ScenicSpot> findTop4ByCategoryAndNameNotOrderByRatingDesc(String category, String name);
}