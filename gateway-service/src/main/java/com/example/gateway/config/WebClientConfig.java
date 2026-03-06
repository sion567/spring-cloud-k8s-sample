package com.example.gateway.config;

import com.example.client.AuthClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced // 关键：允许 WebClient 使用 lb://auth-service 这种格式
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }


    @Bean
    public AuthClient authClient(WebClient.Builder builder) {
        // 1. 构建指向服务名的 WebClient
        WebClient client = builder.baseUrl("http://auth-service").build();

        // 2. 创建适配器并生成代理工厂 (Spring Boot 3.1+ 推荐写法)
        WebClientAdapter adapter = WebClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        // 3. 生成最终的 AuthClient 实例
        return factory.createClient(AuthClient.class);
    }
}
