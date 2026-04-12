#!/bin/bash
set -e

# ===========================================
# USTIL 项目部署脚本
# 用途: 一键部署到生产服务器
# ===========================================

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印函数
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# ===========================================
# 检查环境
# ===========================================
check_environment() {
    print_info "检查运行环境..."
    
    # 检查 Docker
    if ! command -v docker &> /dev/null; then
        print_error "Docker 未安装"
        exit 1
    fi
    
    # 检查 Docker Compose
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        print_error "Docker Compose 未安装"
        exit 1
    fi
    
    # 检查 .env 文件
    if [ ! -f ".env" ]; then
        print_warning ".env 文件不存在，使用默认配置"
        print_warning "生产环境建议创建 .env 文件配置密码"
    fi
    
    print_success "环境检查通过"
}

# ===========================================
# 拉取最新代码
# ===========================================
pull_code() {
    print_info "从 GitHub 拉取最新代码..."
    
    if git rev-parse --is-inside-work-tree &> /dev/null; then
        git pull origin main
        print_success "代码更新完成"
    else
        print_info "不是 Git 仓库，跳过代码拉取"
    fi
}

# ===========================================
# 构建镜像
# ===========================================
build_images() {
    print_info "构建 Docker 镜像..."
    
    # 使用 docker compose 或 docker-compose
    COMPOSE_CMD="docker compose"
    if ! command -v docker compose &> /dev/null; then
        COMPOSE_CMD="docker-compose"
    fi
    
    $COMPOSE_CMD build --no-cache
    
    print_success "镜像构建完成"
}

# ===========================================
# 启动服务
# ===========================================
start_services() {
    print_info "启动所有服务..."
    
    $COMPOSE_CMD down || true
    $COMPOSE_CMD up -d
    
    print_success "服务已启动"
}

# ===========================================
# 等待服务就绪
# ===========================================
wait_for_services() {
    print_info "等待服务启动..."
    sleep 15
}

# ===========================================
# 健康检查
# ===========================================
health_check() {
    print_info "执行健康检查..."
    
    # 检查后端
    print_info "检查后端服务..."
    local backend_ok=false
    for i in {1..10}; do
        if curl -f http://localhost:8081/actuator/health > /dev/null 2>&1; then
            print_success "✅ 后端服务正常"
            backend_ok=true
            break
        fi
        print_warning "等待后端启动... ($i/10)"
        sleep 3
    done
    
    if [ "$backend_ok" = false ]; then
        print_error "❌ 后端服务启动失败"
        docker compose logs --tail=50 backend
        exit 1
    fi
    
    # 检查前端
    print_info "检查前端服务..."
    if curl -f http://localhost:80 > /dev/null 2>&1; then
        print_success "✅ 前端服务正常"
    else
        print_error "❌ 前端服务异常"
        docker compose logs --tail=50 frontend
        exit 1
    fi
    
    # 检查数据库
    print_info "检查数据库连接..."
    if docker exec ustil-mysql mysqladmin ping -h localhost > /dev/null 2>&1; then
        print_success "✅ 数据库连接正常"
    else
        print_error "❌ 数据库连接失败"
        docker compose logs --tail=50 mysql
        exit 1
    fi
    
    print_success "所有服务健康检查通过！"
}

# ===========================================
# 显示部署信息
# ===========================================
show_deployment_info() {
    echo ""
    print_success "========================================="
    print_success "🎉 部署成功！"
    print_success "========================================="
    echo ""
    print_info "📍 前端地址: http://47.121.200.41"
    print_info "📍 API 地址: http://47.121.200.41/api"
    print_info "📍 Swagger UI: http://47.121.200.41/api-docs"
    print_info "📍 数据库端口: 3306"
    echo ""
    print_info "📊 服务状态:"
    docker compose ps
    echo ""
}

# ===========================================
# 主流程
# ===========================================
main() {
    echo ""
    print_info "========================================="
    print_info "🚀 USTIL 项目部署开始"
    print_info "========================================="
    echo ""
    
    check_environment
    pull_code
    build_images
    start_services
    wait_for_services
    health_check
    show_deployment_info
    
    print_success "========================================="
    print_success "✅ 部署完成！"
    print_success "========================================="
    echo ""
}

# 执行主流程
main
