package com.machugit.service.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.machugit.entity.constants.Constants;
import com.machugit.entity.po.VideoInfoFile;
import com.machugit.entity.query.VideoInfoFileQuery;
import com.machugit.mappers.VideoInfoFileMapper;
import com.machugit.service.VideoInfoFileService;
import com.machugit.utils.StringTools;

/**
 * 视频文件信息 业务接口实现
 */
@Service("videoInfoFileService")
public class VideoInfoFileServiceImpl implements VideoInfoFileService {

    @Resource
    private VideoInfoFileMapper<VideoInfoFile, VideoInfoFileQuery> videoInfoFileMapper;

    /**
     * 根据文件名获取文件
     */
    @Override
    public VideoInfoFile getResource(String sourceName) {
        return this.videoInfoFileMapper.selectByFileId(sourceName);
    }

    /**
     * 预上传视频
     */
    @Override
    public VideoInfoFile preUploadVideo(String fileName, Integer chunks) {
        VideoInfoFile bean = new VideoInfoFile();
        bean.setFileId(StringTools.getRandomNumber(Constants.length_10));
        bean.setFileName(fileName);
        bean.setStatus(0);
        bean.setCreateTime(new Date());
        bean.setUploadId(UUID.randomUUID().toString());
        this.videoInfoFileMapper.insert(bean);
        return bean;
    }

    /**
     * 上传视频分片
     */
    @Override
    public void uploadVideo(MultipartFile chunkFile, Integer chunkIndex, String uploadId) {
        try {
            String uploadDir = "d:/BilBil/bilweb/temp/" + uploadId;
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File dest = new File(uploadDir + "/" + chunkIndex);
            chunkFile.transferTo(dest);
        } catch (Exception e) {
            throw new RuntimeException("分片上传失败", e);
        }
    }

    /**
     * 删除上传视频
     */
    @Override
    public void delUploadVideo(String uploadId) {
        // TODO 清除已上传的分片文件和上传记录
        this.videoInfoFileMapper.deleteByUploadId(uploadId);
    }

    /**
     * 上传图片
     */
    @Override
    public VideoInfoFile uploadImage(String file, Boolean createThumbnail) {
        VideoInfoFile bean = new VideoInfoFile();
        String fileId = StringTools.getRandomNumber(Constants.length_10);
        bean.setFileId(fileId);
        bean.setFileName(fileId);
        bean.setFilePath("/images/" + fileId);
        bean.setStatus(1);
        bean.setCreateTime(new Date());
        this.videoInfoFileMapper.insert(bean);
        return bean;
    }

    /**
     * 根据文件ID获取视频文件
     */
    @Override
    public VideoInfoFile getVideoResource(String fileId) {
        return this.videoInfoFileMapper.selectByFileId(fileId);
    }

    /**
     * 获取TS文件
     */
    @Override
    public VideoInfoFile getVideoTs(String fileId, String ts) {
        VideoInfoFileQuery query = new VideoInfoFileQuery();
        query.setFileId(fileId);
        // TODO 根据fileId和ts片段号查找对应的TS文件记录
        return this.videoInfoFileMapper.selectByFileId(fileId);
    }

}
