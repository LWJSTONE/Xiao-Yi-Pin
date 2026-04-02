# 校园兼职网站 (Xiao-Yi-Pin)

基于 Spring Cloud 微服务架构的校园兼职信息发布平台，采用前后端分离设计。

## 技术栈

### 后端
- **Spring Boot**: 2.7.18
- **Spring Cloud**: 2021.0.8 (Jubilee)
- **Spring Cloud Alibaba**: 2021.0.5.0
- **MyBatis-Plus**: 3.5.5
- **MySQL**: 8.0
- **Redis**: 7.x
- **Spring Security + JWT**: 鉴权认证
- **Nacos**: 服务注册与配置中心

### 前端
- **Vue**: 3.3.13
- **Vite**: 4.5.3
- **Element Plus**: 2.7.6
- **Pinia**: 2.1.7
- **Axios**: 1.6.7

## 项目结构

```
Xiao-Yi-Pin/
├── campus-common/          # 公共模块(实体、DTO、VO、工具类)
├── campus-gateway/         # 网关服务(8080) - 路由、鉴权、跨域
├── campus-auth/            # 认证服务(8081) - 登录、JWT、权限
├── campus-user/            # 用户服务(8082) - 学生/雇主/管理员
├── campus-job/             # 职位服务(8083) - 兼职发布、审核、搜索
├── campus-order/           # 订单服务(8084) - 报名、结算、评价
├── frontend/               # Vue3前端项目
├── sql/                    # 数据库DDL脚本
├── nginx.conf              # Nginx配置
├── start_all.bat           # Windows一键启动脚本
└── stop_all.bat            # Windows一键停止脚本
```

## 快速开始

### 环境要求
- JDK 1.8
- Maven 3.8.8+
- MySQL 8.0
- Redis
- Node.js 18
- Nacos 2.2.3

### 1. 初始化数据库
```sql
-- 执行 sql/campus_job.sql 初始化数据库
source sql/campus_job.sql
```

### 2. 启动中间件
- 启动 Nacos: `startup.cmd -m standalone`
- 启动 Redis: `redis-server`
- 启动 MySQL

### 3. 启动后端服务
```bash
# 编译打包
mvn clean package -DskipTests

# 按顺序启动服务
java -jar campus-gateway/target/campus-gateway-1.0.0.jar   # 8080
java -jar campus-auth/target/campus-auth-1.0.0.jar         # 8081
java -jar campus-user/target/campus-user-1.0.0.jar         # 8082
java -jar campus-job/target/campus-job-1.0.0.jar           # 8083
java -jar campus-order/target/campus-order-1.0.0.jar       # 8084

# 或使用一键启动脚本(Windows)
start_all.bat
```

### 4. 启动前端
```bash
cd frontend
npm install
npm run dev      # 开发模式 http://localhost:3000
npm run build    # 生产构建
```

### 5. 默认账号
| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 学生 | student | 123456 |
| 雇主 | employer | 123456 |

## 微服务架构

```
浏览器 → Nginx → Spring Cloud Gateway (8080)
                        ↓
              ┌─────────┼─────────┐─────────┐
              ↓         ↓         ↓         ↓
           Auth      User       Job      Order
          (8081)    (8082)    (8083)    (8084)
              ↓         ↓         ↓         ↓
              └─────────┴──── MySQL ────────┘
                        ↓
                     Redis (缓存/会话)
                        ↓
                     Nacos (注册/配置)
```

## API接口

### 认证模块 `/api/v1/auth`
- `POST /login` - 登录
- `POST /refresh` - 刷新Token
- `POST /logout` - 登出

### 用户模块 `/api/v1/user`
- `GET /profile/me` - 获取个人资料
- `PUT /profile/me` - 更新个人资料
- `POST /verify/apply` - 申请认证
- `GET /verify/status` - 查询认证状态

### 职位模块 `/api/v1/job`
- `GET /categories` - 获取分类树
- `POST /posts` - 发布兼职
- `GET /list` - 搜索兼职列表
- `GET /posts/{id}` - 兼职详情
- `PUT /posts/{id}/audit` - 审核兼职

### 订单模块 `/api/v1/order`
- `POST /apply/{jobId}` - 报名兼职
- `PUT /apply/{appId}/review` - 审核报名
- `POST /{orderId}/settle` - 结算订单
- `POST /{orderId}/review` - 提交评价
