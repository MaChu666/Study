package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.MessageCountGroup;
import com.machugit.entity.po.MessageInfo;


/**
 * 消息信息 业务接口
 */
public interface MessageInfoService {

    /**
     * 获取未读消息数量
     */
    Integer getNoReadCount(String userId);

    /**
     * 加载消息列表
     */
    List<MessageInfo> loadMessage(String userId);

    /**
     * 删除消息
     */
    void delMessage(Integer messageId);

    /**
     * 获取未读消息分组数量
     */
    List<MessageCountGroup> getNoReadCountGroup(String userId);

    /**
     * 全部已读
     */
    void readAll(String userId);

    /**
     * 发送私信
     */
    void sendPrivateMessage(String senderId, String targetUserId, String content);

    /**
     * 加载与指定用户的私信列表
     */
    List<MessageInfo> loadPrivateMessages(String userId, String targetUserId);
}
