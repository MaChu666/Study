package com.machugit.admin.controller;

import java.util.List;

import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.entity.po.BannerConfig;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.service.impl.BannerConfigServiceImpl;
import com.machugit.utils.DateUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/banner")
@Validated
public class AdminBannerController extends ABaseAdminController {

    @Resource
    private BannerConfigServiceImpl bannerConfigService;

    @RequestMapping("/loadBanners")
    public ResponseVO loadBanners() {
        List<BannerConfig> list = bannerConfigService.loadAllBanners();
        return getSuccessResponseVO(list);
    }

    @RequestMapping("/saveBanner")
    public ResponseVO saveBanner(String title,
                                  String imageUrl,
                                  String linkType,
                                  String linkValue,
                                  String sort,
                                  String status,
                                  String startTime,
                                  String endTime) {
        BannerConfig bean = new BannerConfig();
        bean.setTitle(title);
        bean.setImageUrl(imageUrl);
        if (linkType != null && !linkType.isEmpty()) {
            bean.setLinkType(Integer.valueOf(linkType));
        }
        bean.setLinkValue(linkValue);
        if (sort != null && !sort.isEmpty()) {
            bean.setSort(Integer.valueOf(sort));
        }
        if (status != null && !status.isEmpty()) {
            bean.setStatus(Integer.valueOf(status));
        }
        if (startTime != null && !startTime.isEmpty()) {
            bean.setStartTime(DateUtil.parse(startTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
        }
        if (endTime != null && !endTime.isEmpty()) {
            bean.setEndTime(DateUtil.parse(endTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
        }
        bannerConfigService.saveBanner(bean);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/deleteBanner")
    public ResponseVO deleteBanner(String bannerId) {
        bannerConfigService.deleteBanner(Integer.parseInt(bannerId));
        return getSuccessResponseVO(null);
    }
}
