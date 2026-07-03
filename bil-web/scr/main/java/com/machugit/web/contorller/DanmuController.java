package com.machugit.web.contorller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class DanmuController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(DanmuController.class);

    @Resource
    private DanmuInfoServiceImpl danmuInfoService;

    /**
     * 发布弹幕
     */
    @RequestMapping("/postDanmu")
    public ResponseVO postDanmu(@NotEmpty String videoId, String fileId, @NotEmpty String text,
                                @NotEmpty String mode, @NotEmpty String color, @NotEmpty String time) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        danmuInfoService.postDanmu(tokenUserInfoDto.getUserId(), videoId, fileId, text,
                Integer.valueOf(mode), color, Long.valueOf(time));
        return getSuccessResponseVO(null);
    }

    /**
     * 加载弹幕
     */
    @RequestMapping("/loadDanmu")
    public ResponseVO loadDanmu(String fileId, @NotEmpty String videoId) {
        List<DanmuInfo> list = danmuInfoService.loadDanmu(fileId, videoId);
        return getSuccessResponseVO(list);
    }
}
