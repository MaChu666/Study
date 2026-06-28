package com.machugit.admin.controller;

import com.machugit.entity.po.UserInfo;
import com.machugit.entity.query.UserInfoQuery;
import com.machugit.entity.vo.PaginationResultVO;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.service.impl.UserInfoServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/user")
@Validated
public class AdminUserController extends ABaseAdminController {

    @Resource
    private UserInfoServiceImpl userInfoService;

    @RequestMapping("/loadUser")
    public ResponseVO loadUser(@NotEmpty String pageNo,
                               String useNameFuzzy) {
        UserInfoQuery query = new UserInfoQuery();
        query.setPageNo(Integer.parseInt(pageNo));
        query.setUseNameFuzzy(useNameFuzzy);
        query.setOrderBy("join_time desc");
        PaginationResultVO<UserInfo> result = userInfoService.findListByPage(query);
        return getSuccessResponseVO(result);
    }

    @RequestMapping("/changeStatus")
    public ResponseVO changeStatus(@NotEmpty String userId,
                                   @NotEmpty String status) {
        UserInfo updateInfo = new UserInfo();
        updateInfo.setStatus(Integer.parseInt(status));
        userInfoService.updateUserInfoByUserId(updateInfo, userId);
        return getSuccessResponseVO(null);
    }
}
