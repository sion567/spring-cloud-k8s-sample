@echo off
echo [1/5] 正在启动 Minikube 环境检查...
minikube status | findstr "Running" >nul
if %errorlevel% neq 0 (
    echo Minikube 未启动，请先运行 minikube start
    exit /b 1
)

echo [2/5] 正在编译公共模块和业务代码...
call mvn clean install -DskipTests

echo [3/5] 正在切换到 Minikube Docker 守护进程...
@FOR /f "tokens=*" %%i IN ('minikube docker-env') DO @%%i

echo [4/5] 正在构建业务镜像...
:: 假设你的 Dockerfile 都在模块根目录下
docker build -t auth-service:latest ./auth-service
docker build -t order-service:latest ./order-service
docker build -t user-service:latest ./user-service
docker build -t gateway-service:latest ./gateway-service
docker build -t admin-server:latest ./admin-server

echo [5/5] 正在通过 Kustomize 部署到 Minikube (dev 环境)...
:: 先起基础设施 (MySQL/Redis)
kubectl apply -k ./k8s/overlays/dev

echo 正在等待数据库就绪...
kubectl wait --for=condition=ready pod -l layer=infrastructure -n dev --timeout=60s

:: 部署业务应用
kubectl apply -k ./k8s/overlays/dev

echo 部署完成！请运行 'minikube dashboard' 查看状态。
echo 访问网关请运行 'minikube service gateway-service -n dev'
