package com.example.order.service;

import com.example.client.UserClient;
import com.example.common.exception.BusinessException;
import com.example.dto.OrderResponseDTO;
import com.example.dto.PageResult;
import com.example.dto.Result;
import com.example.dto.UserDTO;
import com.example.order.entity.Order;
import com.example.order.mapper.OrderMapper;
import com.example.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserClient userClient;

    public OrderResponseDTO getOrderDetail(String orderId) {
        // 1. 从本地数据库查询订单基本信息
        Order order = orderRepository.findByOrderNo(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在", 404));
        // 2. 远程调用 user-service 获取用户信息
        String username = "未知用户";
        try {
            Result<UserDTO> userResult = userClient.getUser(order.getUserId());
            if (userResult != null && userResult.getCode() == 200) {
                username = userResult.getData().getUsername();
            }
        } catch (Exception e) {
            log.error("调用 user-service 失败: {}", e.getMessage());
            // 这里可以做降级处理，比如返回默认用户名
        }

        // 3. 组装并返回 DTO
        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderNo(order.getOrderNo());
        response.setAmount(order.getAmount());
        response.setStatus(order.getStatus());
        response.setCreateTime(order.getCreateTime());
        response.setUsername(username); // 聚合进来的数据

        return response;
    }


    public void createOrder(Long userId) {
        Result<UserDTO> result = userClient.getUser(userId);
        UserDTO user = result.getData();
        // 处理后续逻辑...
    }

    public PageResult<OrderResponseDTO> getUserOrders(Long userId, PageRequest request) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);
        Page<OrderResponseDTO> dtoPage = orderPage.map(orderMapper::toDTO);
        return PageResult.of(dtoPage);


//        List<OrderResponseDTO> dtoList = orderMapper.toDTOList(orderPage.getContent());
//
//        return new PageResult<>(
//                dtoList,
//                orderPage.getTotalElements(), // 总记录数
//                request.getPage(),            // 当前页（原样返回前端传的值）
//                request.getSize(),            // 每页大小
//                (long) orderPage.getTotalPages() // 总页数
//        );
    }

}