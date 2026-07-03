-- =============================================
-- BilBil V2 数据库升级脚本
-- 参考 B 站业务模型，补充用户成长体系、内容组织、互动深度、创作变现、风控质量
-- =============================================

USE bilisql;

-- =============================================
-- 一、现有表字段补充
-- =============================================

-- 0. 封面列扩容（base64 图片太长，VARCHAR 不够）
ALTER TABLE video_info MODIFY COLUMN video_cover TEXT null comment '封面';

-- 1. 用户表补充：等级、经验、大会员、头像、背景图
ALTER TABLE user_info
    ADD COLUMN avatar varchar(500) null comment '头像URL' AFTER use_name,
    ADD COLUMN banner_image varchar(500) null comment '个人空间头图' AFTER avatar,
    ADD COLUMN level int default 0 not null comment '用户等级（0-6级）' AFTER current_coin_count,
    ADD COLUMN exp int default 0 not null comment '当前经验值' AFTER level,
    ADD COLUMN vip_type tinyint default 0 not null comment '大会员类型（0：无 1：月度 2：季度 3：年度）' AFTER exp,
    ADD COLUMN vip_expire_time datetime null comment '大会员过期时间' AFTER vip_type,
    ADD COLUMN live_status tinyint default 0 not null comment '直播状态（0：离线 1：直播中）' AFTER status,
    ADD INDEX idx_level (level);

-- 2. 视频表补充：来源、定时发布、逻辑删除
ALTER TABLE video_info
    ADD COLUMN source_url varchar(500) null comment '转载来源链接（转载时必填）' AFTER post_type,
    ADD COLUMN scheduled_publish_time datetime null comment '定时发布时间（为空则立即发布）' AFTER create_time,
    ADD COLUMN is_deleted tinyint default 0 not null comment '逻辑删除（0：正常 1：已删）' AFTER status,
    ADD INDEX idx_scheduled_time (scheduled_publish_time);

-- 3. 用户行为表优化：投币数量、取消标记
ALTER TABLE user_action
    ADD COLUMN coin_count tinyint default 1 null comment '投币数量（仅投币类型有效，1或2）' AFTER action_type,
    ADD COLUMN is_cancel tinyint default 0 not null comment '是否取消（0：有效 1：已取消）' AFTER comment_id,
    ADD COLUMN target_user_id varchar(20) null comment '目标用户id（关注等操作时使用）' AFTER user_id;

-- 如果 action_count 列存在则删除（如果该列不存在会报错，可先确认再执行）
-- ALTER TABLE user_action DROP COLUMN action_count;

-- 4. 视频播放历史补充进度
ALTER TABLE video_play_history
    ADD COLUMN progress_seconds int default 0 null comment '上次观看到第几秒' AFTER file_id,
    ADD COLUMN is_finished tinyint default 0 null comment '是否看完（0：未 1：已看完）';

-- =============================================
-- 二、新增核心业务表
-- =============================================

