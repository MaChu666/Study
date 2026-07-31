package com.machugit.interaction.controller;

import com.machugit.entity.dto.TokenUserInfoDto;
import com.machugit.entity.po.ReportInfo;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.ReportInfoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/report")
@Validated
public class ReportController extends com.machugit.controller.ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Resource
    private ReportInfoServiceImpl reportInfoService;

    @RequestMapping("/submitReport")
    public ResponseVO submitReport(Integer targetType, String targetId, Integer reasonType, String reasonDesc) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        String userId = tokenUserInfoDto.getUserId();
        ReportInfo report = new ReportInfo();
        report.setReporterId(userId);
        report.setTargetType(targetType);
        report.setTargetId(targetId);
        report.setReasonType(reasonType);
        report.setReasonDesc(reasonDesc);
        reportInfoService.submitReport(report);
        return getSuccessResponseVO(null);
    }
}
