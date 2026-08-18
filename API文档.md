# VidVault API 接口文档

## 通用说明

### 基础地址

| 环境 | 地址 |
|------|------|
| 用户端 | `http://localhost:7071` |
| 管理后台 | `http://localhost:7070/admin`（context-path） |

### 认证方式

用户端和管理后台均使用 **Header Token** 认证：

```
Header: thoken = <token_value>
```

登录成功后在响应 Cookie 中返回 token，后续请求自动携带。

### 通用响应格式

```json
{
  "status": "success" | "error",
  "code": 200,
  "info": "请求成功",
  "data": {}
}
```

### 状态码

| code | 说明 |
|------|------|
| 200 | 成功 |
| 401 | 未登录 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |
| 600 | 业务错误（参数校验失败等） |
| 601 | 数据冲突（重复 key 等） |
| 901 | 管理端未登录 |

---

## 一、用户端接口

### 1.1 账号模块 `/account`

#### 获取验证码
```
GET /account/checkCode
```
返回 `{ checkCode: "data:image/png;base64,...", checkCodeKey: "xxx" }`

#### 注册
```
POST /account/register
Content-Type: application/x-www-form-urlencoded
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| email | String | 是 | 邮箱 |
| registerPassword | String | 是 | 密码（6-20位，含字母+数字） |
| useName | String | 是 | 昵称 |
| checkCodeKey | String | 是 | 验证码 key |
| checkCode | String | 是 | 验证码 |

#### 登录
```
POST /account/login
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| email | String | 是 | 邮箱 |
| password | String | 是 | 密码（MD5 加密后） |
| checkCodeKey | String | 是 | 验证码 key |
| checkCode | String | 是 | 验证码 |

返回用户信息 + `token`（写入 Cookie）。

#### 自动登录
```
POST /account/autoLogin
```
通过 Cookie 中的 token 自动登录。

#### 退出登录
```
POST /account/logout
```

#### 获取用户统计数据
```
GET /account/getUserCountInfo
```
需要登录。返回关注数、粉丝数、投稿数等。

---

### 1.2 首页模块

#### 加载轮播图
```
GET /banner/loadActiveBanners
```
返回已启用的 Banner 列表。

#### 加载所有分区
```
GET /category/loadAllCategory
```
返回所有分区（含图标、背景图）。

---

### 1.3 视频模块 `/video`

#### 推荐视频
```
GET /video/loadRecommendVideo
```
返回首页推荐视频列表。

#### 热门视频
```
GET /video/loadHotVideoList
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNo | Integer | 否 | 页码，默认 1 |

#### 加载视频详情
```
GET /video/getVideoInfo
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| videoId | String | 是 | 视频 ID（如 BV1xx） |

#### 视频播放信息
```
GET /video/loadVideo
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| videoId | String | 是 | 视频 ID |

返回视频文件路径、封面、互动数据等。

#### 视频分区列表
```
POST /video/loadVideoPList
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| categoryId | Integer | 否 | 分区 ID |
| pageNo | Integer | 否 | 页码 |
| orderType | Integer | 否 | 排序（0:最新 1:最多播放 2:最多收藏） |

#### 搜索
```
GET /video/search
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | 是 | 搜索关键词 |
| pageNo | Integer | 否 | 页码 |
| orderType | Integer | 否 | 排序 |

#### 搜索建议（自动补全）
```
GET /video/suggest
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | 是 | 输入的关键词 |

#### 热搜关键词
```
GET /video/getSearchKeywordTop
```

#### 相关视频推荐
```
GET /video/getVideoRecommend
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| videoId | String | 是 | 当前视频 ID |

#### 上报播放
```
POST /video/reportVideoPlayOnline
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| videoId | String | 是 | 视频 ID |
| fileId | String | 否 | 文件 ID |

---

### 1.4 文件模块 `/file`

