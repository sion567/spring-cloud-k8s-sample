package com.example.client;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

public interface AuthClient {
    /**
     * 调用 auth-service 的校验接口
     * 返回 Mono<Boolean> 符合 Gateway 的非阻塞要求
     */
    @GetExchange("/auth/validate")
    Mono<Boolean> validate(@RequestParam("token") String token);
}
