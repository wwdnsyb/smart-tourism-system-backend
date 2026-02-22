package com.example.smarttourism.repository;

import com.example.smarttourism.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    // 根据 userName 查询订单，并按创建时间倒序排列
    List<Orders> findByUserNameOrderByCreateTimeDesc(String userName);
}