---
stepsCompleted: [1, 2, 3]
inputDocuments:
  - PRD-syit-cpc.md
  - ux-design-specification.md
workflowType: 'architecture'
project_name: '沈阳工业大学计算机程序设计社团官方网站'
user_name: 'Max'
date: '2026-04-03'
---

# Architecture Decision Document

_This document builds collaboratively through step-by-step discovery. Sections are appended as we work through each architectural decision together._

---

## Document Setup

**Created:** 2026-04-03
**Project:** 沈阳工业大学计算机程序设计社团官方网站
**Input Documents:**
- PRD-syit-cpc.md — 产品需求文档
- ux-design-specification.md — UX 设计规范

---

## Input Documents Summary

### From PRD
- **技术栈**: Spring Boot 3.x + Java 17 (后端), Vue.js 3 + TypeScript (前端), MySQL 8.0 (数据库)
- **核心功能**: 成员管理、转正考核、内容发布与审核、内容展示
- **用户角色**: 预备成员(50人)、正式成员(100人)、负责人
- **审核机制**: 20%通过制

### From UX Design
- **设计方向**: 内容优先布局
- **设计系统**: Element Plus + TailwindCSS
- **核心体验**: 轻松发布内容，参与社区自治
- **关键交互**: 3步内容发布、1秒审核投票、可视化转正流程

---

## Project Context Analysis

### Requirements Overview

**Functional Requirements:**

| 模块 | 功能点 | 复杂度 | 优先级 |
|------|--------|--------|--------|
| **成员管理** | 注册/登录、角色管理、个人信息 | 中 | P1 |
| **转正考核** | 在线答题、项目提交、审核流程 | 高 | P1 |
| **内容发布** | 编辑器、草稿保存、预览 | 中 | P1 |
| **内容审核** | 投票机制、实时统计、阈值计算 | 高 | P2 |
| **内容展示** | 列表、详情、搜索 | 低 | P1 |

**关键功能特性：**
- 20%通过制的审核投票机制（Phase 2）
- 角色权限控制（预备/正式/负责人）
- 转正流程的可视化进度
- 内容编辑器的 Markdown 支持

**Non-Functional Requirements:**

| 类型 | 要求 | 影响 |
|------|------|------|
| **性能** | 页面加载 < 2秒，并发 50+ | 需要缓存和优化 |
| **安全** | bcrypt、SQL注入/XSS/CSRF防护 | 标准安全实践 |
| **可用性** | 响应式设计、浏览器兼容 | 前端框架选型 |
| **可维护性** | 代码注释、API文档 | 开发规范 |

### Scale & Complexity

**复杂度评估：**

| 指标 | 评估 | 说明 |
|------|------|------|
| 实时功能 | 中等 | 审核投票实时统计，Phase 1 可用轮询替代 |
| 多租户 | 无 | 单社团系统 |
| 合规要求 | 低 | 无特殊合规要求 |
| 集成复杂度 | 低 | 主要内部功能 |
| 交互复杂度 | 中等 | 审核投票、编辑器 |
| 数据复杂度 | 低-中 | 关系型数据，量级小（<1000用户） |

**项目规模：中等复杂度 Web 应用**

**主要技术域：** 全栈 Web 开发（前端 + 后端 + 数据库）

**预估架构组件：** 15-20 个（包含前后端组件）

### Technical Constraints & Dependencies

**技术栈约束：**
- **后端：** Spring Boot 3.x + Java 17
- **前端：** Vue.js 3 + TypeScript
- **数据库：** MySQL 8.0
- **部署：** Docker + Docker Compose

**外部依赖：**
- 邮件服务（邮箱验证）
- 可选：OSS 对象存储（Phase 2 文件上传）

### Cross-Cutting Concerns Identified

1. **身份认证与授权** — JWT + RBAC 权限控制
2. **数据一致性** — 审核投票的并发处理（Phase 2）
3. **实时通信** — WebSocket/SSE 用于投票实时更新（Phase 2）
4. **文件存储** — 图片上传（Phase 2）
5. **缓存策略** — 热点内容缓存

### Multi-Perspective Insights

**后端架构建议：**
- 审核投票使用乐观锁防止并发问题
- 单独计数表避免锁表
- 幂等性检查防止重复投票

**前端架构建议：**
- Phase 1 使用轮询（5秒）替代 WebSocket
- Phase 2 迁移到 SSE 服务器推送
- 使用 EventSource 接收实时更新

**DevOps 建议：**
- Docker Compose 本地开发
- 校园服务器或阿里云 ECS 生产部署
- MySQL 每日自动备份

**技术负责人建议：**
- **MVP 核心：** 基础 CRUD + 简单答题 + 负责人人工审核
- **Phase 2：** 20%投票机制 + 实时通知 + 文件上传
- 代码注释覆盖率 > 30%
- API 文档使用 Swagger 自动生成

### Architecture Decisions Preview

**关键决策点：**

1. **审核机制：** Phase 1 负责人人工审核 → Phase 2 20%自动投票
2. **实时通信：** Phase 1 轮询 → Phase 2 SSE
3. **答题防作弊：** 随机抽题 + 标签页切换检测
4. **性能优化：** 数据库索引 > Redis 缓存 > Nginx 缓存

---

## Starter Template Evaluation

### Primary Technology Domain

**全栈 Web 应用** — Spring Boot 后端 + Vue.js 前端

### Selected Starter

**后端：Spring Initializr + Maven**

**初始化命令：**
```bash
spring init \
  --dependencies=web,data-jpa,mysql,security,validation,lombok,devtools \
  --build=maven \
  --java-version=17 \
  --packaging=jar \
  syit-cpc-backend
```

**前端：Vite + Vue 3 + TypeScript**

**初始化命令：**
```bash
npm create vue@latest syit-cpc-frontend
# 选项：TypeScript, Vue Router, Pinia, ESLint, Prettier
```

**然后安装：**
```bash
cd syit-cpc-frontend
npm install element-plus
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init -p
```

---
