package com.example.smarttourism.entity;

// 🔥 1. 这里新增了 Jackson 的注解包，用来处理 JSON 和 Java 对象之间的日期转换
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hotel_id")
    private Long hotelId;

    @Column(name = "hotel_name")
    private String hotelName;

    @Column(name = "user_name")
    private String userName;

    private String phone;

    // 🔥 2. 核心修改：明确告诉 Spring Boot，前端传过来的是 "yyyy-MM-dd" 格式的字符串
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "check_in")
    private LocalDate checkIn;

    // 🔥 3. 核心修改：同样处理离店时间
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "check_out")
    private LocalDate checkOut;

    private BigDecimal amount;

    private String status; // PAID, CANCELLED

    // 🔥 4. 顺手优化：规范订单创建时间的格式（精确到秒）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    // 自动填充创建时间和默认状态
// 🔥 把之前的默认 PAID 改成 UNPAID
    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        if (this.status == null) {
            this.status = "UNPAID"; // 真正变成待支付！
        }
    }

    // 🔥 加上字段映射，确保不出错
    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "alipay_trade_no")
    private String alipayTradeNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "pay_time")
    private LocalDateTime payTime;
}
