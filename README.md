## 创建工程

- 创建父目录
```shell
mkdir spring-cloud-k8s-sample && cd spring-cloud-k8s-sample
```
- 初始化 pom.xml
```shell
touch pom.xml
```
- 配置父 pom.xml
- 创建模块
```shell
mvn archetype:generate -DgroupId=com.example -DartifactId=common-lib -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false 
# 创建业务服务 
mvn archetype:generate -DgroupId=com.example -DartifactId=order-service -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
mvn archetype:generate -DgroupId=com.example -DartifactId=user-service -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
mvn archetype:generate -DgroupId=com.example -DartifactId=admin-service -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
# 该模块主要存放 DTO、Feign 接口定义和公共常量，不需要 Spring Boot 运行环境，只需普通 Java 骨架
mvn archetype:generate -DgroupId=com.example -DartifactId=api-common -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
# 该模块是微服务的统一入口，需要 Spring Boot 运行环境。
mvn archetype:generate -DgroupId=com.example -DartifactId=gateway-service -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
mvn archetype:generate -DgroupId=com.example -DartifactId=auth-service -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

## minikube 部署
```shell
# 清除该空间下所有的 Pod、Service、ConfigMap
kubectl delete ns dev
# 创建Namespace (命名空间)
kubectl create namespace dev

# 创建 MySQL 密码 Secret
kubectl create secret generic mysql-secret \
  --from-literal=ROOT_PASSWORD=root
  --from-literal=USER_DB_USER=user_user \
  --from-literal=USER_DB_PASS=UserPass123! \
  --from-literal=ORDER_DB_USER=order_user \
  --from-literal=ORDER_DB_PASS=OrderPass456! \
  -n dev
# 创建 Redis 密码 Secret
kubectl create secret generic redis-secret --from-literal=REDIS_PASSWORD=redis123 -n dev
# 创建 jjwt Secret
kubectl create secret generic jwt-config --from-literal=JWT_SECRET=YourSuperLongAndSecureSecretKey1234567890


# 创建外部配置 (K8s ConfigMap)
kubectl apply -f mysql-init-config.yaml -n dev 

# 部署时指定空间
kubectl apply -f mysql-dev.yaml 
kubectl apply -f redis-dev.yaml

kubectl apply -k ./k8s/infrastructure/mysql # 先启 mysql, redis
kubectl apply -k ./user-service/k8s/ 

# 1. 应用权限和部署（授权：允许 admin-server 所在的 Pod 读取（get/list/watch）当前 Namespace 下的服务信息。）
kubectl apply -f admin-rbac-dev.yaml
kubectl apply -f admin-deployment-dev.yaml
# 2. 获取浏览器访问地址
minikube service admin-server --url


minikube image build --build-arg SERVICE_NAME=order-service --build-arg SERVICE_PATH=order-service -t order-service:v1 .
minikube image build --build-arg SERVICE_NAME=user-service --build-arg SERVICE_PATH=user-service -t user-service:v1 .


# docker build --build-arg SERVICE_NAME=user-service --build-arg SERVICE_PATH=./user-service -t user-service:1.0.0 .
# 内存比例：-XX:MaxRAMPercentage=75.0 会根据 K8s 分配给 Pod 的内存限制（Limit）动态计算堆大小，比硬编码 512m 更具扩展性。
# 优雅停机：在 K8s 中，ENTRYPOINT 必须能够透传信号。使用 sh -c "java ..." 是可行的，但确保 Spring Boot 配置了 server.shutdown=graceful
kubectl apply -f services-all.yaml -n dev


监控：你可以通过 kubectl describe quota -n dev 实时查看配额使用情况。

```

## 测试
- 本地测试
```shell
curl -X GET http://localhost:8080/order-api/api/v1/orders/me -H "Authorization: Bearer dummy-jwt-token" -H "X-User-Id: 1"  -H "Content-Type: application/json"
```