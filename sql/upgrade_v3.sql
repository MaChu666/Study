-- =============================================
-- BilBil V3 数据库升级脚本
-- 新增：系统主题配置表
-- =============================================

USE bilisql;

-- =============================================
-- 系统主题配置表
-- =============================================
CREATE TABLE IF NOT EXISTS sys_theme (
    theme_id      INT AUTO_INCREMENT COMMENT '主题id' PRIMARY KEY,
    theme_name    VARCHAR(100)  DEFAULT NULL COMMENT '主题名称',
    gradient      VARCHAR(500)  DEFAULT NULL COMMENT '主题渐变色（CSS gradient）',
    primary_color VARCHAR(20)   DEFAULT NULL COMMENT '主色调',
    sort          INT           DEFAULT 0 COMMENT '排序',
    status        INT           DEFAULT 1 COMMENT '状态（0：禁用 1：启用）',
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统主题配置表';

-- =============================================
-- 插入默认主题数据
-- =============================================
INSERT INTO sys_theme (theme_name, gradient, primary_color, sort, status) VALUES
('星河紫', 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', '#667eea', 1, 1),
('蜜桃粉', 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)', '#f093fb', 2, 1),
('海洋蓝', 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)', '#4facfe', 3, 1),
('薄荷绿', 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)', '#43e97b', 4, 1),
('暖阳橙', 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)', '#fa709a', 5, 1),
('梦幻紫', 'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)', '#a18cd1', 6, 1),
('日落红', 'linear-gradient(135deg, #fccb90 0%, #d57eeb 100%)', '#fccb90', 7, 1),
('极光蓝', 'linear-gradient(135deg, #e0c3fc 0%, #8ec5fc 100%)', '#e0c3fc', 8, 1);