#### 预上传视频
```
POST /file/preUploadVideo
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| fileName | String | 是 | 文件名 |
| chunks | String | 是 | 分片总数 |

返回 `{ fileId, uploadId }`。

#### 上传视频分片
```
POST /file/uploadVideo
Content-Type: multipart/form-data
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| chunkFile | File | 是 | 分片文件 |
| chunkIndex | String | 是 | 分片序号（从 0 开始） |
| uploadId | String | 是 | 预上传返回的 uploadId |

#### 完成上传
```
POST /file/completeUpload
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| uploadId | String | 是 | 上传会话 ID |
| fileId | String | 是 | 文件 ID |

触发后台合并分片 + ffmpeg 转码。

#### 上传图片
```
POST /file/uploadImage
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | String | 是 | Base64 编码的图片数据 |
| createThumbnail | String | 否 | 是否生成缩略图（"true"） |

返回 `{ fileId, filePath }`。

#### 获取视频资源
```
GET /file/videoResource/{fileId}
```
返回视频文件的 URL 路径。

#### 获取 TS 分片
```
GET /file/videoResourceTs/{fileId}/{ts}
```

---

### 1.5 创作中心 `/ucenter`

需要登录。

#### 投稿视频
```
POST /ucenter/postVideo
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| videoName | String | 是 | 视频标题 |
| categoryId | Integer | 是 | 分区 ID |
| cover | String | 是 | 封面图片路径 |
| fileId | String | 是 | 上传后返回的文件 ID |
| duration | Integer | 否 | 视频时长（秒） |

#### 我的稿件列表
```
GET /ucenter/loadVideoList
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNo | Integer | 否 | 页码 |
| status | Integer | 否 | 审核状态（-1:全部 0:审核中 1:通过 2:驳回） |

#### 获取稿件统计
```
GET /ucenter/getVideoCountInfo
```

#### 获取稿件详情（编辑用）
```
GET /ucenter/getVideoByVideoId
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| videoId | String | 是 | 视频 ID |

#### 保存互动设置
```
POST /ucenter/saveVideoInteraction
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| videoId | String | 是 | 视频 ID |
| interaction | String | 是 | 互动 JSON |

#### 删除视频
```
POST /ucenter/deleteVideo
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| videoId | String | 是 | 视频 ID |

---

### 1.6 个人中心 `/uhome`

#### 获取用户信息
```
GET /uhome/getUserInfo
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 目标用户 ID |

#### 更新个人信息
```
POST /uhome/updateUserInfo
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| avatar | String | 否 | 头像（Base64） |
| useName | String | 否 | 昵称 |
| sex | Integer | 否 | 性别（0:未知 1:男 2:女） |
| birthday | String | 否 | 生日 |
| school | String | 否 | 学校 |
| personProfile | String | 否 | 个人简介 |

#### 用户视频列表
```
GET /uhome/loadVideoList
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 目标用户 ID |
| pageNo | Integer | 否 | 页码 |

#### 关注 / 取关
```
POST /uhome/focus        # 关注
POST /uhome/cancelFocus  # 取消关注
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| focusUserId | String | 是 | 目标用户 ID |

#### 关注列表 / 粉丝列表
```
GET /uhome/loadFocusList   # 关注列表
GET /uhome/loadFansList    # 粉丝列表
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 用户 ID |
| pageNo | Integer | 否 | 页码 |

#### 用户收藏
```
GET /uhome/loadUserCollection
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 用户 ID |

#### 主题切换
```
POST /uhome/saveTheme
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| themeId | Integer | 是 | 主题 ID |

#### 用户搜索
```
GET /uhome/searchUsers
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | 是 | 搜索关键词 |

---

### 1.7 系列模块 `/uhome/series`

需要登录。

#### 系列列表
```
GET /uhome/series/loadVideoSeries
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 用户 ID |

#### 系列视频列表
```
GET /uhome/series/loadAllVideo
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| seriesId | Integer | 是 | 系列 ID |

#### 创建系列
```
POST /uhome/series/saveVideoSeries
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| seriesName | String | 是 | 系列名称 |
| seriesDescription | String | 否 | 系列描述 |

