# VidVault — UGC 视频分享平台

仿 B 站的视频分享社区，前后端分离架构。支持视频投稿、弹幕互动、主题切换、全文搜索等功能。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 2.7.18 |
| ORM | MyBatis | — |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | — |
| 搜索引擎 | Elasticsearch | 7.17 |
| 用户端前端 | Vue 3 + Vite | 5 |
| 管理端前端 | Vue 3 + Vite | 5 |
| UI 框架 | Element Plus | — |
| 状态管理 | Pinia | — |

## 项目结构

```
bil-common/          # 公共模块：PO、Mapper、Service、Redis、工具类、ES
bil-web/             # 用户端接口（端口 7071）
bil-admin/           # 管理后台接口（端口 7070，context-path /admin）
bil-frontend/        # 用户端前端（端口 5173）
bil-admin-frontend/  # 管理后台前端
sql/                 # 数据库脚本
  init.sql           # 初始化建表
  upgrade_v2.sql     # V2 升级（ALTER + 新表）
  upgrade_v3.sql     # V3 升级（主题配置表）
  test_data.sql      # 测试数据
```

## 快速启动

### 1. 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0（端口 3306，root / 123456）
- Redis（端口 6379）
- Elasticsearch 7.17（端口 9200，可选，搜索回退 MySQL）

### 2. 数据库

```bash
mysql -u root -p123456 < sql/init.sql
mysql -u root -p123456 < sql/upgrade_v2.sql
mysql -u root -p123456 < sql/upgrade_v3.sql
mysql -u root -p123456 < sql/test_data.sql
```

### 3. Redis

确保 Redis 运行在 `localhost:6379`。

### 4. Elasticsearch（可选）

```bash
cd D:\elasticsearch-7.17.25-windows-x86_64\elasticsearch-7.17.25\bin
elasticsearch.bat
```

不启动 ES 时搜索功能自动回退到 MySQL。

### 5. 后端

在 IntelliJ IDEA 中依次运行：

- `BilbilWebRunApplication` — 用户端接口（端口 7071）
- `BilbilAdminRunApplication` — 管理后台接口（端口 7070）

> **注意**：源码目录为 `scr/` 而非 `src/`，请在 IDEA 中使用 Build → Rebuild Project 编译。

### 6. 前端

```bash
cd bil-frontend && npm install && npm run dev      # http://localhost:5173
cd bil-admin-frontend && npm install && npm run dev  # 管理后台
```

### 7. 测试账号

| 角色 | 邮箱 | 密码 |
|------|------|------|
| 普通用户 / 管理员 | test@bilbil.com | Abc123!@# |

管理员登录管理后台使用上述账号。

## 功能清单

### 用户端

| 模块 | 说明 |
|------|------|
| 注册 / 登录 | 邮箱注册、MD5 加密、验证码、Token 会话、自动登录 |
| 首页 | 轮播图、分区标签、推荐视频、排行榜、热门标签 |
| 视频播放 | 分片上传转码、弹幕 Canvas 渲染、倍速 0.5-2x、清晰度切换、画中画、音量控制、续播 |
| 视频详情 | 互动栏（点赞/投币/收藏）、评论嵌套回复、弹幕面板、相关推荐 |
| 搜索 | 自动补全、历史记录、4 种排序、富结果卡片、ES 全文检索 |
| 创作中心 | 分区分片上传、封面拖拽上传、审核状态跟踪 |
| 个人中心 | Lv 等级徽章、VIP 徽章、4 标签页（投稿/收藏/动态/系列/关于）、头像裁剪上传 |
| 消息中心 | 未读数、消息列表、分组、一键已读、私信 |
| 收藏夹 | 多收藏夹分组、夹内视频管理 |
| 系列 | 创建系列、添加视频、排序、系列详情页 |
| 签到 | 每日签到、连续签到、经验获取 |
| 动态 | 图文/视频动态、动态点赞 |
| 举报 | 视频 / 评论 / 弹幕举报 |
| 主题切换 | 多套渐变色主题，用户可自由切换 |
| 暗色模式 | CSS 变量系统、平滑过渡 |
| 移动端适配 | 汉堡菜单、底部导航栏、响应式布局 |

### 管理后台

| 模块 | 说明 |
|------|------|
| 数据统计 | 实时统计、按周统计 |
| 视频管理 | 视频列表、审核、推荐、删除、封面/视频预览 |
| 用户管理 | 用户列表、封禁/解封 |
| 分区管理 | 分区列表、新增/编辑/删除、排序、图标裁剪上传 |
| Banner 管理 | 轮播图列表、新增/编辑/删除 |
| 主题管理 | 主题列表、新增/编辑/删除（渐变色配置） |
| 弹幕管理 | 弹幕列表、删除 |
| 评论管理 | 评论列表、删除 |
| 举报管理 | 举报列表、处理 |
| 系统设置 | 文件大小限制、评论开关、弹幕开关、审核开关、注册开关 |

## API 文档

完整接口文档见 `API文档.md`。

## 代码规范

见 `CODE_STANDARDS.md`。

## 架构说明

### 请求流程

```
浏览器 → Vite(:5173) → /api → bil-web(:7071) → MySQL / Redis / ES
                       /videos → 静态资源映射
                       /images → 静态资源映射
```

### ES 线程池隔离

ES 查询运行在独立线程池 `esExecutor`（4-8 线程，队列 50），不占用 Tomcat 请求线程。查询超时 4 秒，超时或异常自动回退 MySQL。

### 视频上传流程

1. `preUploadVideo` — 预申请，返回 uploadId
2. `uploadVideo` — 分片上传（10MB/片，2 路并行，3 次重试）
3. `completeUpload` — 合并分片 → ffmpeg 转码 → 提取时长

### 配置要点

- Tomcat 最大线程：50
- 单文件上传限制：2GB
- 分片超时：4 分钟 / 片
- Token 有效期：30 天