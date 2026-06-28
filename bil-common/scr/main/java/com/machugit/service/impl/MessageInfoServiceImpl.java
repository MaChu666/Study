package com.machugit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.po.MessageCountGroup;
import com.machugit.entity.po.MessageInfo;
import com.machugit.entity.query.MessageInfoQuery;
import com.machugit.mappers.MessageInfoMapper;
import com.machugit.service.MessageInfoService;


/**
 * 消息信息 业务接口实现
 */
@Service("messageInfoService")
public class MessageInfoServiceImpl implements MessageInfoService {

    @Resource
    private MessageInfoMapper<MessageInfo, MessageInfoQuery> messageInfoMapper;

    /**
     * 获取未读消息数量
     */
    @Override
    public Integer getNoReadCount(String userId) {
        MessageInfoQuery query = new MessageInfoQuery();
        query.setUserId(userId);
        query.setReadStatus(0);
        return this.messageInfoMapper.selectCount(query);
    }

    /**
     * 加载消息列表
     */
    @Override
    public List<MessageInfo> loadMessage(String userId) {
        MessageInfoQuery query = new MessageInfoQuery();
        query.setUserId(userId);
        query.setOrderBy("create_time desc");
        return this.messageInfoMapper.selectList(query);
    }

    /**
     * 删除消息
     */
    @Override
    public void delMessage(Integer messageId) {
        this.messageInfoMapper.deleteByMessageId(messageId);
    }

    /**
     * 获取未读消息分组数量
     */
    @Override
    public List<MessageCountGroup> getNoReadCountGroup(String userId) {
        return null;
    }

    /**
     * 全部已读
     */
    @Override
    public void readAll(String userId) {
        MessageInfo bean = new MessageInfo();
        bean.setReadStatus(1);
        MessageInfoQuery query = new MessageInfoQuery();
        query.setUserId(userId);
        this.messageInfoMapper.updateByParam(bean, query);
    }
}
