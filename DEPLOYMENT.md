# USTIL 项目部署指南

## 📋 目录

- [项目架构](#项目架构)
- [环境要求](#环境要求)
- [快速部署](#快速部署)
- [Jenkins 自动化部署](#jenkins-自动化部署)
- [手动部署](#手动部署)
- [配置说明](#配置说明)
- [常用命令](#常用命令)
- [故障排查](#故障排查)

## 🏗️ 项目架构

```
┌─────────────────────────────────────────────┐
│  服务器 47.121.200.41                        │
├─────────────────────────────────────────────┤
│                                              │
│  Jenkins:8080 (CI/CD)                        │
│  ├─ 拉取代码 (Git)                           │
│  ├─ 构建镜像 (Docker)                        │
│  └─ 自动部署                                  │
│                                              │
│  Docker 容器组:                               │
│  ├─ ustil-nginx:80 (前端)                    │
│  │   └─ Vue.js + Nginx                       │
│  │                                            │
│  ├─ ustil-backend:8081 (后端)                │
│  │   └─ Spring Boot + JDK 17                 │
│  │                                            │
│  └─ ustil-mysql:3306 (数据库)                │
│      └─ MySQL 8.0                            │
│                                              │
└─────────────────────────────────────────────┘
```

## 💻 环境要求

### 服务器要求

- **操作系统**: Ubuntu 20.04+ / CentOS 8+
- **内存**: ≥ 4GB（推荐 8GB）
- **硬盘**: ≥ 20GB
- **CPU**: ≥ 2 核

### 软件要求

- **Docker**: 20.10+
- **Docker Compose**: 2.0+
- **Jenkins**: 2.375+（可选，用于自动化部署）

## 🚀 快速部署

### 方式一：一键部署脚本

```bash
# 1. 登录服务器
ssh root@47.121.200.41

# 2. 克隆项目
cd /opt
git clone https://github.com/incredibleIT/ustil.git
cd ustil

# 3. 创建环境变量文件
cp .env.example .env
# 编辑 .env 文件，设置数据库密码和 JWT 密钥
vim .env

# 4. 运行部署脚本
chmod +x deploy.sh
./deploy.sh
```

### 方式二：使用 Docker Compose

```bash
# 1. 创建 .env 文件
cat > .env << EOF
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key_at_least_32_chars
EOF

# 2. 启动所有服务
docker compose up -d

# 3. 查看日志
docker compose logs -f
```

## 🔧 Jenkins 自动化部署

### 第一步：安装 Jenkins 插件

登录 Jenkins → 系统管理 → 插件管理 → 安装以下插件：

1. ✅ **Docker Plugin**
2. ✅ **Docker Pipeline Plugin**
3. ✅ **Pipeline Plugin**
4. ✅ **Git Plugin**
5. ✅ **Credentials Plugin**

### 第二步：配置凭据

登录 Jenkins → 系统管理 → 凭据管理 → 添加凭据：

1. **GitHub 凭据**
   - ID: `github-credentials`
   - 类型: Username with password
   - 用户名: 你的 GitHub 用户名
   - 密码: GitHub Token

2. **数据库密码凭据**
   - ID: `ustil-db-password`
   - 类型: Secret text
   - 密钥: 你的数据库密码

3. **JWT 密钥凭据**
   - ID: `ustil-jwt-secret`
   - 类型: Secret text
   - 密钥: 你的 JWT 密钥（至少32字符）

### 第三步：创建 Pipeline 任务

1. **新建任务** → 选择 **Pipeline**
2. **任务名称**: `ustil-deploy`
3. **配置 Pipeline**:
   - 选择 **Pipeline script from SCM**
   - SCM: Git
   - Repository URL: `https://github.com/incredibleIT/ustil.git`
   - Credentials: `github-credentials`
   - Branch: `*/main`
   - Script Path: `Jenkinsfile`

4. **配置触发器**（可选）:
   - **轮询 SCM**: `H/5 * * * *`（每5分钟检查一次）
   - **GitHub Webhook**: `http://47.121.200.41:8080/github-webhook/`

### 第四步：配置 GitHub Webhook

1. 打开 GitHub 仓库 → Settings → Webhooks
2. **Add webhook**:
   - Payload URL: `http://47.121.200.41:8080/github-webhook/`
   - Content type: `application/json`
   - Events: `Just the push event`

### 第五步：运行构建

1. 进入 Jenkins 任务页面
2. 点击 **Build Now**
3. 查看构建日志

## 📦 手动部署

### 1. 服务器环境初始化

```bash
# 上传初始化脚本到服务器
scp setup-server.sh root@47.121.200.41:/root/

# SSH 登录服务器
ssh root@47.121.200.41

# 运行初始化脚本
chmod +x /root/setup-server.sh
/root/setup-server.sh
```

### 2. 部署项目

```bash
# 进入部署目录
cd /opt/ustil

# 创建环境变量文件
cat > .env << EOF
DB_PASSWORD=Aa12345!
JWT_SECRET=ThisIsASecretKeyForJWTTokenGenerationMustBeAtLeast32CharactersLong
EOF

# 构建并启动
docker compose up -d --build

# 查看服务状态
docker compose ps
```

## ⚙️ 配置说明

### 环境变量 (.env)

| 变量名 | 说明 | 默认值 | 必填 |
|--------|------|--------|------|
| `DB_PASSWORD` | 数据库密码 | `yy3908533` | ✅ |
| `JWT_SECRET` | JWT 密钥（≥32字符） | `ThisIsASecretKeyForJWTTokenGeneration...` | ✅ |
| `SERVER_PORT` | 后端端口 | `8081` | ❌ |
| `SPRING_PROFILES_ACTIVE` | Spring Profile | `prod` | ❌ |

### 端口映射

| 容器 | 内部端口 | 外部端口 | 说明 |
|------|---------|---------|------|
| ustil-nginx | 80 | 80 | 前端访问 |
| ustil-nginx | 443 | 443 | HTTPS（可选） |
| ustil-backend | 8081 | 8081 | 后端 API |
| ustil-mysql | 3306 | 3306 | 数据库 |
| Jenkins | 8080 | 8080 | CI/CD |

### Nginx 反向代理

```nginx
# 前端静态文件
location / {
    root /usr/share/nginx/html;
    try_files $uri $uri/ /index.html;
}

# 后端 API 代理
location /api/ {
    proxy_pass http://backend:8081/api/;
}

# Swagger UI
location /swagger-ui/ {
    proxy_pass http://backend:8081/swagger-ui/;
}

# API 文档
location /api-docs {
    proxy_pass http://backend:8081/api-docs;
}
```

## 🛠️ 常用命令

### Docker Compose 命令

```bash
# 启动所有服务
docker compose up -d

# 停止所有服务
docker compose down

# 重启服务
docker compose restart

# 查看服务状态
docker compose ps

# 查看日志
docker compose logs -f
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f mysql

# 重新构建镜像
docker compose build --no-cache

# 更新部署
docker compose pull
docker compose up -d

# 完全清理（含数据卷）
docker compose down -v
```

### Docker 容器操作

```bash
# 进入后端容器
docker exec -it ustil-backend sh

# 进入数据库容器
docker exec -it ustil-mysql mysql -u root -p

# 查看容器资源使用
docker stats

# 查看容器日志
docker logs -f ustil-backend
```

### 健康检查

```bash
# 检查后端
curl http://localhost:8081/actuator/health

# 检查前端
curl http://localhost:80

# 检查数据库
docker exec ustil-mysql mysqladmin ping -h localhost
```

## 🔍 故障排查

### 问题一：后端启动失败

```bash
# 查看后端日志
docker compose logs backend

# 常见原因：
# 1. 数据库未启动
# 2. 数据库密码错误
# 3. 端口冲突
```

**解决方案**：
```bash
# 检查数据库是否运行
docker compose ps mysql

# 测试数据库连接
docker exec ustil-mysql mysql -u ustil_user -p syit_cpc

# 重启数据库
docker compose restart mysql
```

### 问题二：前端无法访问后端

```bash
# 检查 Nginx 配置
docker exec ustil-frontend nginx -t

# 检查网络连通性
docker exec ustil-frontend wget -qO- http://backend:8081/actuator/health
```

### 问题三：数据库迁移失败

```bash
# 查看 Flyway 日志
docker compose logs backend | grep Flyway

# 手动执行迁移
docker exec ustil-mysql mysql -u root -p syit_cpc < backend/src/main/resources/db/migration/V1__init.sql
```

### 问题四：端口冲突

```bash
# 查看端口占用
netstat -tulpn | grep 8080
netstat -tulpn | grep 8081
netstat -tulpn | grep 3306

# 停止占用进程
kill -9 <PID>
```

### 问题五：Jenkins 构建失败

```bash
# 检查 Jenkins 日志
tail -f /var/log/jenkins/jenkins.log

# 检查 Docker 权限
usermod -aG docker jenkins
systemctl restart jenkins

# 检查凭据配置
# Jenkins → 系统管理 → 凭据管理
```

## 📊 监控与维护

### 日志管理

```bash
# 查看实时日志
docker compose logs -f

# 查看最近100行日志
docker compose logs --tail=100

# 导出日志
docker compose logs > logs.txt
```

### 数据备份

```bash
# 备份数据库
docker exec ustil-mysql mysqldump -u root -p syit_cpc > backup_$(date +%Y%m%d).sql

# 恢复数据库
docker exec -i ustil-mysql mysql -u root -p syit_cpc < backup_20240101.sql
```

### 清理资源

```bash
# 清理未使用的镜像
docker image prune -a

# 清理未使用的卷
docker volume prune

# 清理所有未使用的资源
docker system prune -a
```

## 🔒 安全建议

1. **修改默认密码**
   - 数据库密码
   - JWT 密钥
   - Jenkins 管理员密码

2. **配置 HTTPS**
   - 使用 Let's Encrypt 免费证书
   - 配置 Nginx SSL

3. **限制端口访问**
   - 只开放 80、443、8080 端口
   - 使用防火墙限制 IP 访问

4. **定期更新**
   - 更新 Docker 镜像
   - 更新系统软件包
   - 更新依赖库

## 📞 获取帮助

- **项目地址**: https://github.com/incredibleIT/ustil
- **问题反馈**: GitHub Issues
- **文档**: 查看项目 README.md
