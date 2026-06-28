package com.machugit.entity.po;

import java.io.Serializable;


/**
 * 消息数量分组
 */
public class MessageCountGroup implements Serializable {

    /**
     * 消息类型
     */
    private Integer messageType;

    /**
     * 数量
     */
    private Integer count;

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public Integer getMessageType() {
        return this.messageType;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCount() {
        return this.count;
    }
}
