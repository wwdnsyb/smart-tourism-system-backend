package com.example.smarttourism.repository;

import com.example.smarttourism.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // 找某人的所有收藏
    List<Favorite> findByUserId(Long userId);

    // 检查某个特定的景点或酒店是否已被收藏
    Favorite findByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, String targetType);

    // 取消收藏（根据类型和ID同时删除）
    void deleteByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, String targetType);
}