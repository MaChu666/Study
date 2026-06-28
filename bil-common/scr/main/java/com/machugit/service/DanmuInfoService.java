package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.DanmuInfo;
import com.machugit.entity.query.DanmuInfoQuery;


/**
 * 弹幕信息 业务接口
 */
public interface DanmuInfoService {

    /**
     * 发布弹幕
     */
    void postDanmu(String userId, String videoId, String fileId, String text, Integer mode, String color, Long time);

    /**
     * 加载弹幕
     */
    List<DanmuInfo> loadDanmu(String fileId, String videoId);

    /**
     * 加载弹幕（管理员）
     */
    List<DanmuInfo> loadDanmuAdmin(DanmuInfoQuery query);

    /**
     * 删除弹幕
     */
    void delDanmu(Integer danmuId);
}
