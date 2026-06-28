-- =============================================
-- BilBil 项目数据库初始化脚本
-- =============================================

USE bilisql;

-- =============================================
-- 用户信息表
-- =============================================
CREATE TABLE IF NOT EXISTS `user_info` (
    `user_id`          VARCHAR(20)   NOT NULL COMMENT '用户id',
    `use_name`         VARCHAR(50)   NOT NULL COMMENT '昵称',
    `email`            VARCHAR(150)  NOT NULL COMMENT '邮箱',
    `password`         VARCHAR(64)   NOT NULL COMMENT '密码（MD5加密）',
    `sex`              INT           DEFAULT 0 COMMENT '性别（0：未知 1：男 2：女）',
    `birthday`         VARCHAR(20)   DEFAULT NULL COMMENT '出生日期',
    `school`           VARCHAR(100)  DEFAULT NULL COMMENT '学校',
    `person_profile`   VARCHAR(500)  DEFAULT NULL COMMENT '个人简介',
    `join_time`        DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `last_login_time`  DATETIME      DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip`    VARCHAR(50)   DEFAULT NULL COMMENT '最后登录ip',
    `status`           INT           DEFAULT 1 COMMENT '状态（0：封禁 1：正常）',
    `notice_info`      VARCHAR(500)  DEFAULT NULL COMMENT '空间公告',
    `total_coin_count` INT           DEFAULT 0 COMMENT '硬币总数',
    `current_coin_count` INT         DEFAULT 0 COMMENT '当前硬币数',
    `theme`            INT           DEFAULT 1 COMMENT '主题',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_use_name` (`use_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- =============================================
-- 分类信息表
-- =============================================
CREATE TABLE IF NOT EXISTS `category_info` (
    `category_id`   INT          NOT NULL AUTO_INCREMENT COMMENT '分类id',
    `category_code` VARCHAR(50)  DEFAULT NULL COMMENT '分类编码',
    `category_name` VARCHAR(100) DEFAULT NULL COMMENT '分类名称',
    `p_category_id` INT          DEFAULT 0 COMMENT '父分类id（0表示顶级分类）',
    `icon`          VARCHAR(500) DEFAULT NULL COMMENT '图标',
    `background`    VARCHAR(500) DEFAULT NULL COMMENT '背景图',
    `sort`          INT          DEFAULT 0 COMMENT '排序',
    PRIMARY KEY (`category_id`),
    KEY `idx_p_category_id` (`p_category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类信息表';

-- =============================================
-- 系统设置表
-- =============================================
CREATE TABLE IF NOT EXISTS `sys_setting` (
    `id`            INT          NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `max_file_size` BIGINT       DEFAULT 0 COMMENT '最大文件大小（字节）',
    `max_chunk_size` BIGINT      DEFAULT 0 COMMENT '最大分片大小（字节）',
    `comment_open`  INT          DEFAULT 1 COMMENT '评论开关（0：关闭 1：开启）',
    `danmu_open`    INT          DEFAULT 1 COMMENT '弹幕开关（0：关闭 1：开启）',
    `video_audit`   INT          DEFAULT 1 COMMENT '视频审核（0：免审 1：审核）',
    `register_open` INT          DEFAULT 1 COMMENT '注册开关（0：关闭 1：开启）',
    `sys_name`      VARCHAR(100) DEFAULT 'BilBil' COMMENT '系统名称',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统设置表';

-- 插入默认设置
INSERT INTO `sys_setting` (`id`, `max_file_size`, `max_chunk_size`, `comment_open`, `danmu_open`, `video_audit`, `register_open`, `sys_name`)
VALUES (1, 1073741824, 5242880, 1, 1, 1, 1, 'BilBil')
ON DUPLICATE KEY UPDATE `id`=`id`;

-- =============================================
-- 视频信息表
-- =============================================
CREATE TABLE IF NOT EXISTS `video_info` (
    `video_id`      VARCHAR(20)   NOT NULL COMMENT '视频id',
    `video_cover`   VARCHAR(500)  DEFAULT NULL COMMENT '封面',
    `video_name`    VARCHAR(200)  DEFAULT NULL COMMENT '视频名称',
    `p_category_id` INT           DEFAULT NULL COMMENT '父分类id',
    `category_id`   INT           DEFAULT NULL COMMENT '分类id',
    `post_type`     INT           DEFAULT 1 COMMENT '发布类型（1：自制 2：转载）',
    `tags`          VARCHAR(500)  DEFAULT NULL COMMENT '标签',
    `introduction`  TEXT          DEFAULT NULL COMMENT '简介',
    `interaction`   VARCHAR(200)  DEFAULT NULL COMMENT '互动设置',
    `user_id`       VARCHAR(20)   NOT NULL COMMENT '发布用户id',
    `play_count`    BIGINT        DEFAULT 0 COMMENT '播放量',
    `like_count`    BIGINT        DEFAULT 0 COMMENT '点赞数',
    `danmu_count`   BIGINT        DEFAULT 0 COMMENT '弹幕数',
    `comment_count` BIGINT        DEFAULT 0 COMMENT '评论数',
    `coin_count`    BIGINT        DEFAULT 0 COMMENT '投币数',
    `collect_count` BIGINT        DEFAULT 0 COMMENT '收藏数',
    `status`        INT           DEFAULT 0 COMMENT '状态（0：审核中 1：通过 2：驳回）',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`video_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_p_category_id` (`p_category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_play_count` (`play_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频信息表';

-- =============================================
-- 视频文件信息表
-- =============================================
CREATE TABLE IF NOT EXISTS `video_info_file` (
    `file_id`     VARCHAR(50)   NOT NULL COMMENT '文件id',
    `upload_id`   VARCHAR(50)   DEFAULT NULL COMMENT '上传id',
    `user_id`     VARCHAR(20)   DEFAULT NULL COMMENT '用户id',
    `video_id`    VARCHAR(20)   DEFAULT NULL COMMENT '视频id',
    `file_name`   VARCHAR(200)  DEFAULT NULL COMMENT '文件名',
    `file_path`   VARCHAR(500)  DEFAULT NULL COMMENT '文件路径',
    `file_size`   BIGINT        DEFAULT 0 COMMENT '文件大小（字节）',
    `duration`    INT           DEFAULT 0 COMMENT '时长（秒）',
    `status`      INT           DEFAULT 0 COMMENT '状态（0：上传中 1：转码中 2：完成）',
    `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`file_id`),
    KEY `idx_upload_id` (`upload_id`),
    KEY `idx_video_id` (`video_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频文件信息表';

-- =============================================
-- 弹幕信息表
-- =============================================
CREATE TABLE IF NOT EXISTS `danmu_info` (
    `danmu_id`  INT          NOT NULL AUTO_INCREMENT COMMENT '弹幕id',
    `video_id`  VARCHAR(20)  NOT NULL COMMENT '视频id',
    `file_id`   VARCHAR(50)  NOT NULL COMMENT '文件id',
    `user_id`   VARCHAR(20)  NOT NULL COMMENT '用户id',
    `text`      VARCHAR(500) DEFAULT NULL COMMENT '弹幕内容',
    `mode`      INT          DEFAULT 1 COMMENT '弹幕模式（1：滚动 2：顶部 3：底部）',
    `color`     VARCHAR(20)  DEFAULT '#FFFFFF' COMMENT '弹幕颜色',
    `time`      BIGINT       DEFAULT 0 COMMENT '弹幕出现时间（毫秒）',
    `post_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    PRIMARY KEY (`danmu_id`),
    KEY `idx_video_id` (`video_id`),
    KEY `idx_file_id` (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='弹幕信息表';

-- =============================================
-- 用户行为表（点赞、投币、收藏等）
-- =============================================
CREATE TABLE IF NOT EXISTS `user_action` (
    `action_id`    INT         NOT NULL AUTO_INCREMENT COMMENT '行为id',
    `video_id`     VARCHAR(20) NOT NULL COMMENT '视频id',
    `user_id`      VARCHAR(20) NOT NULL COMMENT '用户id',
    `action_type`  INT         DEFAULT NULL COMMENT '行为类型（1：点赞 2：投币 3：收藏）',
    `action_count` INT         DEFAULT 1 COMMENT '行为数量',
    `comment_id`   INT         DEFAULT NULL COMMENT '评论id（点赞评论时使用）',
    `create_time`  DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`action_id`),
    KEY `idx_video_id` (`video_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_action_type` (`action_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为表';

-- =============================================
-- 评论信息表
-- =============================================
CREATE TABLE IF NOT EXISTS `comment_info` (
    `comment_id`       INT          NOT NULL AUTO_INCREMENT COMMENT '评论id',
    `video_id`         VARCHAR(20)  NOT NULL COMMENT '视频id',
    `user_id`          VARCHAR(20)  NOT NULL COMMENT '用户id',
    `content`          TEXT         DEFAULT NULL COMMENT '评论内容',
    `reply_comment_id` INT          DEFAULT 0 COMMENT '回复的评论id（0表示顶级评论）',
    `img_path`         VARCHAR(500) DEFAULT NULL COMMENT '图片路径',
    `top_type`         INT          DEFAULT 0 COMMENT '置顶类型（0：不置顶 1：置顶）',
    `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`comment_id`),
    KEY `idx_video_id` (`video_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论信息表';

-- =============================================
-- 视频播放历史表
-- =============================================
CREATE TABLE IF NOT EXISTS `video_play_history` (
    `history_id`  INT         NOT NULL AUTO_INCREMENT COMMENT '历史id',
    `video_id`    VARCHAR(20) NOT NULL COMMENT '视频id',
    `user_id`     VARCHAR(20) NOT NULL COMMENT '用户id',
    `file_id`     VARCHAR(50) DEFAULT NULL COMMENT '文件id',
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后观看时间',
    PRIMARY KEY (`history_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_video_id` (`video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频播放历史表';

-- =============================================
-- 消息信息表
-- =============================================
CREATE TABLE IF NOT EXISTS `message_info` (
    `message_id`   INT          NOT NULL AUTO_INCREMENT COMMENT '消息id',
    `user_id`      VARCHAR(20)  NOT NULL COMMENT '接收用户id',
    `message_type` INT          DEFAULT NULL COMMENT '消息类型（1：评论 2：点赞 3：关注 4：系统通知）',
    `content`      VARCHAR(500) DEFAULT NULL COMMENT '消息内容',
    `read_status`  INT          DEFAULT 0 COMMENT '阅读状态（0：未读 1：已读）',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`message_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_read_status` (`read_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息信息表';

-- =============================================
-- 用户关注表
-- =============================================
CREATE TABLE IF NOT EXISTS `user_focus` (
    `focus_id`      INT         NOT NULL AUTO_INCREMENT COMMENT '关注id',
    `user_id`       VARCHAR(20) NOT NULL COMMENT '用户id（关注者）',
    `focus_user_id` VARCHAR(20) NOT NULL COMMENT '被关注用户id',
    `create_time`   DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    PRIMARY KEY (`focus_id`),
    UNIQUE KEY `uk_user_focus` (`user_id`, `focus_user_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_focus_user_id` (`focus_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

-- =============================================
-- 用户收藏表
-- =============================================
CREATE TABLE IF NOT EXISTS `user_collection` (
    `collection_id` INT         NOT NULL AUTO_INCREMENT COMMENT '收藏id',
    `user_id`       VARCHAR(20) NOT NULL COMMENT '用户id',
    `video_id`      VARCHAR(20) NOT NULL COMMENT '视频id',
    `create_time`   DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`collection_id`),
    UNIQUE KEY `uk_user_video` (`user_id`, `video_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- =============================================
-- 视频系列表
-- =============================================
CREATE TABLE IF NOT EXISTS `video_series` (
    `series_id`          INT          NOT NULL AUTO_INCREMENT COMMENT '系列id',
    `user_id`            VARCHAR(20)  NOT NULL COMMENT '用户id',
    `series_name`        VARCHAR(200) DEFAULT NULL COMMENT '系列名称',
    `series_description` VARCHAR(500) DEFAULT NULL COMMENT '系列描述',
    `sort`               INT          DEFAULT 0 COMMENT '排序',
    `update_time`        DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`series_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频系列表';

-- =============================================
-- 系列视频关联表
-- =============================================
CREATE TABLE IF NOT EXISTS `series_video` (
    `id`        INT         NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `series_id` INT         NOT NULL COMMENT '系列id',
    `video_id`  VARCHAR(20) NOT NULL COMMENT '视频id',
    `sort`      INT         DEFAULT 0 COMMENT '排序',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_series_video` (`series_id`, `video_id`),
    KEY `idx_series_id` (`series_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系列视频关联表';

-- =============================================
-- 初始化数据：默认分类
-- =============================================
INSERT INTO `category_info` (`category_id`, `category_code`, `category_name`, `p_category_id`, `sort`) VALUES
(1,  'animation',  '动画',    0, 1),
(2,  'music',      '音乐',    0, 2),
(3,  'game',       '游戏',    0, 3),
(4,  'knowledge',  '知识',    0, 4),
(5,  'tech',       '科技',    0, 5),
(6,  'sports',     '运动',    0, 6),
(7,  'life',       '生活',    0, 7),
(8,  'movie',      '电影',    0, 8),
(9,  'tv',         '电视剧',  0, 9),
(10, 'documentary','纪录片',  0, 10)
ON DUPLICATE KEY UPDATE `category_name`=VALUES(`category_name`);
