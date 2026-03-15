package com.example.smarttourism.controller;

import com.example.smarttourism.entity.Hotel;
import com.example.smarttourism.entity.Orders;
import com.example.smarttourism.repository.HotelRepository;
import com.example.smarttourism.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict; // 🔥 引入清除缓存注解
import org.springframework.cache.annotation.Cacheable; // 🔥 引入存入缓存注解
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

    // 1. 获取所有酒店列表 (🔥 加上读取缓存注解)
    @Cacheable(value = "hotelsList")
    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    // 2. 获取单个酒店详情 (保留原有功能)
    @GetMapping("/{id}")
    public Hotel getHotelById(@PathVariable Long id) {
        return hotelRepository.findById(id).orElse(null);
    }

    // 3. 提交订单 (预订) (保留原有功能)
    @PostMapping("/book")
    public String createOrder(@RequestBody Orders order) {
        // 这里简单处理：直接保存即视为下单成功
        // 实际项目中可以加库存校验
        ordersRepository.save(order);
        return "预订成功";
    }

    // =========================================================
    // 🔥 以下为专供管理后台使用的“增、删、改”接口 (加上清除缓存注解)
    // =========================================================

    // 5. 上架新酒店 (🔥 新增酒店后，清空旧缓存)
    @CacheEvict(value = "hotelsList", allEntries = true)
    @PostMapping
    public Hotel addHotel(@RequestBody Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    // 6. 编辑酒店信息 (🔥 修改酒店后，清空旧缓存)
    @CacheEvict(value = "hotelsList", allEntries = true)
    @PutMapping("/{id}")
    public Hotel updateHotel(@PathVariable Long id, @RequestBody Hotel hotel) {
        // 确保要修改的 ID 和路径里的 ID 是一致的
        hotel.setId(id);
        return hotelRepository.save(hotel);
    }

    // 7. 下架酒店 (🔥 删除酒店后，清空旧缓存)
    @CacheEvict(value = "hotelsList", allEntries = true)
    @DeleteMapping("/{id}")
    public String deleteHotel(@PathVariable Long id) {
        hotelRepository.deleteById(id);
        return "酒店下架成功";
    }
}