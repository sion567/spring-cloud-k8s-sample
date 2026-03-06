# 创建父目录
mkdir spring-cloud-k8s-sample && cd spring-cloud-k8s-sample
# 初始化 pom.xml
touch pom.xml

# 配置父 pom.xml

# 创建公共模块
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
    

# 创建外部配置 (K8s ConfigMap)
kubectl apply -f order-config.yaml

# 1. 应用权限和部署
kubectl apply -f admin-rbac.yaml
kubectl apply -f admin-deployment.yaml
# 2. 获取浏览器访问地址
minikube service admin-server --url


密码存储位置：Secret


minikube image build --build-arg SERVICE_NAME=order-service --build-arg SERVICE_PATH=order-service -t order-service:v1 .
minikube image build --build-arg SERVICE_NAME=user-service --build-arg SERVICE_PATH=user-service -t user-service:v1 .

  

