package com.machugit.service;

import com.machugit.entity.po.VideoInfoFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频文件信息 业务接口
 */
public interface VideoInfoFileService {

    /**
     * 根据文件名获取文件
     */
    VideoInfoFile getResource(String sourceName);

    /**
     * 预上传视频
     */
    VideoInfoFile preUploadVideo(String fileName, Integer chunks);

    /**
     * 上传视频分片
     */
    void uploadVideo(MultipartFile chunkFile, Integer chunkIndex, String uploadId);

    /**
     * 删除上传视频
     */
    void delUploadVideo(String uploadId);

    /**
     * 上传图片
     */
    VideoInfoFile uploadImage(String file, Boolean createThumbnail);

    /**
     * 根据文件ID获取视频文件
     */
    VideoInfoFile getVideoResource(String fileId);

    /**
     * 获取TS文件
     */
    VideoInfoFile getVideoTs(String fileId, String ts);

}
