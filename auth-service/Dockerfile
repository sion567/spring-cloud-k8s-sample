FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

ARG SERVICE_NAME
ARG SERVICE_PATH

COPY ${SERVICE_PATH}/target/${SERVICE_NAME}-1.0.0.jar app.jar

# 时区（Java 应用）
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Hong_Kong /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# JVM 优化
ENV JAVA_OPTS="-Xms512m -Xmx512m"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]