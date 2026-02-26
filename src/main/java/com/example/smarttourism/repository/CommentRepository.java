package com.example.smarttourism.repository; // ⚠️ 注意核对你的包名

import com.example.smarttourism.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 🔥 神奇的 Spring Data JPA：按照目标ID和类型查找评价，并按时间倒序排（最新的在最上面）
    List<Comment> findByTargetIdAndTargetTypeOrderByCreateTimeDesc(Long targetId, String targetType);
}