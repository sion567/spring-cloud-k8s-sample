FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

ARG SERVICE_NAME
ARG SERVICE_PATH
ENV SERVER_PORT=8080
# 时区（Java 应用）
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

COPY ${SERVICE_PATH}/target/${SERVICE_NAME}-*.jar app.jar
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
# 推荐增加 -XX:+UseG1GC 优化垃圾回收
# $JAVA_OPTS 留空，方便在 K8s Deployment 中配置
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

EXPOSE ${SERVER_PORT}
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]