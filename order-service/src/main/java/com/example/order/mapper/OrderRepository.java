package com.example.order.mapper;

import com.example.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    Optional<Order> findByOrderNo(String orderNo);

    // 3. 查詢金額大於某個值的訂單 (使用 JPQL)
    @Query("SELECT o FROM Order o WHERE o.amount > :minAmount AND o.status = 'PAID'")
    List<Order> findLargePaidOrders(@Param("minAmount") BigDecimal minAmount);

    // 4. 根據狀態統計某個用戶的訂單總金額
    @Query("SELECT SUM(o.amount) FROM Order o WHERE o.userId = :userId AND o.status = :status")
    BigDecimal sumAmountByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
}
