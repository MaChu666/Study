package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.po.DanmuInfo;
import com.machugit.entity.query.DanmuInfoQuery;
import com.machugit.mappers.DanmuInfoMapper;
import com.machugit.service.DanmuInfoService;


/**
 * 弹幕信息 业务接口实现
 */
@Service("danmuInfoService")
public class DanmuInfoServiceImpl implements DanmuInfoService {

    @Resource
    private DanmuInfoMapper<DanmuInfo, DanmuInfoQuery> danmuInfoMapper;

    /**
     * 发布弹幕
     */
    @Override
    public void postDanmu(String userId, String videoId, String fileId, String text, Integer mode, String color, Long time) {
        DanmuInfo danmuInfo = new DanmuInfo();
        danmuInfo.setUserId(userId);
        danmuInfo.setVideoId(videoId);
        danmuInfo.setFileId(fileId);
        danmuInfo.setText(text);
        danmuInfo.setMode(mode);
        danmuInfo.setColor(color);
        danmuInfo.setTime(time);
        danmuInfo.setPostTime(new Date());
        this.danmuInfoMapper.insert(danmuInfo);
    }

    /**
     * 加载弹幕
     */
    @Override
    public List<DanmuInfo> loadDanmu(String fileId, String videoId) {
        DanmuInfoQuery query = new DanmuInfoQuery();
        query.setFileId(fileId);
        query.setVideoId(videoId);
        return this.danmuInfoMapper.selectList(query);
    }

    /**
     * 加载弹幕（管理员）
     */
    @Override
    public List<DanmuInfo> loadDanmuAdmin(DanmuInfoQuery query) {
        return this.danmuInfoMapper.selectList(query);
    }

    /**
     * 删除弹幕
     */
    @Override
    public void delDanmu(Integer danmuId) {
        this.danmuInfoMapper.deleteByDanmuId(danmuId);
    }
}
