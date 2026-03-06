package com.example.order.controller;

import com.example.common.model.dto.PageReq;
import com.example.common.model.dto.PageRes;
import com.example.dto.OrderResponseDTO;
import com.example.dto.Result;
import com.example.order.entity.Order;
import com.example.order.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders") // 1. 加入版本号，遵循 RESTful 规范
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    /**
     * 获取当前用户的订单列表（支持分页）
     * 优秀点：语义化路径、强制分页、统一 DTO、Header 校验
     */
    @GetMapping("/me")
    public Result<PageRes<OrderResponseDTO>> getMyOrders(
            @RequestHeader(name = "X-User-Id", required = true) Long userId,
            @Valid PageReq pageRequest) { // 包装分页参数：page, size

        return Result.success(orderService.getUserOrders(userId, pageRequest));
    }

    /**
     * 获取订单详情
     * 优秀点：路径变量校验、更明确的命名
     */
    @GetMapping("/{orderId}") // 路径直接用 ID，不用写 /detail
    public Result<OrderResponseDTO> getOrderDetail(
            @PathVariable @NotBlank(message = "订单ID不能为空") String orderId) {

        return Result.success(orderService.getOrderDetail(orderId));
    }

}
