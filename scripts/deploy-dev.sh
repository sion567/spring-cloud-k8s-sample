#!/bin/bash
set -e

echo "🚀 [1/5] 检查 Minikube 状态..."
if ! minikube status | grep -q "Running"; then
    echo "❌ Minikube 未运行，尝试启动..."
    minikube start --cni=calico
fi

echo "📦 [2/5] 编译 Java 项目 (Maven)..."
mvn clean install -DskipTests

echo "🐳 [3/5] 挂载 Minikube Docker 环境..."
eval $(minikube docker-env)

echo "🛠️ [4/5] 构建所有微服务镜像..."
services=("auth-service" "order-service" "user-service" "gateway-service" "admin-server")
for service in "${services[@]}"; do
    echo "Building $service..."
    docker build -t "$service:latest" "./$service"
done

echo "☸️ [5/5] 执行 K8s 部署 (Overlays/Dev)..."
# 部署所有内容 (包含基础和业务)
kubectl apply -k ./k8s/overlays/dev/

echo "⏳ 等待 Pod 启动..."
kubectl get pods -n dev -w

echo "✅ 部署脚本执行完毕！"
