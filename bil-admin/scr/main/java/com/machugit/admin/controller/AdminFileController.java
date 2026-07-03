package com.machugit.admin.controller;

import com.machugit.entity.po.VideoInfoFile;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.service.impl.VideoInfoFileServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/file")
@Validated
public class AdminFileController extends ABaseAdminController {

    @Resource
    private VideoInfoFileServiceImpl videoInfoFileService;

    @RequestMapping("/uploadImage")
    public ResponseVO uploadImage(@NotEmpty String file,
                                  @NotEmpty String createThumbnail) {
        VideoInfoFile bean = videoInfoFileService.uploadImage(file, Boolean.parseBoolean(createThumbnail));
        return getSuccessResponseVO(bean);
    }

    @RequestMapping("/getResource")
    public ResponseVO getResource(@NotEmpty String sourceName) {
        VideoInfoFile videoInfoFile = videoInfoFileService.getResource(sourceName);
        return getSuccessResponseVO(videoInfoFile);
    }

    @RequestMapping("/videoResource/{fileId}")
    public ResponseVO videoResource(@NotEmpty @PathVariable("fileId") String fileId) {
        VideoInfoFile videoInfoFile = videoInfoFileService.getVideoResource(fileId);
        return getSuccessResponseVO(videoInfoFile);
    }

    @RequestMapping("/videoResourceTs/{fileId}/{ts}")
    public ResponseVO videoResourceTs(@NotEmpty @PathVariable("fileId") String fileId,
                                      @NotEmpty @PathVariable("ts") String ts) {
        VideoInfoFile videoInfoFile = videoInfoFileService.getVideoTs(fileId, ts);
        return getSuccessResponseVO(videoInfoFile);
    }
}