#### 添加视频到系列
```
POST /uhome/series/saveSeriesVideo
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| seriesId | Integer | 是 | 系列 ID |
| videoId | String | 是 | 视频 ID |

#### 从系列移除视频
```
POST /uhome/series/delSeriesVideo
```

#### 调整视频排序
```
POST /uhome/series/changeVideoSeriesSort
```

#### 删除系列
```
POST /uhome/series/delVideoSeries
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| seriesId | Integer | 是 | 系列 ID |

#### 系列详情（含视频）
```
GET /uhome/series/loadVideoSeriesWithVideo
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| seriesId | Integer | 是 | 系列 ID |

---

### 1.8 评论模块 `/comment`

#### 发表评论
```
POST /comment/postComment
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| videoId | String | 是 | 视频 ID |
| content | String | 是 | 评论内容 |
| replyCommentId | Integer | 否 | 回复的评论 ID |

#### 加载评论列表
```
GET /comment/loadComment
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| videoId | String | 是 | 视频 ID |
| pageNo | Integer | 否 | 页码 |
| orderType | Integer | 否 | 排序（0:按时间 1:按热度） |

#### 置顶 / 取消置顶
```
POST /comment/topComment
POST /comment/cancelTopComment
```

#### 删除评论
```
POST /comment/userDelComment
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| commentId | Integer | 是 | 评论 ID |

---

### 1.9 弹幕模块 `/danmu`

#### 发送弹幕
```
POST /danmu/postDanmu
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| videoId | String | 是 | 视频 ID |
| fileId | String | 是 | 视频文件 ID |
| content | String | 是 | 弹幕内容 |
| time | Integer | 是 | 弹幕出现时间（秒） |
| color | String | 否 | 颜色（默认白色） |
| position | String | 否 | 位置（top/middle/bottom） |

#### 加载弹幕
```
GET /danmu/loadDanmu
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| videoId | String | 是 | 视频 ID |
| fileId | String | 是 | 视频文件 ID |

---

### 1.10 用户互动 `/userAction`

#### 检查互动状态
```
GET /userAction/checkStatus
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| videoId | String | 是 | 视频 ID |
| actionType | Integer | 是 | 类型（1:点赞 2:投币 3:收藏） |

#### 执行互动
```
POST /userAction/doAction
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| videoId | String | 是 | 视频 ID |
| actionType | Integer | 是 | 类型（1:点赞 2:投币 3:收藏） |
| coinCount | Integer | 否 | 投币数量 |
| folderId | Integer | 否 | 收藏夹 ID（收藏时可用） |

---

### 1.11 收藏夹模块 `/favorite`

需要登录。

#### 收藏夹列表
```
GET /favorite/loadFolders
```

#### 创建 / 编辑收藏夹
```
POST /favorite/saveFolder
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| folderId | Integer | 否 | 编辑时传入 |
| folderName | String | 是 | 收藏夹名称 |
| description | String | 否 | 描述 |

#### 删除收藏夹
```
POST /favorite/delFolder
```

#### 收藏夹内容
```
GET /favorite/loadVideos
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| folderId | Integer | 是 | 收藏夹 ID |

#### 添加视频到收藏夹
```
POST /favorite/addVideo
```

#### 从收藏夹移除
```
POST /favorite/removeVideo
```

---

### 1.12 动态模块 `/dynamic`

#### 动态列表
```
GET /dynamic/loadDynamics
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 目标用户 ID |
| pageNo | Integer | 否 | 页码 |

#### 发布动态
```
POST /dynamic/postDynamic
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| content | String | 否 | 文字内容 |
| images | String | 否 | 图片（逗号分隔） |
| videoId | String | 否 | 关联视频 ID |

#### 删除动态
```
POST /dynamic/deleteDynamic
```

