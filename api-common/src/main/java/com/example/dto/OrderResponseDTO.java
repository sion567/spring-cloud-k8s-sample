package com.example.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponseDTO {
    private String orderNo;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createTime;

    // 冗余字段，方便前端直接显示，不需要再查一次用户接口
    private String username;
}
