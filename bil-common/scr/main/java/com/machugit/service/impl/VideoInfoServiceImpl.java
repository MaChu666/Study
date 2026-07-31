package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import io.seata.spring.annotation.GlobalTransactional;

import com.machugit.component.RedisComponent;
import com.machugit.entity.enums.PageSize;
import com.machugit.entity.po.CategoryInfo;
import com.machugit.entity.po.VideoInfo;
import com.machugit.entity.po.VideoInfoFile;
import com.machugit.entity.query.CategoryInfoQuery;
import com.machugit.entity.query.SimplePage;
import com.machugit.entity.query.VideoInfoFileQuery;
import com.machugit.entity.query.VideoInfoQuery;
import com.machugit.entity.vo.PaginationResultVO;
import com.machugit.exception.BusinessException;
import com.machugit.mappers.CategoryInfoMapper;
import com.machugit.mappers.VideoInfoFileMapper;
import com.machugit.mappers.VideoInfoMapper;
import com.machugit.redis.RedisUtils;
import com.machugit.service.VideoInfoService;
import com.machugit.service.impl.VideoAuditLogServiceImpl;
import com.machugit.entity.po.UserDynamic;
import com.machugit.mappers.UserDynamicMapper;
import com.machugit.entity.query.UserDynamicQuery;
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
    private CategoryInfoMapper<CategoryInfo, CategoryInfoQuery> categoryInfoMapper;

    @Resource
    private RedisUtils<String> redisUtils;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private VideoAuditLogServiceImpl videoAuditLogService;

    @Resource
    private UserDynamicMapper<UserDynamic, UserDynamicQuery> userDynamicMapper;

    /**
     * 加载推荐视频（加权排序：播放量 + 点赞*2 + 投币*3 + 收藏*2）
     */
    @Override
    public List<VideoInfo> loadRecommendVideo() {
        VideoInfoQuery query = new VideoInfoQuery();
        query.setStatus(1);
        query.setIsDeleted(0);
        query.setOrderBy("(play_count + like_count * 2 + coin_count * 3 + collect_count * 2) desc");
        query.setSimplePage(new SimplePage(0, PageSize.SIZE15.getSize()));
        return this.videoInfoMapper.selectList(query);
    }

    /**
     * 分页加载视频
     */
    @Override
    public PaginationResultVO<VideoInfo> loadVideo(Integer pCategoryId, Integer categoryId, Integer pageNo) {
        VideoInfoQuery query = new VideoInfoQuery();
        if (pCategoryId != null && pCategoryId > 0) {
            query.setPCategoryId(pCategoryId);
        }
        if (categoryId != null && categoryId > 0) {
            query.setCategoryId(categoryId);
        }
        query.setStatus(1);
        query.setIsDeleted(0);
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
        VideoInfo video = this.videoInfoMapper.selectByVideoId(videoId);
        if (video != null) {
            VideoInfo updateInfo = new VideoInfo();
            Long currentPlay = video.getPlayCount() == null ? 0L : video.getPlayCount();
            updateInfo.setPlayCount(currentPlay + 1);
            this.videoInfoMapper.updateByVideoId(updateInfo, videoId);
            video.setPlayCount(currentPlay + 1);
        }
        return video;
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
        return searchWithOrder(keyword, "play_count desc");
    }

    public List<VideoInfo> searchWithOrder(String keyword, String orderBy) {
        VideoInfoQuery query = new VideoInfoQuery();
        query.setVideoNameFuzzy(keyword);
        query.setStatus(1);
        query.setIsDeleted(0);
        query.setOrderBy(orderBy != null ? orderBy : "play_count desc");
        query.setSimplePage(new SimplePage(0, PageSize.SIZE20.getSize()));
        List<VideoInfo> list = this.videoInfoMapper.selectList(query);
        // Also search by tags if name search yields few results
        if (list.size() < 10) {
            VideoInfoQuery tagQuery = new VideoInfoQuery();
            tagQuery.setTagsFuzzy(keyword);
            tagQuery.setStatus(1);
            tagQuery.setIsDeleted(0);
            tagQuery.setOrderBy("play_count desc");
            tagQuery.setSimplePage(new SimplePage(0, PageSize.SIZE20.getSize()));
            List<VideoInfo> tagList = this.videoInfoMapper.selectList(tagQuery);
            for (VideoInfo v : tagList) {
                if (list.size() >= 20) break;
                if (!list.contains(v)) list.add(v);
            }
        }
        // Also search by matching category name
        CategoryInfoQuery catQuery = new CategoryInfoQuery();
        catQuery.setCategoryNameFuzzy(keyword);
        List<CategoryInfo> categories = this.categoryInfoMapper.selectList(catQuery);
        if (categories != null && !categories.isEmpty()) {
            for (CategoryInfo cat : categories) {
                if (list.size() >= 20) break;
                VideoInfoQuery catVideoQuery = new VideoInfoQuery();
                catVideoQuery.setCategoryId(cat.getCategoryId());
                catVideoQuery.setStatus(1);
                catVideoQuery.setIsDeleted(0);
                catVideoQuery.setOrderBy(orderBy != null ? orderBy : "play_count desc");
                catVideoQuery.setSimplePage(new SimplePage(0, PageSize.SIZE20.getSize()));
                List<VideoInfo> catList = this.videoInfoMapper.selectList(catVideoQuery);
                for (VideoInfo v : catList) {
                    if (list.size() >= 20) break;
                    if (!list.contains(v)) list.add(v);
                }
            }
        }
        return list;
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
        VideoInfoQuery query = new VideoInfoQuery();
        query.setCategoryId(currentVideo.getCategoryId());
        query.setStatus(1);
        query.setIsDeleted(0);
        query.setOrderBy("(play_count + like_count * 2 + coin_count * 3) desc");
        query.setSimplePage(new SimplePage(0, PageSize.SIZE15.getSize()));
        List<VideoInfo> list = this.videoInfoMapper.selectList(query);
        list.removeIf(v -> v.getVideoId().equals(videoId));
        if (list.size() < 10) {
            VideoInfoQuery hotQuery = new VideoInfoQuery();
            hotQuery.setStatus(1);
            hotQuery.setIsDeleted(0);
            hotQuery.setOrderBy("play_count desc");
            hotQuery.setSimplePage(new SimplePage(0, PageSize.SIZE15.getSize()));
            List<VideoInfo> hotList = this.videoInfoMapper.selectList(hotQuery);
            hotList.removeIf(v -> v.getVideoId().equals(videoId));
            for (VideoInfo v : hotList) {
                if (list.size() >= 15) break;
                if (!list.contains(v)) list.add(v);
            }
        }
        return list;
    }

    /**
     * 加载热门视频列表
     */
    @Override
    public List<VideoInfo> loadHotVideoList() {
        VideoInfoQuery query = new VideoInfoQuery();
        query.setStatus(1);
        query.setIsDeleted(0);
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
        query.setIsDeleted(0);
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
        this.videoInfoMapper.updateByVideoId(updateInfo, videoId);

        if (videoAuditLogService != null) {
            videoAuditLogService.logAudit(videoId, "admin", videoInfo.getStatus(), status, reason);
        }
    }

    /**
     * 删除视频（软删除）
     */
    @Override
    public void deleteVideo(String videoId) {
        VideoInfo videoInfo = this.videoInfoMapper.selectByVideoId(videoId);
        if (videoInfo == null) {
            throw new BusinessException("视频不存在");
        }
        VideoInfo updateInfo = new VideoInfo();
        updateInfo.setIsDeleted(1);
        updateInfo.setUpdateTime(new Date());
        this.videoInfoMapper.updateByVideoId(updateInfo, videoId);
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
        videoInfo.setIsDeleted(0);
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

        // 自动发布视频动态 (dynamicType=4)
        UserDynamic dynamic = new UserDynamic();
        dynamic.setUserId(videoInfo.getUserId());
        dynamic.setDynamicType(4);
        dynamic.setVideoId(videoInfo.getVideoId());
        dynamic.setContent("发布了新视频: " + videoInfo.getVideoName());
        dynamic.setCreateTime(new Date());
        dynamic.setUpdateTime(new Date());
        this.userDynamicMapper.insert(dynamic);
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

    @Override
    public void addCoinCount(String videoId, Integer count) {
        // TODO: implement add coin count logic
    }
}