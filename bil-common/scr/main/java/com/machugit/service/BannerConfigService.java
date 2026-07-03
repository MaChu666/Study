package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.BannerConfig;

/**
 * 轮播图配置 业务接口
 */
public interface BannerConfigService {

    /**
     * 加载生效中的轮播图
     */
    List<BannerConfig> loadActiveBanners();

    /**
     * 加载全部轮播图（管理端）
     */
    List<BannerConfig> loadAllBanners();

    /**
     * 保存轮播图（管理端）
     */
    BannerConfig saveBanner(BannerConfig banner);

    /**
     * 删除轮播图（管理端）
     */
    void deleteBanner(Integer bannerId);

}
