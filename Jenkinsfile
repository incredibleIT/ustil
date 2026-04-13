pipeline {
    agent any
    
    options {
        // 使用 root 用户执行，避免 Docker 权限问题
        disableConcurrentBuilds()
    }
    
    environment {
        // 服务名称
        BACKEND_SERVICE = 'ustil-backend'
        FRONTEND_SERVICE = 'ustil-frontend'
        MYSQL_SERVICE = 'ustil-mysql'
        
        // 项目路径
        PROJECT_DIR = '/opt/ustil'
        
        // 凭据 ID (在 Jenkins Credentials 中配置)
        GIT_CREDENTIALS_ID = 'github-credentials'
        DB_PASSWORD_CREDENTIALS_ID = 'ustil-db-password'
        JWT_SECRET_CREDENTIALS_ID = 'ustil-jwt-secret'
    }
    
    stages {
        stage('📦 拉取代码') {
            steps {
                echo '📦 从 GitHub 拉取最新代码...'
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    extensions: [],
                    userRemoteConfigs: [[
                        credentialsId: GIT_CREDENTIALS_ID,
                        url: 'https://github.com/incredibleIT/ustil.git'
                    ]]
                ])
            }
        }
        
        stage('🔨 构建后端镜像') {
            steps {
                echo '🔨 构建 Spring Boot Docker 镜像...'
                dir('backend') {
                    sh '''
                        echo "  📂 当前目录: $(pwd)"
                        echo "  📄 POM 文件: $(ls -l pom.xml 2>/dev/null || echo 'NOT FOUND')"
                        echo ""
                        docker build -t ${BACKEND_SERVICE}:latest .
                    '''
                }
            }
        }
        
        stage('🎨 构建前端镜像') {
            steps {
                echo '🎨 构建 Vue.js Docker 镜像...'
                dir('frontend') {
                    sh '''
                        echo "  📂 当前目录: $(pwd)"
                        echo "  📄 package.json: $(ls -l package.json 2>/dev/null || echo 'NOT FOUND')"
                        echo ""
                        docker build -t ${FRONTEND_SERVICE}:latest .
                    '''
                }
            }
        }
        
        stage('🚀 部署服务') {
            steps {
                echo '🚀 部署所有服务...'
                sh '''
                    # 获取数据库密码和 JWT Secret
                    export DB_PASSWORD="yy3908533"
                    export JWT_SECRET="ThisIsASecretKeyForJWTTokenGenerationMustBeAtLeast32CharactersLong"
                    
                    # 创建 .env 文件
                    cat > .env << EOF
DB_PASSWORD=${DB_PASSWORD}
JWT_SECRET=${JWT_SECRET}
EOF
                    
                    # 创建 Docker 网络
                    echo "  🌐 创建 Docker 网络..."
                    docker network create ustil-network || true
                    
                    # 停止并删除旧容器
                    echo "  ⏹️  停止旧服务..."
                    docker stop ustil-backend ustil-frontend ustil-mysql || true
                    docker rm ustil-backend ustil-frontend ustil-mysql || true
                    
                    # 启动 MySQL
                    echo "  🗄️  启动 MySQL..."
                    docker run -d \
                        --name ustil-mysql \
                        --network ustil-network \
                        --restart unless-stopped \
                        -p 3306:3306 \
                        -e MYSQL_ROOT_PASSWORD=${DB_PASSWORD} \
                        -e MYSQL_DATABASE=cpc \
                        -v mysql_data:/var/lib/mysql \
                        mysql:8.0 \
                        --character-set-server=utf8mb4 \
                        --collation-server=utf8mb4_unicode_ci
                    
                    # 等待 MySQL 启动
                    echo "  ⏳ 等待 MySQL 就绪..."
                    sleep 10
                    
                    # 启动后端
                    echo "  🚀 启动后端服务..."
                    docker run -d \
                        --name ustil-backend \
                        --network ustil-network \
                        --restart unless-stopped \
                        -p 8081:8081 \
                        -e "SPRING_DATASOURCE_URL=jdbc:mysql://ustil-mysql:3306/cpc?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai" \
                        -e SPRING_DATASOURCE_USERNAME=root \
                        -e SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD} \
                        -e JWT_SECRET=${JWT_SECRET} \
                        -e SERVER_PORT=8081 \
                        ustil-backend:latest
                    
                    # 启动前端
                    echo "  🎨 启动前端服务..."
                    docker run -d \
                        --name ustil-frontend \
                        --network ustil-network \
                        --restart unless-stopped \
                        -p 80:80 \
                        ustil-frontend:latest
                    
                    # 清理悬空镜像
                    echo "  🧹 清理悬空镜像..."
                    docker image prune -f
                '''
            }
        }
        
        stage('⏳ 等待服务就绪') {
            steps {
                echo '⏳ 等待服务启动...'
                sh 'sleep 15'
            }
        }
        
        stage('✅ 健康检查') {
            steps {
                echo '✅ 检查服务健康状态...'
                sh '''
                    # 检查后端
                    echo "检查后端健康状态..."
                    for i in {1..10}; do
                        if curl -f http://localhost:8081/actuator/health > /dev/null 2>&1; then
                            echo "✅ 后端服务正常"
                            break
                        fi
                        echo "等待后端启动... ($i/10)"
                        sleep 3
                    done
                    
                    # 检查前端
                    echo "检查前端服务..."
                    if curl -f http://localhost:80 > /dev/null 2>&1; then
                        echo "✅ 前端服务正常"
                    else
                        echo "❌ 前端服务异常"
                        exit 1
                    fi
                    
                    # 检查数据库
                    echo "检查数据库连接..."
                    docker exec ustil-mysql mysqladmin ping -h localhost
                '''
            }
        }
        
        stage('📊 验证 API') {
            steps {
                echo '📊 验证 API 端点...'
                sh '''
                    # 测试 API 根路径
                    curl -f http://localhost:8081/api/test > /dev/null 2>&1 || echo "⚠️ API 测试端点不可用"
                    
                    # 测试 Swagger
                    curl -f http://localhost:8081/api-docs > /dev/null 2>&1 && echo "✅ Swagger 文档可用" || echo "⚠️ Swagger 文档不可用"
                '''
            }
        }
    }
    
    post {
        success {
            echo '🎉 部署成功！'
            echo '📍 前端地址: http://47.121.200.41'
            echo '📍 API 地址: http://47.121.200.41/api'
            echo '📍 Swagger UI: http://47.121.200.41/api-docs'
            
            // 发送成功通知（可选）
            // mail to: 'team@example.com',
            //      subject: "✅ USTIL 部署成功",
            //      body: "部署成功！\n\n前端: http://47.121.200.41\nAPI: http://47.121.200.41/api"
        }
        failure {
            echo '❌ 部署失败！查看日志...'
            sh 'docker logs --tail=50 ustil-backend || true'
            sh 'docker logs --tail=50 ustil-frontend || true'
        }
        always {
            echo '📊 服务状态:'
            sh 'docker ps -a --filter "name=ustil" || true'
            
            // 清理工作空间（可选，调试时可以注释掉）
            // cleanWs()
        }
    }
}
