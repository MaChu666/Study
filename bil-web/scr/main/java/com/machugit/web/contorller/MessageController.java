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
import com.machugit.entity.po.MessageCountGroup;
import com.machugit.entity.po.MessageInfo;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.MessageInfoServiceImpl;

@RestController
@RequestMapping("/message")
@Validated
public class MessageController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Resource
    private MessageInfoServiceImpl messageInfoService;

    @RequestMapping("/getNoReadCount")
    public ResponseVO getNoReadCount() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        Integer count = messageInfoService.getNoReadCount(tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(count);
    }

    @RequestMapping("/loadMessage")
    public ResponseVO loadMessage() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        List<MessageInfo> list = messageInfoService.loadMessage(tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(list);
    }

    @RequestMapping("/delMessage")
    public ResponseVO delMessage(@NotEmpty String messageId) {
        Integer messageIdInt = Integer.parseInt(messageId);
        messageInfoService.delMessage(messageIdInt);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/getNoReadCountGroup")
    public ResponseVO getNoReadCountGroup() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        List<MessageCountGroup> list = messageInfoService.getNoReadCountGroup(tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(list);
    }

    @RequestMapping("/readAll")
    public ResponseVO readAll() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        messageInfoService.readAll(tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }
}
