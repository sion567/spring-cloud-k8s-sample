package com.example.user.mapper;

import com.example.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    /**
     * 根據用戶名查詢用戶
     * 使用 Optional 可以優雅地處理「用戶不存在」的場景，避免返回 null
     */
    Optional<User> findByUsername(String username);

    /**
     * 檢查用戶名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 根據郵箱查詢（可選）
     */
    Optional<User> findByEmail(String email);
}
