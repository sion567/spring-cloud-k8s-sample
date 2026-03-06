package com.example.gateway.filter;

import com.example.client.AuthClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    // 实际开发中建议放在 K8s Secret 或 ConfigMap 中
    private final String SECRET = "YourSuperSecretKeyForJWTEncryption123456";

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private AuthClient authClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
       return filterV3(exchange, chain);
    }

    @Override
    public int getOrder() {
        return -100; // 优先级最高
    }

    private Mono<Void> filterV1(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取请求头中的 Token
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (token == null || token.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 2. 调用 auth-service 进行校验 (使用 lb:// 服务名)
        return webClientBuilder.build()
                .get()
                .uri("http://auth-service/auth/validate?token=" + token)
                .retrieve()
                .bodyToMono(Boolean.class) // 假设 auth 返回 boolean
                .flatMap(isValid -> {
                    if (isValid) {
                        // 3. 校验通过，继续转发
                        return chain.filter(exchange);
                    } else {
                        // 4. 校验失败，返回 401
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                });
    }

    private Mono<Void> filterV2(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 1. 放行白名单（如登录接口）
        if (path.contains("/api/login")) {
            return chain.filter(exchange);
        }

        // 2. 获取 Token
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            // 3. 校验 JWT
            token = token.substring(7);
            SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

            // 4. 解析 UserId 并增强 Header 传给后端微服务
            String userId = claims.getSubject();
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private Mono<Void> filterV3(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (token == null || token.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 调用响应式代理方法
        return authClient.validate(token)
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return chain.filter(exchange);
                    }
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                })
                .onErrorResume(e -> {
                    // 处理服务不可用等异常
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return exchange.getResponse().setComplete();
                });
    }
}
