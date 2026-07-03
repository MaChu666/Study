package com.machugit.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    private static final Logger logger = LoggerFactory.getLogger(VideoInfoFileServiceImpl.class);

    @Value("${project.video.upload-temp}")
    private String uploadTempDir;

    @Value("${project.video.output}")
    private String videoOutputDir;

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
            String uploadDir = uploadTempDir + "/" + uploadId;
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
     * 合并分片并转码为 HLS
     */
    @Override
    public void completeUpload(String uploadId, String fileId) {
        new Thread(() -> {
            try {
                doTranscode(uploadId, fileId);
            } catch (Exception e) {
                logger.error("转码失败 uploadId={}, fileId={}: {}", uploadId, fileId, e.getMessage());
            }
        }).start();
        logger.info("转码任务已提交 uploadId={}, fileId={}", uploadId, fileId);
    }

    private void doTranscode(String uploadId, String fileId) throws Exception {
        String uploadDir = uploadTempDir + "/" + uploadId;
        String outputDir = videoOutputDir + "/" + fileId;
        File outputDirFile = new File(outputDir);
        outputDirFile.mkdirs();

        logger.info("开始转码 fileId={}", fileId);

        // Merge raw chunks into a single file
        String mergedFile = uploadDir + "/merged.tmp";
        java.io.FileOutputStream fos = new java.io.FileOutputStream(mergedFile);
        File dir = new File(uploadDir);
        File[] chunks = dir.listFiles();
        if (chunks != null) {
            java.util.Arrays.sort(chunks, (a, b) -> {
                try {
                    return Integer.compare(Integer.parseInt(a.getName()), Integer.parseInt(b.getName()));
                } catch (NumberFormatException e) {
                    return a.getName().compareTo(b.getName());
                }
            });
            for (File chunk : chunks) {
                if (chunk.isFile() && !chunk.getName().endsWith(".tmp") && !chunk.getName().endsWith(".txt")) {
                    java.nio.file.Files.copy(chunk.toPath(), fos);
                }
            }
        }
        fos.close();
        logger.info("分片合并完成 fileId={}", fileId);

        // Move merged file to output directory
        String outputFile = outputDir + "/video.mp4";
        java.nio.file.Files.move(
            java.nio.file.Paths.get(mergedFile),
            java.nio.file.Paths.get(outputFile),
            java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        logger.info("合并完成 fileId={}", fileId);

        // Extract video duration via ffprobe
        Integer duration = getVideoDuration(outputFile);
        logger.info("视频时长 fileId={}, duration={}秒", fileId, duration);

        VideoInfoFile updateBean = new VideoInfoFile();
        updateBean.setFilePath("/videos/" + fileId + "/video.mp4");
        updateBean.setDuration(duration);
        updateBean.setStatus(2);
        this.videoInfoFileMapper.updateByFileId(updateBean, fileId);

        // Clean up temp files
        deleteDirectory(new File(uploadDir));
        logger.info("转码任务全部完成 fileId={}", fileId);
    }

    private Integer getVideoDuration(String filePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("ffprobe",
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                filePath);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = reader.readLine();
            reader.close();
            p.waitFor();
            if (line != null && !line.isEmpty()) {
                double seconds = Double.parseDouble(line.trim());
                return (int) Math.round(seconds);
            }
        } catch (Exception e) {
            logger.warn("获取视频时长失败: {}", e.getMessage());
        }
        return null;
    }

    private void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteDirectory(f);
                }
            }
        }
        dir.delete();
    }

    /**
     * 删除上传视频
     */
    @Override
    public void delUploadVideo(String uploadId) {
        this.videoInfoFileMapper.deleteByUploadId(uploadId);
        deleteDirectory(new File(uploadTempDir + "/" + uploadId));
    }

    /**
     * 上传图片
     */
    @Override
    public VideoInfoFile uploadImage(String file, Boolean createThumbnail) {
        VideoInfoFile bean = new VideoInfoFile();
        String fileId = StringTools.getRandomNumber(Constants.length_10);
        String ext = "png";
        try {
            if (file.startsWith("data:image/")) {
                int colon = file.indexOf(":");
                int semicolon = file.indexOf(";");
                if (colon >= 0 && semicolon > colon) {
                    String mime = file.substring(colon + 1, semicolon);
                    if (mime.contains("/")) ext = mime.substring(mime.indexOf("/") + 1);
                }
                int comma = file.indexOf(",");
                if (comma >= 0) {
                    byte[] bytes = java.util.Base64.getDecoder().decode(file.substring(comma + 1));
                    File imgDir = new File(videoOutputDir.replace("videos", "images"));
                    imgDir.mkdirs();
                    java.nio.file.Files.write(java.nio.file.Paths.get(imgDir.getPath(), fileId + "." + ext), bytes);
                }
            }
        } catch (Exception e) {
            logger.error("保存图片失败: {}", e.getMessage());
        }
        bean.setFileId(fileId);
        bean.setFileName(fileId + "." + ext);
        bean.setFilePath("/images/" + fileId + "." + ext);
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
