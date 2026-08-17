package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.DanmuInfo;
import com.machugit.entity.query.DanmuInfoQuery;
import com.machugit.entity.vo.DanmuVO;


/**
 * 弹幕信息 业务接口
 */
public interface DanmuInfoService {

    /**
     * 发布弹幕
     */
    void postDanmu(String userId, String videoId, String fileId, String text, Integer mode, String color, Integer fontSize, Integer isPrior, Integer danmuType, Long time, Long jumpTime);

    /**
     * 加载弹幕
     */
    List<DanmuInfo> loadDanmu(String fileId, String videoId);

    /**
     * 加载弹幕（管理员），返回包含视频名称和用户昵称的 VO 列表
     */
    List<DanmuVO> loadDanmuAdmin(DanmuInfoQuery query);
    /**
     * 删除弹幕
     */
    void delDanmu(Integer danmuId);
}
