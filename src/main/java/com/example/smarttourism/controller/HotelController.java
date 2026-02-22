package com.example.smarttourism.controller;

import com.example.smarttourism.entity.Hotel;
import com.example.smarttourism.entity.Orders;
import com.example.smarttourism.repository.HotelRepository;
import com.example.smarttourism.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@CrossOrigin("*") // 允许前端跨域访问
public class HotelController {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    // 1. 获取所有酒店列表
    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    // 2. 获取单个酒店详情
    @GetMapping("/{id}")
    public Hotel getHotelById(@PathVariable Long id) {
        return hotelRepository.findById(id).orElse(null);
    }

    // 3. 提交订单 (预订)
    @PostMapping("/book")
    public String createOrder(@RequestBody Orders order) {
        // 这里简单处理：直接保存即视为下单成功
        // 实际项目中可以加库存校验
        ordersRepository.save(order);
        return "预订成功";
    }

    // 4. 获取我的订单 (按手机号查，为了毕设演示简单点)
    // 前端传 ?phone=13800000000
    // 你需要在 Repository 里加一个 findByPhone 方法，这里先略过，后面需要再加
}