-- 用户每日签到记录
CREATE TABLE IF NOT EXISTS user_sign_in (
    sign_id     int auto_increment comment '签到id' primary key,
    user_id     varchar(20)                        not null comment '用户id',
    sign_date   date                               not null comment '签到日期',
    continuous_days int default 0                 null comment '连续签到天数',
    create_time datetime default CURRENT_TIMESTAMP null comment '签到时间',
    constraint uk_user_sign_date unique (user_id, sign_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '用户签到表';

-- 修改 level 默认值为 1（新用户默认 Lv1）
ALTER TABLE user_info MODIFY COLUMN level INT DEFAULT 1 NOT NULL comment '用户等级（0-6级）';

-- 经验获取日志
CREATE TABLE IF NOT EXISTS user_exp_log (
    log_id      int auto_increment primary key,
    user_id     varchar(20)   not null,
    exp_amount  int           not null comment '获取/扣除的经验值',
    source_type tinyint       not null comment '来源（1：登录 2：投币 3：观看 4：分享 5：每日任务 6：签到）',
    source_id   varchar(50)   null comment '关联业务id',
    create_time datetime default CURRENT_TIMESTAMP null,
    INDEX idx_user_time (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '经验值变动日志';

-- 收藏夹分组表
CREATE TABLE IF NOT EXISTS favorite_folder (
    folder_id   int auto_increment comment '收藏夹id' primary key,
    user_id     varchar(20)                        not null comment '用户id',
    folder_name varchar(100)                       not null comment '收藏夹名称',
    description varchar(200)                       null comment '简介',
    cover_image varchar(500)                       null comment '封面图',
    type        tinyint default 0                 null comment '隐私类型（0：公开 1：私密）',
    video_count int    default 0                  null comment '视频数量（冗余字段）',
    sort        int    default 0                  null comment '排序',
    create_time datetime default CURRENT_TIMESTAMP null,
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uk_user_folder_name unique (user_id, folder_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '收藏夹分组表';

-- 收藏夹视频关联表
CREATE TABLE IF NOT EXISTS favorite_video (
    id          int auto_increment primary key,
    folder_id   int                                not null comment '收藏夹id（0表示默认收藏夹）',
    user_id     varchar(20)                        not null comment '用户id',
    video_id    varchar(20)                        not null comment '视频id',
    create_time datetime default CURRENT_TIMESTAMP null comment '收藏时间',
    constraint uk_folder_video unique (folder_id, video_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '收藏夹视频关联表';

-- 用户动态表
CREATE TABLE IF NOT EXISTS user_dynamic (
    dynamic_id    int auto_increment comment '动态id' primary key,
    user_id       varchar(20)                        not null comment '发布者id',
    dynamic_type  tinyint                            not null comment '类型（1：转发 2：图文 3：纯文字 4：视频动态）',
    content       text                               null comment '文本内容',
    images        json                               null comment '图片列表（JSON数组）',
    video_id      varchar(20)                        null comment '关联的视频id',
    forward_id    int                                null comment '转发的原动态id',
    forward_count int      default 0                 null comment '转发数（冗余）',
    like_count    int      default 0                 null comment '点赞数（冗余）',
    comment_count int      default 0                 null comment '评论数（冗余）',
    status        tinyint  default 0                 null comment '状态（0：正常 1：审核中 2：驳回）',
    create_time   datetime default CURRENT_TIMESTAMP null,
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '用户动态表';

-- 动态点赞表
CREATE TABLE IF NOT EXISTS dynamic_like (
    id          int auto_increment primary key,
    dynamic_id  int                                not null,
    user_id     varchar(20)                        not null,
    create_time datetime default CURRENT_TIMESTAMP null,
    constraint uk_dynamic_user unique (dynamic_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '动态点赞表';

-- 举报信息表
CREATE TABLE IF NOT EXISTS report_info (
    report_id      int auto_increment comment '举报id' primary key,
    reporter_id    varchar(20)                        not null comment '举报人id',
    target_type    tinyint                            not null comment '举报类型（1：视频 2：评论 3：弹幕 4：用户 5：动态）',
    target_id      varchar(50)                        not null comment '目标业务id',
    reason_type    tinyint                            not null comment '原因类型（1：色情 2：暴力 3：侵权 4：引战 5：其他）',
    reason_desc    varchar(500)                       null comment '补充描述',
    proof_images   json                               null comment '证据截图（JSON数组）',
    status         tinyint  default 0                 null comment '处理状态（0：待审核 1：已驳回 2：已受理 3：已处理）',
    handler_id     varchar(20)                        null comment '处理人id（管理员）',
    handle_result  varchar(200)                       null comment '处理结果描述',
    create_time    datetime default CURRENT_TIMESTAMP null,
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    INDEX idx_target (target_type, target_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '举报信息表';

-- 视频审核日志
CREATE TABLE IF NOT EXISTS video_audit_log (
    audit_id    int auto_increment primary key,
    video_id    varchar(20)   not null,
    auditor_id  varchar(20)   not null comment '审核员id',
    from_status tinyint       null comment '原状态',
    to_status   tinyint       not null comment '审核后状态（1：通过 2：驳回）',
    reason      varchar(500)  null comment '审核意见/驳回原因',
    create_time datetime default CURRENT_TIMESTAMP null,
    INDEX idx_video_id (video_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '视频审核操作日志';

-- 硬币交易明细日志
CREATE TABLE IF NOT EXISTS coin_transaction_log (
    log_id      int auto_increment primary key,
    user_id     varchar(20)                        not null,
    video_id    varchar(20)                        not null,
    coin_amount tinyint                            not null comment '投币数量（1或2）',
    source_type tinyint default 1                 null comment '来源（1：用户投币 2：系统奖励）',
    create_time datetime default CURRENT_TIMESTAMP null,
    INDEX idx_user_id (user_id),
    INDEX idx_user_time (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '硬币交易明细日志';

-- 首页轮播图配置
CREATE TABLE IF NOT EXISTS banner_config (
    banner_id   int auto_increment primary key,
    title       varchar(100)                       null comment '标题',
    image_url   varchar(500)                       not null comment '图片地址',
    link_type   tinyint                            null comment '跳转类型（1：视频 2：专栏 3：外链 4：活动）',
    link_value  varchar(200)                       null comment '跳转值（如视频id或URL）',
    sort        int    default 0                  null comment '排序',
    status      tinyint default 1                 null comment '状态（0：下线 1：上线）',
    start_time  datetime                           null comment '开始时间',
    end_time    datetime                           null comment '结束时间',
    create_time datetime default CURRENT_TIMESTAMP null,
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '首页轮播图配置';

-- comment_info 增加 root 字段用于楼中楼
ALTER TABLE comment_info ADD COLUMN root int default 0 null comment '根评论id（0表示自身是根评论）' AFTER reply_comment_id;

-- 消息已读：用 last_read_time 替代逐条标记
ALTER TABLE user_info ADD COLUMN last_read_time datetime null comment '最后阅读消息时间' AFTER notice_info;
