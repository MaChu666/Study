package com.machugit.interaction.controller;

import java.util.List;

import com.machugit.entity.dto.TokenUserInfoDto;
import com.machugit.entity.po.DanmuInfo;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.DanmuInfoServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/danmu")
@Validated
public class DanmuController extends com.machugit.controller.ABaseController {

    @Resource
    private DanmuInfoServiceImpl danmuInfoService;

    /**
     * 发布弹幕
     */
    @RequestMapping("/postDanmu")
    public ResponseVO<Void> postDanmu(@NotEmpty String videoId, String fileId, @NotEmpty String text,
                                @NotEmpty String mode, @NotEmpty String color, @NotEmpty String time,
                                String fontSize, String isPrior, String danmuType, String jumpTime) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        Integer fontPx = (fontSize == null || fontSize.isEmpty()) ? null : Integer.valueOf(fontSize);
        Integer priorFlag = (isPrior == null || isPrior.isEmpty()) ? null : Integer.valueOf(isPrior);
        Integer typeFlag = (danmuType == null || danmuType.isEmpty()) ? null : Integer.valueOf(danmuType);
        Long jumpTimeVal = (jumpTime == null || jumpTime.isEmpty()) ? null : Long.valueOf(jumpTime);
        danmuInfoService.postDanmu(tokenUserInfoDto.getUserId(), videoId, fileId, text,
                Integer.valueOf(mode), color, fontPx, priorFlag, typeFlag, Long.valueOf(time), jumpTimeVal);
        return getSuccessResponseVO(null);
    }

    /**
     * 加载弹幕
     */
    @RequestMapping("/loadDanmu")
    public ResponseVO<List<DanmuInfo>> loadDanmu(String fileId, @NotEmpty String videoId) {
        List<DanmuInfo> list = danmuInfoService.loadDanmu(fileId, videoId);
        return getSuccessResponseVO(list);
    }
}
