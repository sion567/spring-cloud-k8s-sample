package com.example.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@Configuration
public class RateLimiterConfig {

    @Value("${app.auth.enabled:true}")
    private boolean authEnabled;
//    public Mono<Void> filter(ServerWebExchange exchange, ...) {
//        if (!authEnabled) return chain.filter(exchange); // 開發環境直接放行
//        // ... 原有的 JWT 校验逻辑
//    }

    @Bean("myRateLimiter")
    @Profile("K8s") // 或者使用 @ConditionalOnProperty(name="limiter.type", havingValue="redis")
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 20); // 令牌桶：每秒10个，容量20
    }

    @Bean("myRateLimiter")
    @Profile("dev")
    @Primary
    public RateLimiter<Object> memoryRateLimiter() {
        // 明确指定泛型为 Map，避免桥接方法冲突
        return new RateLimiter<Object>() {
            @Override
            public Class<Object> getConfigClass() {
                // 返回 Object.class 即可，因为我们不需要自定义配置类
                return Object.class;
            }

            @Override
            public Object newConfig() {
                // 返回一个空对象，防止某些内部逻辑检查配置时报 NPE
                return new Object();
            }

            @Override
            public Mono<Response> isAllowed(String routeId, String id) {
                // 本地开发环境直接放行
                return Mono.just(new Response(true, Collections.emptyMap()));
            }

            @Override
            public Map<String, Object> getConfig() {
                // 返回空配置即可
                return Collections.emptyMap();
            }
        };
    }

    @Bean
    public KeyResolver ipKeyResolver() {
        // 根据客户端 IP 进行限流
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }
}
