ALTER TABLE danmu_info ADD COLUMN jump_time bigint DEFAULT 0 COMMENT '空降目标时间(毫秒)' AFTER time;
