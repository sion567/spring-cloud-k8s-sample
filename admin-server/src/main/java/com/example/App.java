package com.example;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAdminServer
@SpringBootApplication
// admin-server 有 RBAC 权限，会主动调用 K8s API 扫描集群中所有的 Pod
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
