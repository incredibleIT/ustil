# 使用 Eclipse Temurin JDK 17 (Alpine 版本，体积小)
FROM eclipse-temurin:17-jre-alpine

# 设置工作目录
WORKDIR /app

# 复制 Maven 构建的 JAR 文件
COPY target/cpc-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口 8081 (生产环境端口，避开 Jenkins 的 8080)
EXPOSE 8081

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8081/actuator/health || exit 1

# JVM 参数优化
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 启动 Spring Boot 应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=8081 -jar app.jar"]
