package com.machugit.web.contorller;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.machugit.entity.dto.TokenUserInfoDto;
import com.machugit.entity.po.VideoPlayHistory;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.VideoPlayHistoryServiceImpl;

@RestController
@RequestMapping("/history")
@Validated
public class HistoryController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);

    @Resource
    private VideoPlayHistoryServiceImpl videoPlayHistoryService;

    @RequestMapping("/loadHistory")
    public ResponseVO loadHistory() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        List<VideoPlayHistory> list = videoPlayHistoryService.loadHistory(tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(list);
    }

    @RequestMapping("/delHistory")
    public ResponseVO delHistory(@NotEmpty String historyId) {
        Integer historyIdInt = Integer.parseInt(historyId);
        videoPlayHistoryService.delHistory(historyIdInt);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/cleanHistory")
    public ResponseVO cleanHistory() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        videoPlayHistoryService.cleanHistory(tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }
}
