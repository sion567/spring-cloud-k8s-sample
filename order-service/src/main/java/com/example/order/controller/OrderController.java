package com.example.order.controller;

import com.example.dto.OrderResponseDTO;
import com.example.dto.Result;
import com.example.order.entity.Order;
import com.example.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/my-orders")
    public List<Order> getOrders(@RequestHeader("X-User-Id") Long userId) {
        // 注意：网关传来的 Header 是 String，Spring 会自动转为 Long
        return orderService.getUserOrders(userId);
    }

    @GetMapping("/detail/{id}")
    public Result<OrderResponseDTO> getOrderDetail(@PathVariable String id) {
        OrderResponseDTO detail = orderService.getOrderDetail(id);
        return Result.success(detail);
    }

}
