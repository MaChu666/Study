package com.machugit.service;


/**
 * 用户行为 业务接口
 */
public interface UserActionService {

    /**
     * 执行用户行为
     */
    void doAction(String videoId, String userId, Integer actionType, Integer actionCount, Integer commentId);
    java.util.Map<String, Boolean> checkStatus(String videoId, String userId);
}
