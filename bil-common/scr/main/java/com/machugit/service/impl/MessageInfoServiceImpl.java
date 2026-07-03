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

    @Resource
    private com.machugit.mappers.UserInfoMapper<com.machugit.entity.po.UserInfo, com.machugit.entity.query.UserInfoQuery> userInfoMapper;

    @Override
    public Integer getNoReadCount(String userId) {
        MessageInfoQuery query = new MessageInfoQuery();
        query.setUserId(userId);
        query.setReadStatus(0);
        return this.messageInfoMapper.selectCount(query);
    }

    @Override
    public List<MessageInfo> loadMessage(String userId) {
        MessageInfoQuery query = new MessageInfoQuery();
        query.setUserId(userId);
        query.setOrderBy("create_time desc");
        return this.messageInfoMapper.selectList(query);
    }

    @Override
    public void delMessage(Integer messageId) {
        this.messageInfoMapper.deleteByMessageId(messageId);
    }

    @Override
    public List<MessageCountGroup> getNoReadCountGroup(String userId) {
        return null;
    }

    @Override
    public void readAll(String userId) {
        // 更新 last_read_time（全局红点用）
        com.machugit.entity.po.UserInfo u = new com.machugit.entity.po.UserInfo();
        u.setLastReadTime(new java.util.Date());
        userInfoMapper.updateByUserId(u, userId);
        // 同时标记所有消息已读（私信列表红点用）
        MessageInfo bean = new MessageInfo();
        bean.setReadStatus(1);
        MessageInfoQuery query = new MessageInfoQuery();
        query.setUserId(userId);
        this.messageInfoMapper.updateByParam(bean, query);
    }

    /**
     * 发送私信：在接收者和发送者两边各存一条记录
     */
    @Override
    public void sendPrivateMessage(String senderId, String targetUserId, String content) {
        java.util.Date now = new java.util.Date();
        // 接收者消息
        MessageInfo recvMsg = new MessageInfo();
        recvMsg.setUserId(targetUserId);
        recvMsg.setMessageType(5); // 私信类型
        recvMsg.setContent(senderId + ":" + content);
        recvMsg.setReadStatus(0);
        recvMsg.setCreateTime(now);
        this.messageInfoMapper.insert(recvMsg);
        // 发送者消息（已发）
        MessageInfo sentMsg = new MessageInfo();
        sentMsg.setUserId(senderId);
        sentMsg.setMessageType(5);
        sentMsg.setContent("to:" + targetUserId + ":" + content);
        sentMsg.setReadStatus(1);
        sentMsg.setCreateTime(now);
        this.messageInfoMapper.insert(sentMsg);
    }

    /**
     * 加载与指定用户的私信对话
     */
    @Override
    public List<MessageInfo> loadPrivateMessages(String userId, String targetUserId) {
        MessageInfoQuery query = new MessageInfoQuery();
        query.setUserId(userId);
        query.setMessageType(5);
        query.setOrderBy("create_time asc");
        return this.messageInfoMapper.selectList(query);
    }
}
