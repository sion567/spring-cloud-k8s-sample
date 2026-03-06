package com.example.user.service;

import com.example.common.exception.BusinessException;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用戶ID: " + id + " 不存在", 404));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用戶: " + username + " 不存在", 404));
    }

    public void checkUserExists(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("用戶名已存在", 400);
        }
    }
}
