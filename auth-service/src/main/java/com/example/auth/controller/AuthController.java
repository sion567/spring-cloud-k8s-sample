package com.example.auth.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> user) {
        if ("admin".equals(user.get("username"))) {
            return "dummy-jwt-token-for-" + user.get("username");
        }
        throw new RuntimeException("Auth Failed");
    }

    @GetMapping("/validate")
    public boolean validate(@RequestParam("token") String token) {
        return token != null && token.startsWith("dummy-jwt-token");
    }
}