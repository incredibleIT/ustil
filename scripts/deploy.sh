#!/bin/bash

# USTIL 快速部署脚本
# 用法: ./deploy.sh

set -e

SERVER="47.121.200.41"
PROJECT_DIR="/opt/ustil"

echo "====================================="
echo "🚀 USTIL 快速部署"
echo "====================================="
echo ""

# 1. 检查 SSH 连接
echo "📡 步骤 1/5: 检查服务器连接..."
if ! ssh -o ConnectTimeout=5 root@$SERVER "echo 'connected'" > /dev/null 2>&1; then
  echo "❌ 无法连接到服务器 $SERVER"
  echo "请检查 SSH 配置和网络连接"
  exit 1
fi
echo "✅ 服务器连接正常"

# 2. 拉取最新代码
echo ""
echo "📦 步骤 2/5: 拉取最新代码..."
ssh root@$SERVER "cd $PROJECT_DIR && git pull origin main"
echo "✅ 代码拉取完成"

# 3. 构建并部署
echo ""
echo "🔨 步骤 3/5: 构建并部署服务..."
ssh root@$SERVER << 'ENDSSH'
cd /opt/ustil

echo "  📦 构建后端镜像..."
cd backend
docker run --rm \
  -v "$PWD":/app \
  -w /app \
  maven:3.9-eclipse-temurin-17-alpine \
  mvn clean package -DskipTests -q

cd ..

echo "  🎨 构建前端镜像..."
cd frontend
docker build -t ustil-frontend:latest . -q

cd ..

echo "  🚀 重启服务..."
docker compose down
docker compose up -d

echo "  🧹 清理悬空镜像..."
docker image prune -f
ENDSSH

echo "✅ 服务部署完成"

# 4. 等待服务就绪
echo ""
echo "⏳ 步骤 4/5: 等待服务启动..."
sleep 10

# 5. 健康检查
echo ""
echo "✅ 步骤 5/5: 健康检查..."
BACKEND_STATUS=$(ssh root@$SERVER "curl -s -o /dev/null -w '%{http_code}' http://localhost:8081/actuator/health" 2>/dev/null || echo "000")
FRONTEND_STATUS=$(ssh root@$SERVER "curl -s -o /dev/null -w '%{http_code}' http://localhost:80" 2>/dev/null || echo "000")

echo ""
if [ "$BACKEND_STATUS" = "200" ]; then
  echo "  ✅ 后端服务正常 (8081)"
else
  echo "  ⚠️  后端服务可能还在启动中 (状态: $BACKEND_STATUS)"
fi

if [ "$FRONTEND_STATUS" = "200" ]; then
  echo "  ✅ 前端服务正常 (80)"
else
  echo "  ❌ 前端服务异常 (状态: $FRONTEND_STATUS)"
fi

# 显示容器状态
echo ""
echo "📊 容器状态:"
ssh root@$SERVER "docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'"

echo ""
echo "====================================="
echo "🎉 部署完成！"
echo "====================================="
echo ""
echo "📍 前端地址: http://$SERVER"
echo "📍 API 地址: http://$SERVER:8081/api"
echo "📍 Jenkins:  http://$SERVER:8080"
echo ""
echo "📝 查看日志:"
echo "  ssh root@$SERVER 'docker compose -f $PROJECT_DIR/docker-compose.yml logs -f'"
echo ""
