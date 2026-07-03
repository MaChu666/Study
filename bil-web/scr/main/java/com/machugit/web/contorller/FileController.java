package com.machugit.web.contorller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.machugit.entity.po.VideoInfoFile;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.VideoInfoFileServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/file")
@Validated
public class FileController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Resource
    private VideoInfoFileServiceImpl videoInfoFileService;

    /**
     * 获取视频资源文件
     */
    @RequestMapping("/getResource")
    public ResponseVO getResource(@NotEmpty String sourceName) {
        VideoInfoFile videoInfoFile = videoInfoFileService.getResource(sourceName);
        return getSuccessResponseVO(videoInfoFile);
    }

    /**
     * 预上传视频
     */
    @RequestMapping("/preUploadVideo")
    public ResponseVO preUploadVideo(@NotEmpty String fileName, @NotEmpty String chunks) {
        VideoInfoFile bean = videoInfoFileService.preUploadVideo(fileName, Integer.valueOf(chunks));
        return getSuccessResponseVO(bean);
    }

    /**
     * 上传视频分片
     */
    @RequestMapping("/uploadVideo")
    public ResponseVO uploadVideo(MultipartFile chunkFile, @NotEmpty String chunkIndex, @NotEmpty String uploadId) {
        if (chunkFile == null || chunkFile.isEmpty()) {
            throw new BusinessException("分片文件不能为空");
        }
        videoInfoFileService.uploadVideo(chunkFile, Integer.valueOf(chunkIndex), uploadId);
        return getSuccessResponseVO(null);
    }

    /**
     * 合并分片并转码
     */
    @RequestMapping("/completeUpload")
    public ResponseVO completeUpload(@NotEmpty String uploadId, @NotEmpty String fileId) {
        videoInfoFileService.completeUpload(uploadId, fileId);
        return getSuccessResponseVO(null);
    }

    /**
     * 删除上传视频
     */
    @RequestMapping("/delUploadVideo")
    public ResponseVO delUploadVideo(@NotEmpty String uploadId) {
        videoInfoFileService.delUploadVideo(uploadId);
        return getSuccessResponseVO(null);
    }

    /**
     * 上传图片
     */
    @RequestMapping("/uploadImage")
    public ResponseVO uploadImage(@NotEmpty String file, String createThumbnail) {
        VideoInfoFile bean = videoInfoFileService.uploadImage(file, "true".equals(createThumbnail));
        return getSuccessResponseVO(bean);
    }

    /**
     * 获取视频资源
     */
    @RequestMapping("/videoResource/{fileId}")
    public ResponseVO videoResource(@PathVariable @NotEmpty String fileId) {
        VideoInfoFile videoInfoFile = videoInfoFileService.getVideoResource(fileId);
        return getSuccessResponseVO(videoInfoFile);
    }

    /**
     * 获取视频TS文件
     */
    @RequestMapping("/videoResourceTs/{fileId}/{ts}")
    public ResponseVO videoResourceTs(@PathVariable @NotEmpty String fileId, @PathVariable @NotEmpty String ts) {
        VideoInfoFile videoInfoFile = videoInfoFileService.getVideoTs(fileId, ts);
        return getSuccessResponseVO(videoInfoFile);
    }
}
