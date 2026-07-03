# VidVault

模仿 B 站的 UGC 视频分享平台，前后端分离架构。

## 项目技术栈

### 后端
- Spring Boot 2.7
- MyBatis
- MySQL 8.0
- Redis
- Elasticsearch (可选，搜索回退 MySQL)

### 前端
- Vue 3 + Vite 5
- Element Plus
- Pinia
- Mitt

## 项目结构

```
bil-common/     # 公共模块：PO、Mapper、Service、Redis、工具类
bil-web/        # Web 前端接口（端口 7071）
bil-admin/      # 管理后台接口（端口 7070，context-path /admin）
bil-frontend/   # Web 前端（端口 5173）
bil-admin-frontend/  # 管理后台前端
sql/            # 数据库脚本
  init.sql          # 初始化建表
  upgrade_v2.sql    # V2 升级（ALTER + 新表）
  test_data.sql     # 测试数据
```

## 快速启动

### 1. 数据库
```bash
mysql -u root -p123456 < sql/init.sql
mysql -u root -p123456 < sql/upgrade_v2.sql
mysql -u root -p123456 < sql/test_data.sql
```

### 2. Redis
确保 Redis 运行在 localhost:6379

### 3. 后端
```bash
# 在 IDEA 中运行：
BilbilWebRunApplication   (端口 7071)
BilbilAdminRunApplication (端口 7070)
```

### 4. 前端
```bash
cd bil-frontend && npm install && npm run dev     # http://localhost:5173
cd bil-admin-frontend && npm install && npm run dev  # 管理后台
```

### 5. 测试账号
| 角色 | 邮箱 | 密码 (MD5) |
|------|------|-----------|
| 普通用户+管理员 | test@bilbil.com | e99a18c428cb38d5f260853678922e03 |

管理员登录：访问 admin 后台，用上述账号登录。

## API 接口

完整接口文档见 `接口文档.md`，Postman 集合见 `BilBil.postman_collection.json`

## 功能清单

| 模块 | 状态 | 说明 |
|------|------|------|
| 注册/登录 | 完成 | MD5加密、验证码、Token会话 |
| 首页 | 完成 | 轮播图、分区标签、加权推荐、排行榜、热门标签 |
| 视频播放 | 完成 | 弹幕Canvas渲染、倍速0.5-2x、清晰度切换、PIP画中画、音量控制、续播 |
| 视频详情 | 完成 | 互动栏(点赞/投币/收藏)、评论嵌套回复、弹幕面板、相关推荐 |
| 搜索 | 完成 | 自动补全、历史记录、4种排序、富结果卡片 |
| 创作中心 | 完成 | 分区分片上传、封面拖拽上传、审核状态跟踪 |
| 用户中心 | 完成 | Lv等级徽章、VIP徽章、5标签页(投稿/收藏/动态/系列/关于) |
| 消息中心 | 完成 | 未读数、消息列表、分组、一键已读 |
| 收藏夹 | 完成 | 多收藏夹分组、夹内视频管理 |
| 系列 | 完成 | 创建系列、添加视频、排序、系列详情页 |
| 签到 | 完成 | 每日签到、连续签到、经验获取 |
| 动态 | 完成 | 图文/视频动态、动态点赞 |
| 举报 | 完成 | 视频/评论/弹幕举报 |
| 暗色模式 | 完成 | CSS变量系统、平滑过渡 |
| 管理后台 | 完成 | 视频审核、用户管理、分类管理、Banner管理、弹幕/评论管理、系统设置 |1
| 移动端适配 | 完成 | 汉堡菜单、底部导航栏、响应式布局 |
