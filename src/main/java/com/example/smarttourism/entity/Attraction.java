package com.example.smarttourism.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 景点实体（MyBatis-Plus）
 */
@Data
@TableName("attraction")
public class Attraction {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String description;
    private String content;
    private String imageUrl;
    private String location;
    private BigDecimal ticketPrice;
}
