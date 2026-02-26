package com.example.smarttourism.controller;

import com.example.smarttourism.entity.Orders;
import com.example.smarttourism.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrdersController {

    @Autowired
    private OrdersRepository ordersRepository;

    // 🔥 核心修改：去掉了 "/create"，现在路径直接是 POST /api/orders，完美匹配前端代码！
    @PostMapping
    public Map<String, Object> createOrder(@RequestBody Orders order) {
        Map<String, Object> res = new HashMap<>();

        if (order.getUserName() == null || order.getUserName().isEmpty()) {
            res.put("code", 400);
            res.put("msg", "用户名不能为空");
            return res;
        }

        ordersRepository.save(order);

        res.put("code", 200);
        res.put("msg", "下单成功");
        return res;
    }

    // 2. 查询某个用户的所有订单
    @GetMapping("/user/{userName}")
    public Map<String, Object> getUserOrders(@PathVariable String userName) {
        Map<String, Object> res = new HashMap<>();
        // 注意：这里需要确保你的 OrdersRepository 里确实定义了 findByUserNameOrderByCreateTimeDesc 方法哦
        List<Orders> orders = ordersRepository.findByUserNameOrderByCreateTimeDesc(userName);

        res.put("code", 200);
        res.put("data", orders);
        return res;
    }

    // 3. 管理员查询所有订单
    @GetMapping("/all")
    public Map<String, Object> getAllOrders() {
        Map<String, Object> res = new HashMap<>();
        List<Orders> orders = ordersRepository.findAll();

        res.put("code", 200);
        res.put("data", orders);
        return res;
    }

    // 4. 管理员删除订单
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteOrder(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        try {
            ordersRepository.deleteById(id);
            res.put("code", 200);
            res.put("msg", "删除成功");
        } catch (Exception e) {
            res.put("code", 500);
            res.put("msg", "删除失败，订单不存在");
        }
        return res;
    }

    // 🔥 新增：专门用来把订单状态改成“已支付”的接口
    @PostMapping("/paySuccess")
    public Map<String, Object> paySuccess(@RequestParam String orderNo,
                                          @RequestParam(required = false) String alipayTradeNo) {
        Map<String, Object> res = new HashMap<>();

        Orders order = ordersRepository.findByOrderNo(orderNo);

        if (order != null && "UNPAID".equals(order.getStatus())) {
            order.setStatus("PAID"); // 变成已支付
            if (alipayTradeNo != null) {
                order.setAlipayTradeNo(alipayTradeNo); // 记录支付宝流水号
            }
            order.setPayTime(java.time.LocalDateTime.now()); // 记录时间
            ordersRepository.save(order);

            res.put("code", 200);
            res.put("msg", "订单状态更新成功！");
        } else {
            res.put("code", 400);
            res.put("msg", "订单不存在或已处理");
        }
        return res;
    }
}