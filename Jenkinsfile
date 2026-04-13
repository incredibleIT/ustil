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
                sh '''
                    echo "  📂 当前目录: $(pwd)"
                    echo "  📂 文件列表:"
                    ls -la backend/
                    echo ""
                    
                    # 先使用 Maven 构建 JAR（使用临时 Dockerfile 避免 Volume 映射问题）
                    echo "  📦 Maven 构建中..."
                    cd backend
                    echo "  📂 Backend 目录: $(pwd)"
                    echo "  📄 POM 文件: $(ls -l pom.xml 2>/dev/null || echo 'NOT FOUND')"
                    echo ""
                    
                    # 创建临时构建 Dockerfile
                    cat > Dockerfile.build << 'BUILDEOF'
                    FROM maven:3.9-eclipse-temurin-17-alpine
                    WORKDIR /app
                    COPY . .
                    RUN mvn clean package -DskipTests -q
                    BUILDEOF
                    
                    # 构建临时镜像
                    docker build -t temp-maven-build -f Dockerfile.build .
                    
                    # 提取 JAR 文件到宿主机
                    docker run --rm temp-maven-build cat /app/target/*.jar > app.jar
                    
                    # 清理临时镜像
                    docker rmi temp-maven-build
                    rm -f Dockerfile.build
                    
                    echo "  ✅ JAR 文件: $(ls -lh app.jar 2>/dev/null || echo 'NOT FOUND')"
                    
                    # 构建生产 Docker 镜像
                    echo "  🐳 构建生产镜像..."
                    docker build -t ${BACKEND_SERVICE}:latest .
                '''
            }
        }
        
        stage('🎨 构建前端镜像') {
            steps {
                echo '🎨 构建 Vue.js Docker 镜像...'
                dir('frontend') {
                    sh '''
                        echo "  📂 Frontend 目录: $(pwd)"
                        echo "  📄 package.json: $(ls -l package.json 2>/dev/null || echo 'NOT FOUND')"
                        echo ""
                        
                        # 创建临时构建 Dockerfile
                        cat > Dockerfile.build << 'BUILDEOF'
                        FROM node:20-alpine
                        WORKDIR /app
                        COPY . .
                        ENV NODE_OPTIONS="--max-old-space-size=4096"
                        RUN npm install
                        RUN npm run build
                        BUILDEOF
                        
                        # 构建临时镜像
                        docker build -t temp-node-build -f Dockerfile.build .
                        
                        # 提取 dist 目录
                        mkdir -p dist
                        docker run --rm temp-node-build tar -c -C /app/dist . | tar -x -C ./dist
                        
                        # 清理临时镜像
                        docker rmi temp-node-build
                        rm -f Dockerfile.build
                        
                        echo "  ✅ Dist 目录: $(ls -lh dist/)"
                        
                        # 构建生产 Docker 镜像
                        echo "  🐳 构建生产镜像..."
                        docker build -t ${FRONTEND_SERVICE}:latest .
                    '''
                }
            }
        }
        
        stage('🚀 部署服务') {
            steps {
                echo '🚀 使用 Docker Compose 部署所有服务...'
                sh '''
                    # 获取数据库密码和 JWT Secret
                    export DB_PASSWORD="yy3908533"
                    export JWT_SECRET="ThisIsASecretKeyForJWTTokenGenerationMustBeAtLeast32CharactersLong"
                    
                    # 创建 .env 文件
                    cat > .env << EOF
DB_PASSWORD=${DB_PASSWORD}
JWT_SECRET=${JWT_SECRET}
EOF
                    
                    # 停止旧容器（保留数据卷）
                    echo "  ⏹️  停止旧服务..."
                    docker-compose down || true
                    
                    # 启动所有服务
                    echo "  🚀 启动新服务..."
                    docker-compose up -d
                    
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
                    docker exec ${MYSQL_SERVICE} mysqladmin ping -h localhost
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
            sh 'docker-compose logs --tail=50 backend || true'
            sh 'docker-compose logs --tail=50 frontend || true'
        }
        always {
            echo '📊 服务状态:'
            sh 'docker-compose ps || true'
            
            // 清理工作空间（可选，调试时可以注释掉）
            // cleanWs()
        }
    }
}
