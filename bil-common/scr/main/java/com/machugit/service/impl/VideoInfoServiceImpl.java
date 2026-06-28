package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.component.RedisComponent;
import com.machugit.entity.enums.PageSize;
import com.machugit.entity.po.VideoInfo;
import com.machugit.entity.po.VideoInfoFile;
import com.machugit.entity.query.SimplePage;
import com.machugit.entity.query.VideoInfoFileQuery;
import com.machugit.entity.query.VideoInfoQuery;
import com.machugit.entity.vo.PaginationResultVO;
import com.machugit.exception.BusinessException;
import com.machugit.mappers.VideoInfoFileMapper;
import com.machugit.mappers.VideoInfoMapper;
import com.machugit.redis.RedisUtils;
import com.machugit.service.VideoInfoService;
import com.machugit.utils.StringTools;

/**
 * 视频信息 业务接口实现
 */
@Service("videoInfoService")
public class VideoInfoServiceImpl implements VideoInfoService {

    @Resource
    private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

    @Resource
    private VideoInfoFileMapper<VideoInfoFile, VideoInfoFileQuery> videoInfoFileMapper;

    @Resource
    private RedisUtils<String> redisUtils;

    @Resource
    private RedisComponent redisComponent;

    /**
     * 加载推荐视频
     */
    @Override
    public List<VideoInfo> loadRecommendVideo() {
        VideoInfoQuery query = new VideoInfoQuery();
        query.setOrderBy("play_count desc");
        query.setSimplePage(new SimplePage(0, PageSize.SIZE15.getSize()));
        return this.videoInfoMapper.selectList(query);
    }

    /**
     * 分页加载视频
     */
    @Override
    public PaginationResultVO<VideoInfo> loadVideo(Integer pCategoryId, Integer categoryId, Integer pageNo) {
        VideoInfoQuery query = new VideoInfoQuery();
        query.setPCategoryId(pCategoryId);
        query.setCategoryId(categoryId);
        query.setPageNo(pageNo);
        query.setPageSize(PageSize.SIZE15.getSize());
        query.setOrderBy("create_time desc");

        int count = this.videoInfoMapper.selectCount(query);
        SimplePage page = new SimplePage(pageNo, count, PageSize.SIZE15.getSize());
        query.setSimplePage(page);
        List<VideoInfo> list = this.videoInfoMapper.selectList(query);
        return new PaginationResultVO<VideoInfo>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
    }

    /**
     * 获取视频详情
     */
    @Override
    public VideoInfo getVideoInfo(String videoId) {
        return this.videoInfoMapper.selectByVideoId(videoId);
    }

    /**
     * 加载视频文件列表
     */
    @Override
    public List<VideoInfoFile> loadVideoPList(String videoId) {
        VideoInfoFileQuery query = new VideoInfoFileQuery();
        query.setVideoId(videoId);
        query.setOrderBy("file_id asc");
        return this.videoInfoFileMapper.selectList(query);
    }

    /**
     * 搜索视频
     */
    @Override
    public List<VideoInfo> search(String keyword) {
        VideoInfoQuery query = new VideoInfoQuery();
        query.setVideoNameFuzzy(keyword);
        query.setOrderBy("play_count desc");
        query.setSimplePage(new SimplePage(0, PageSize.SIZE20.getSize()));
        return this.videoInfoMapper.selectList(query);
    }

    /**
     * 获取搜索关键词排行
     */
    @Override
    public List<String> getSearchKeywordTop() {
        // TODO 从Redis有序集合中获取搜索关键词排行
        return this.redisUtils.getZSetList("search:keyword:top", 10);
    }

