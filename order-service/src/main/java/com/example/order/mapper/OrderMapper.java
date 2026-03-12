package com.example.order.mapper;

import com.example.dto.OrderResponseDTO;
import com.example.order.entity.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponseDTO toDTO(Order order);
    List<OrderResponseDTO> oDTOList(List<Order> list);
}
