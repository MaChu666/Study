package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.entity.po.BannerConfig;
import com.machugit.entity.query.BannerConfigQuery;
import com.machugit.mappers.BannerConfigMapper;
import com.machugit.service.BannerConfigService;
import com.machugit.utils.DateUtil;

/**
 * 轮播图配置 业务接口实现
 */
@Service("bannerConfigService")
public class BannerConfigServiceImpl implements BannerConfigService {

    @Resource
    private BannerConfigMapper<BannerConfig, BannerConfigQuery> bannerConfigMapper;

    /**
     * 加载生效中的轮播图
     */
    @Override
    public List<BannerConfig> loadActiveBanners() {
        String now = DateUtil.format(new Date(), DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern());
        BannerConfigQuery query = new BannerConfigQuery();
        query.setStatus(1);
        query.setStartTimeEnd(now);
        query.setEndTimeStart(now);
        query.setOrderBy("sort asc");
        return this.bannerConfigMapper.selectList(query);
    }

    /**
     * 加载全部轮播图（管理端）
     */
    @Override
    public List<BannerConfig> loadAllBanners() {
        BannerConfigQuery query = new BannerConfigQuery();
        query.setOrderBy("sort asc");
        return this.bannerConfigMapper.selectList(query);
    }

    /**
     * 保存轮播图（管理端）
     */
    @Override
    public BannerConfig saveBanner(BannerConfig banner) {
        if (banner.getBannerId() == null) {
            banner.setCreateTime(new Date());
            banner.setUpdateTime(new Date());
            this.bannerConfigMapper.insert(banner);
        } else {
            banner.setUpdateTime(new Date());
            this.bannerConfigMapper.updateByBannerId(banner, banner.getBannerId());
        }
        return banner;
    }

    /**
     * 删除轮播图（管理端）
     */
    @Override
    public void deleteBanner(Integer bannerId) {
        this.bannerConfigMapper.deleteByBannerId(bannerId);
    }

}
