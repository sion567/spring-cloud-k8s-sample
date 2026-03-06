@echo off
title Spring Cloud K8s Local Runner
setlocal enabledelayedexpansion

:: 设置环境变量 (禁用 K8s 並激活 dev)
set SPRING_PROFILES_ACTIVE=dev
set SPRING_CLOUD_KUBERNETES_ENABLED=false

echo [1/3] build public components (api-common, common-lib)...
call mvn clean install -DskipTests -pl api-common,common-lib -am
if %errorlevel% neq 0 (
    echo build failed！
    pause
    exit /b %errorlevel%
)

echo [2/3] build business components (admin, user, auth, order, gateway)...
call mvn package -DskipTests -pl admin-server,user-service,auth-service,order-service,gateway-service
if %errorlevel% neq 0 (
    echo build failed！
    pause
    exit /b %errorlevel%
)

echo [3/3] Starting up all microservices...

:: 启动 Admin Server
echo -> Starting Admin Server (Port: 8888)...
start "Admin Server" cmd /k "java -jar admin-server\target\admin-server-1.0-SNAPSHOT.jar"
timeout /t 5

:: 启动 User Service
echo -> Starting User Service (Port: 8081)...
start "User Service" cmd /k "java -jar user-service\target\user-service-1.0-SNAPSHOT.jar"
timeout /t 5

:: 启动 auth Service
echo -> Starting Auth Service (Port: 8082)...
start "Auth Service" cmd /k "java -jar auth-service\target\auth-service-1.0-SNAPSHOT.jar"

:: 启动 Order Service
echo -> Starting Order Service (Port: 8085)...
start "Order Service" cmd /k "java -jar order-service\target\order-service-1.0-SNAPSHOT.jar"

:: 启动 Gateway Service
echo -> Starting Gateway Service (Port: 8080)...
start "Gateway Service" cmd /k "java -jar gateway-service\target\gateway-service-1.0-SNAPSHOT.jar"

echo.
echo All services are now operational!
echo.
pause