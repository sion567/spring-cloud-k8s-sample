#!/bin/bash

export SPRING_PROFILES_ACTIVE=dev
export SPRING_CLOUD_KUBERNETES_ENABLED=false

echo "[1/3] build public components..."
mvn clean install -DskipTests -pl api-common,common-lib -am

if [ $? -ne 0 ]; then
    echo "build failed！"
    exit 1
fi

echo "[2/3] build business components..."
mvn package -DskipTests -pl admin-server,user-service,auth-service,order-service,gateway-service

mkdir -p ./logs

echo "[3/3] Starting up all microservices..."

start_service() {
    local name=$1
    local port=$2
    echo " -> Starting $name (Port: $port)..."
    nohup java -jar ${name}/target/${name}-1.0.0.jar > ./logs/${name}.log 2>&1 &
}

start_service "admin-server" 8888
sleep 5

start_service "user-service" 8081
start_service "auth-service" 8082
start_service "order-service" 8085
start_service "gateway-service" 8080

echo "All services are now operational!"
echo "status: ps -ef | grep java"
echo "Log path: ./logs/"
echo "Admin UI: http://localhost:8888"