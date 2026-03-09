#!/bin/bash

# 定義部署函數
deploy_app() {
    export APP_NAME=$1
    export IMAGE_TAG=$2

    echo "🚀 正在部署服務: $APP_NAME, 鏡像版本: $IMAGE_TAG"

    # 使用 envsubst 替換變量並直接交給 kubectl
    envsubst < deployment.tmpl | kubectl apply -f -
}

# 執行部署命令
deploy_app "order-service" "v1"
deploy_app "user-service" "v1"
deploy_app "gateway-service" "v1"