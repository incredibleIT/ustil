#!/bin/bash
set -e

# ===========================================
# USTIL 服务器环境初始化脚本
# 用途: 在新服务器上安装所有必要软件
# ===========================================

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

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
# 更新系统
# ===========================================
update_system() {
    print_info "更新系统软件包..."
    apt-get update && apt-get upgrade -y
    print_success "系统更新完成"
}

# ===========================================
# 安装 Docker
# ===========================================
install_docker() {
    print_info "安装 Docker..."
    
    if command -v docker &> /dev/null; then
        print_warning "Docker 已安装，跳过"
        return
    fi
    
    curl -fsSL https://get.docker.com | sh
    systemctl enable docker
    systemctl start docker
    
    print_success "Docker 安装完成"
}

# ===========================================
# 安装 Docker Compose
# ===========================================
install_docker_compose() {
    print_info "安装 Docker Compose..."
    
    if docker compose version &> /dev/null; then
        print_warning "Docker Compose 已安装，跳过"
        return
    fi
    
    DOCKER_COMPOSE_VERSION="v2.24.0"
    curl -L "https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
    
    # 创建软链接
    ln -sf /usr/local/bin/docker-compose /usr/bin/docker-compose
    
    print_success "Docker Compose 安装完成"
}

# ===========================================
# 配置防火墙
# ===========================================
configure_firewall() {
    print_info "配置防火墙规则..."
    
    # 允许必要端口
    ufw allow 22/tcp    # SSH
    ufw allow 80/tcp    # HTTP
    ufw allow 443/tcp   # HTTPS
    ufw allow 8080/tcp  # Jenkins
    ufw allow 8081/tcp  # Backend (可选，调试用)
    
    # 启用防火墙（需要用户确认）
    print_warning "是否立即启用防火墙？(y/n)"
    read -r enable_ufw
    if [[ "$enable_ufw" =~ ^[Yy]$ ]]; then
        ufw --force enable
        print_success "防火墙已启用"
    else
        print_warning "防火墙未启用，请手动配置"
    fi
}

# ===========================================
# 创建部署目录
# ===========================================
create_deploy_directory() {
    print_info "创建部署目录..."
    
    mkdir -p /opt/ustil
    chown -R root:root /opt/ustil
    chmod -R 755 /opt/ustil
    
    print_success "部署目录创建完成: /opt/ustil"
}

# ===========================================
# 配置时区
# ===========================================
configure_timezone() {
    print_info "配置时区为 Asia/Shanghai..."
    
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
    echo "Asia/Shanghai" > /etc/timezone
    dpkg-reconfigure -f noninteractive tzdata
    
    print_success "时区配置完成"
}

# ===========================================
# 禁用 Swap（推荐用于 Docker）
# ===========================================
disable_swap() {
    print_info "禁用 Swap..."
    
    if swapon --show | grep -q "/"; then
        swapoff -a
        sed -i '/swap/d' /etc/fstab
        print_success "Swap 已禁用"
    else
        print_warning "Swap 未启用，跳过"
    fi
}

# ===========================================
# 配置 Docker 镜像加速（可选）
# ===========================================
configure_docker_mirror() {
    print_info "是否配置 Docker 镜像加速器？(y/n)"
    read -r configure_mirror
    
    if [[ "$configure_mirror" =~ ^[Yy]$ ]]; then
        print_info "配置 Docker 镜像加速器..."
        
        mkdir -p /etc/docker
        cat > /etc/docker/daemon.json << 'EOF'
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://registry.docker-cn.com"
  ]
}
EOF
        systemctl daemon-reload
        systemctl restart docker
        print_success "Docker 镜像加速器配置完成"
    else
        print_warning "跳过 Docker 镜像加速器配置"
    fi
}

# ===========================================
# 测试 Docker
# ===========================================
test_docker() {
    print_info "测试 Docker 安装..."
    
    docker run --rm hello-world
    print_success "Docker 测试通过"
}

# ===========================================
# 显示安装信息
# ===========================================
show_info() {
    echo ""
    print_success "========================================="
    print_success "✅ 服务器环境初始化完成！"
    print_success "========================================="
    echo ""
    print_info "📦 Docker 版本: $(docker --version)"
    print_info "📦 Docker Compose 版本: $(docker compose version || docker-compose --version)"
    print_info "📂 部署目录: /opt/ustil"
    print_info "🌐 开放端口: 80, 443, 8080, 8081"
    echo ""
    print_info "下一步："
    print_info "1. 将项目代码复制到 /opt/ustil"
    print_info "2. 创建 .env 文件配置密码"
    print_info "3. 运行 ./deploy.sh 部署项目"
    echo ""
}

# ===========================================
# 主流程
# ===========================================
main() {
    echo ""
    print_info "========================================="
    print_info "🚀 USTIL 服务器环境初始化"
    print_info "========================================="
    echo ""
    
    update_system
    install_docker
    install_docker_compose
    configure_timezone
    disable_swap
    configure_firewall
    configure_docker_mirror
    create_deploy_directory
    test_docker
    show_info
    
    print_success "========================================="
    print_success "✅ 初始化完成！"
    print_success "========================================="
    echo ""
}

# 执行主流程
main