#### 点赞 / 取消点赞
```
POST /dynamic/likeDynamic
POST /dynamic/unlikeDynamic
```

---

### 1.13 签到模块 `/signIn`

需要登录。

#### 签到
```
POST /signIn/signIn
```

#### 今日签到状态
```
GET /signIn/getTodaySign
```

#### 连续签到天数
```
GET /signIn/getContinuousDays
```

---

### 1.14 消息模块 `/message`

需要登录。

#### 未读消息数
```
GET /message/getNoReadCount
```

#### 消息列表
```
GET /message/loadMessage
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNo | Integer | 否 | 页码 |
| messageType | Integer | 否 | 消息类型 |

#### 按类型分组未读数
```
GET /message/getNoReadCountGroup
```

#### 全部已读
```
POST /message/readAll
```

#### 删除消息
```
POST /message/delMessage
```

#### 发送私信
```
POST /message/sendPrivateMessage
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| receiverId | String | 是 | 接收者 ID |
| content | String | 是 | 消息内容 |

#### 私信记录
```
GET /message/loadPrivateMessages
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| peerId | String | 是 | 对方用户 ID |

---

### 1.15 播放历史 `/history`

需要登录。

#### 历史列表
```
GET /history/loadHistory
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNo | Integer | 否 | 页码 |

#### 删除单条
```
POST /history/delHistory
```

#### 清空历史
```
POST /history/cleanHistory
```

---

### 1.16 举报模块 `/report`

需要登录。

#### 提交举报
```
POST /report/submitReport
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| targetId | String | 是 | 被举报对象 ID |
| targetType | Integer | 是 | 类型（1:视频 2:评论 3:弹幕） |
| reason | String | 是 | 举报原因 |

---

### 1.17 系统设置 `/sysSetting`

#### 获取系统设置
```
GET /sysSetting/getSetting
```

#### 获取可用主题列表
```
GET /sysSetting/loadThemes
```

---

## 二、管理后台接口

基础路径：`/admin`

所有管理后台接口需要管理员 Token 认证。

### 2.1 账号模块 `/account`

#### 验证码
```
GET /account/checkCode
```

#### 登录
```
POST /account/login
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| email | String | 是 | 邮箱 |
| password | String | 是 | 密码（MD5） |
| checkCodeKey | String | 是 | 验证码 key |
| checkCode | String | 是 | 验证码 |

#### 退出
```
POST /account/logout
```

---

### 2.2 首页统计 `/index`

#### 实时统计数据
```
GET /index/getActualTimeStatisticsInfo
```
返回用户数、视频数、评论数等实时统计。

#### 按周统计
```
GET /index/getWeekStatisticsInfo
```

---

### 2.3 视频管理 `/videoInfo`

#### 视频列表
```
POST /videoInfo/loadVideoList
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNo | Integer | 否 | 页码 |
| videoName | String | 否 | 标题模糊搜索 |
| categoryId | Integer | 否 | 分区筛选 |
| status | Integer | 否 | 审核状态（-1:全部） |

#### 审核视频
```
POST /videoInfo/auditVideo
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| videoId | String | 是 | 视频 ID |
| status | Integer | 是 | 状态（1:通过 2:驳回） |
| reason | String | 否 | 驳回原因 |

#### 删除视频
```
POST /videoInfo/deleteVideo
```

#### 推荐视频
```
POST /videoInfo/recommendVideo
```

#### 视频分区列表
```
POST /videoInfo/loadVideoPList
```

---

### 2.4 用户管理 `/user`

#### 用户列表
```
POST /user/loadUser
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNo | Integer | 否 | 页码 |
| useName | String | 否 | 昵称搜索 |
| email | String | 否 | 邮箱搜索 |
| status | Integer | 否 | 状态（-1:全部） |

#### 修改用户状态
```
POST /user/changeStatus
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 用户 ID |
| status | Integer | 是 | 状态（0:封禁 1:正常） |

