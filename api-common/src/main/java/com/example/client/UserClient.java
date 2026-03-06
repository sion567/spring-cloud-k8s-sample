package com.example.client;

import com.example.dto.Result;
import com.example.dto.UserDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/api/users")
public interface UserClient {
    @GetExchange("/{id}")
    Result<UserDTO> getUser(@PathVariable Long id);
}