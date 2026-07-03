package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.enums.PageSize;
import com.machugit.entity.po.MessageInfo;
import com.machugit.entity.po.UserDynamic;
import com.machugit.entity.po.UserFocus;
import com.machugit.entity.query.MessageInfoQuery;
import com.machugit.entity.query.SimplePage;
import com.machugit.entity.query.UserDynamicQuery;
import com.machugit.entity.query.UserFocusQuery;
import com.machugit.entity.vo.PaginationResultVO;
import com.machugit.mappers.MessageInfoMapper;
import com.machugit.mappers.UserDynamicMapper;
import com.machugit.mappers.UserFocusMapper;
import com.machugit.service.UserDynamicService;

/**
 * 用户动态 业务接口实现
 */
@Service("userDynamicService")
public class UserDynamicServiceImpl implements UserDynamicService {

    @Resource
    private UserDynamicMapper<UserDynamic, UserDynamicQuery> userDynamicMapper;

    @Resource
    private UserFocusMapper<UserFocus, UserFocusQuery> userFocusMapper;

    @Resource
    private MessageInfoMapper<MessageInfo, MessageInfoQuery> messageInfoMapper;

    /**
     * 分页加载用户动态
     */
    @Override
    public List<UserDynamic> loadDynamics(String userId, Integer pageNo) {
        UserDynamicQuery query = new UserDynamicQuery();
        query.setUserId(userId);
        query.setOrderBy("create_time desc");
        query.setPageNo(pageNo == null ? 1 : pageNo);
        query.setPageSize(PageSize.SIZE15.getSize());

        int count = this.userDynamicMapper.selectCount(query);
        SimplePage page = new SimplePage(query.getPageNo(), count, query.getPageSize());
        query.setSimplePage(page);
        return this.userDynamicMapper.selectList(query);
    }

    /**
     * 发布动态
     */
    @Override
    public void postDynamic(UserDynamic dynamic) {
        dynamic.setCreateTime(new Date());
        dynamic.setUpdateTime(new Date());
        this.userDynamicMapper.insert(dynamic);
        // 通知所有粉丝
        notifyFollowers(dynamic);
    }

    /**
     * 通知粉丝有新动态
     */
    public void notifyFollowers(UserDynamic dynamic) {
        try {
            // 查询粉丝列表
            UserFocusQuery focusQuery = new UserFocusQuery();
            focusQuery.setFocusUserId(dynamic.getUserId());
            List<UserFocus> fansList = this.userFocusMapper.selectList(focusQuery);
            if (fansList == null || fansList.isEmpty()) return;
            Date now = new Date();
            for (UserFocus fan : fansList) {
                MessageInfo msg = new MessageInfo();
                msg.setUserId(fan.getUserId()); // 发送给粉丝
                msg.setMessageType(2); // 动态通知类型
                msg.setContent(dynamic.getUserId() + " 发布了新动态: " + (dynamic.getContent() != null ? dynamic.getContent() : ""));
                msg.setReadStatus(0);
                msg.setCreateTime(now);
                this.messageInfoMapper.insert(msg);
            }
        } catch (Exception e) {
            // 不影响主流程
        }
    }

    /**
     * 删除动态
     */
    @Override
    public void deleteDynamic(Integer dynamicId) {
        this.userDynamicMapper.deleteByDynamicId(dynamicId);
    }

}
