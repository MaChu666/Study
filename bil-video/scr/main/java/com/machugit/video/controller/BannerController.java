package com.machugit.video.controller;

import com.machugit.entity.vo.ResponseVO;
import com.machugit.service.impl.BannerConfigServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/banner")
@Validated
public class BannerController extends com.machugit.controller.ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(BannerController.class);

    @Resource
    private BannerConfigServiceImpl bannerConfigService;

    @RequestMapping("/loadActiveBanners")
    public ResponseVO loadActiveBanners() {
        return getSuccessResponseVO(bannerConfigService.loadActiveBanners());
    }
}