---

### 2.5 分区管理 `/category`

#### 分区列表
```
GET /category/loadCategory
```

#### 保存分区
```
POST /category/saveCategory
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| categoryId | Integer | 否 | 编辑时传入 |
| categoryName | String | 是 | 分区名称 |
| categoryCode | String | 是 | 分区编码 |
| pCategoryId | Integer | 否 | 父分区 ID |
| icon | String | 否 | 图标（Base64） |
| background | String | 否 | 背景图（Base64） |

#### 删除分区
```
POST /category/delCategory
```

#### 调整排序
```
POST /category/changeSort
```

---

### 2.6 Banner 管理 `/banner`

#### 轮播图列表
```
GET /banner/loadBanners
```

#### 保存轮播图
```
POST /banner/saveBanner
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| bannerId | Integer | 否 | 编辑时传入 |
| title | String | 是 | 标题 |
| imageUrl | String | 是 | 图片路径 |
| linkUrl | String | 否 | 跳转链接 |
| sort | Integer | 否 | 排序 |

#### 删除轮播图
```
POST /banner/deleteBanner
```

---

### 2.7 主题管理 `/theme`

#### 主题列表
```
GET /theme/loadAllThemes
```

#### 新增主题
```
POST /theme/addTheme
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| themeName | String | 是 | 主题名称 |
| gradient | String | 是 | CSS 渐变值（如 `linear-gradient(135deg, #667eea 0%, #764ba2 100%)`） |
| primaryColor | String | 是 | 主色调（如 `#667eea`） |
| sort | Integer | 否 | 排序 |
| status | Integer | 否 | 状态（0:禁用 1:启用） |

#### 编辑主题
```
POST /theme/updateTheme
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| themeId | Integer | 是 | 主题 ID |
| themeName | String | 否 | 主题名称 |
| gradient | String | 否 | CSS 渐变值 |
| primaryColor | String | 否 | 主色调 |
| sort | Integer | 否 | 排序 |
| status | Integer | 否 | 状态 |

#### 删除主题
```
POST /theme/deleteTheme
```

---

### 2.8 弹幕管理 `/interact`

#### 弹幕列表
```
POST /interact/loadDanmu
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| videoId | String | 否 | 筛选视频 |

#### 删除弹幕
```
POST /interact/delDanmu
```

---

### 2.9 评论管理 `/interact`

#### 评论列表
```
POST /interact/loadComment
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| videoId | String | 否 | 筛选视频 |

#### 删除评论
```
POST /interact/delComment
```

---

### 2.10 举报管理 `/report`

#### 举报列表
```
POST /report/loadReports
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNo | Integer | 否 | 页码 |
| status | Integer | 否 | 处理状态 |

#### 处理举报
```
POST /report/handleReport
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| reportId | Integer | 是 | 举报 ID |
| status | Integer | 是 | 处理状态（1:已处理） |

---

### 2.11 系统设置 `/setting`

#### 获取设置
```
GET /setting/getSetting
```

#### 保存设置
```
POST /setting/saveSetting
```
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| maxFileSize | Long | 否 | 最大文件大小（字节） |
| maxChunkSize | Long | 否 | 最大分片大小（字节） |
| commentOpen | Integer | 否 | 评论开关（0:关 1:开） |
| danmuOpen | Integer | 否 | 弹幕开关（0:关 1:开） |
| videoAudit | Integer | 否 | 视频审核（0:免审 1:审核） |
| registerOpen | Integer | 否 | 注册开关（0:关 1:开） |
| sysName | String | 否 | 系统名称 |

---

### 2.12 文件模块 `/file`

与用户端类似，管理后台也可上传图片和预览视频资源。

#### 上传图片
```
POST /file/uploadImage
```

#### 获取资源
```
GET /file/getResource
```

#### 视频资源
```
GET /file/videoResource/{fileId}
GET /file/videoResourceTs/{fileId}/{ts}
```