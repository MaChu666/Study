package com.machugit.video.service;

import com.machugit.entity.po.VideoInfo;
import com.machugit.entity.vo.PaginationResultVO;

public interface VideoService {
    PaginationResultVO getFeed(Integer pageNo, Integer pageSize);
    VideoInfo getVideoDetail(String videoId, String userId);
    String uploadVideo(String userId, String filePath, String videoName, String introduction, Integer categoryId);
}