    /**
     * 获取视频推荐
     */
    @Override
    public List<VideoInfo> getVideoRecommend(String videoId) {
        VideoInfo currentVideo = this.videoInfoMapper.selectByVideoId(videoId);
        if (currentVideo == null) {
            throw new BusinessException("视频不存在");
        }
        // TODO 从ElasticSearch获取推荐视频，当前回退为查询同分类视频
        VideoInfoQuery query = new VideoInfoQuery();
        query.setCategoryId(currentVideo.getCategoryId());
        query.setOrderBy("play_count desc");
        query.setSimplePage(new SimplePage(0, PageSize.SIZE15.getSize()));
        return this.videoInfoMapper.selectList(query);
    }

    /**
     * 加载热门视频列表
     */
    @Override
    public List<VideoInfo> loadHotVideoList() {
        VideoInfoQuery query = new VideoInfoQuery();
        query.setOrderBy("play_count desc");
        query.setSimplePage(new SimplePage(0, PageSize.SIZE20.getSize()));
        return this.videoInfoMapper.selectList(query);
    }

    /**
     * 上报视频在线播放
     */
    @Override
    public void reportVideoPlayOnline(String fileId, String deviceId) {
        // TODO 通过Redis计数器记录在线播放数据，用于统计播放量
        String redisKey = "video:play:online:" + fileId + ":" + deviceId;
        this.redisUtils.increment(redisKey);
    }

    /**
     * 分页查询视频列表（后台管理）
     */
    @Override
    public PaginationResultVO<VideoInfo> loadVideoList(VideoInfoQuery query) {
        int count = this.videoInfoMapper.selectCount(query);
        int pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        List<VideoInfo> list = this.videoInfoMapper.selectList(query);
        return new PaginationResultVO<VideoInfo>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
    }

    /**
     * 审核视频
     */
    @Override
    public void auditVideo(String videoId, Integer status, String reason) {
        VideoInfo videoInfo = this.videoInfoMapper.selectByVideoId(videoId);
        if (videoInfo == null) {
            throw new BusinessException("视频不存在");
        }
        VideoInfo updateInfo = new VideoInfo();
        updateInfo.setStatus(status);
        updateInfo.setUpdateTime(new Date());
        // TODO 保存审核原因到审核记录表
        this.videoInfoMapper.updateByVideoId(updateInfo, videoId);
    }

    /**
     * 删除视频
     */
    @Override
    public void deleteVideo(String videoId) {
        VideoInfo videoInfo = this.videoInfoMapper.selectByVideoId(videoId);
        if (videoInfo == null) {
            throw new BusinessException("视频不存在");
        }
        // TODO 删除关联的视频文件记录和物理文件
        this.videoInfoFileMapper.deleteByVideoId(videoId);
        this.videoInfoMapper.deleteByVideoId(videoId);
    }

    /**
     * 发布视频
     */
    @Override
    public void postVideo(VideoInfo videoInfo, List<String> uploadFileList) {
        videoInfo.setVideoId(StringTools.getRandomBVId());
        videoInfo.setPlayCount(0L);
        videoInfo.setLikeCount(0L);
        videoInfo.setDanmuCount(0L);
        videoInfo.setCommentCount(0L);
        videoInfo.setCoinCount(0L);
        videoInfo.setCollectCount(0L);
        videoInfo.setStatus(0);
        videoInfo.setCreateTime(new Date());
        videoInfo.setUpdateTime(new Date());
        this.videoInfoMapper.insert(videoInfo);

        if (uploadFileList != null && !uploadFileList.isEmpty()) {
            for (String fileId : uploadFileList) {
                VideoInfoFile updateFile = new VideoInfoFile();
                updateFile.setVideoId(videoInfo.getVideoId());
                this.videoInfoFileMapper.updateByFileId(updateFile, fileId);
            }
        }
    }

    /**
     * 推荐视频
     */
    @Override
    public void recommendVideo(String videoId) {
        VideoInfo videoInfo = this.videoInfoMapper.selectByVideoId(videoId);
        if (videoInfo == null) {
            throw new BusinessException("视频不存在");
        }
        // TODO 将视频标记为推荐，可存入Redis推荐列表或更新推荐标记字段
        this.redisUtils.zaddCount("video:recommend", videoId);
    }

}
