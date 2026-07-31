package com.machugit.video.service.impl;

import com.machugit.entity.po.VideoInfo;
import com.machugit.entity.vo.PaginationResultVO;
import com.machugit.service.VideoInfoService;
import com.machugit.video.rocketmq.VideoEventPublisher;
import com.machugit.video.service.VideoService;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class VideoServiceImpl implements VideoService {
    private static final Logger log = LoggerFactory.getLogger(VideoServiceImpl.class);
    private static final String VIDEO_CACHE_KEY = "video:detail:";

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private VideoEventPublisher videoEventPublisher;

    @Override
    public PaginationResultVO getFeed(Integer pageNo, Integer pageSize) {
        com.machugit.entity.query.VideoInfoQuery query = new com.machugit.entity.query.VideoInfoQuery();
        query.setPageNo(pageNo);
        query.setPageSize(pageSize);
        return videoInfoService.loadVideoList(query);
    }

    @Override
    public VideoInfo getVideoDetail(String videoId, String userId) {
        String cacheKey = VIDEO_CACHE_KEY + videoId;
        VideoInfo cached = (VideoInfo) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        VideoInfo video = videoInfoService.getVideoInfo(videoId);
        if (video != null) {
            long ttl = 300 + (long)(Math.random() * 300);
            redisTemplate.opsForValue().set(cacheKey, video, ttl, TimeUnit.SECONDS);
        }
        return video;
    }

    @Override
    public String uploadVideo(String userId, String filePath, String videoName, String introduction, Integer categoryId) {
        VideoInfo video = new VideoInfo();
        video.setVideoName(videoName);
        video.setIntroduction(introduction);
        video.setCategoryId(categoryId);
        video.setUserId(userId);
        video.setStatus(0);
        videoInfoService.postVideo(video, null);
        videoEventPublisher.publishVideoUploadEvent(video.getVideoId(), userId);
        return video.getVideoId();
    }
}