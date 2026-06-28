package com.machugit.entity.query;



/**
 * 视频文件信息表参数
 */
public class VideoInfoFileQuery extends BaseParam {


    /**
     * 文件id
     */
    private String fileId;

    /**
     * 上传id
     */
    private String uploadId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 视频id
     */
    private String videoId;

    /**
     * 文件名
     */
    private String fileName;

    private String fileNameFuzzy;

    /**
     * 文件路径
     */
    private String filePath;

    private String filePathFuzzy;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 时长（秒）
     */
    private Integer duration;

    /**
     * 状态（0：处理中 1：成功 2：失败）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;

    /**
     * 更新时间
     */
    private String updateTime;

    private String updateTimeStart;

    private String updateTimeEnd;


    public void setFileId(String fileId){
        this.fileId = fileId;
    }

    public String getFileId(){
        return this.fileId;
    }

    public void setUploadId(String uploadId){
        this.uploadId = uploadId;
    }

    public String getUploadId(){
        return this.uploadId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getUserId(){
        return this.userId;
    }

    public void setVideoId(String videoId){
        this.videoId = videoId;
    }

    public String getVideoId(){
        return this.videoId;
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    public String getFileName(){
        return this.fileName;
    }

    public void setFileNameFuzzy(String fileNameFuzzy){
        this.fileNameFuzzy = fileNameFuzzy;
    }

    public String getFileNameFuzzy(){
        return this.fileNameFuzzy;
    }

    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    public String getFilePath(){
        return this.filePath;
    }

    public void setFilePathFuzzy(String filePathFuzzy){
        this.filePathFuzzy = filePathFuzzy;
    }

    public String getFilePathFuzzy(){
        return this.filePathFuzzy;
    }

    public void setFileSize(Long fileSize){
        this.fileSize = fileSize;
    }

    public Long getFileSize(){
        return this.fileSize;
    }

    public void setDuration(Integer duration){
        this.duration = duration;
    }

    public Integer getDuration(){
        return this.duration;
    }

    public void setStatus(Integer status){
        this.status = status;
    }

    public Integer getStatus(){
        return this.status;
    }

    public void setCreateTime(String createTime){
        this.createTime = createTime;
    }

    public String getCreateTime(){
        return this.createTime;
    }

    public void setCreateTimeStart(String createTimeStart){
        this.createTimeStart = createTimeStart;
    }

    public String getCreateTimeStart(){
        return this.createTimeStart;
    }

    public void setCreateTimeEnd(String createTimeEnd){
        this.createTimeEnd = createTimeEnd;
    }

    public String getCreateTimeEnd(){
        return this.createTimeEnd;
    }

    public void setUpdateTime(String updateTime){
        this.updateTime = updateTime;
    }

    public String getUpdateTime(){
        return this.updateTime;
    }

    public void setUpdateTimeStart(String updateTimeStart){
        this.updateTimeStart = updateTimeStart;
    }

    public String getUpdateTimeStart(){
        return this.updateTimeStart;
    }

    public void setUpdateTimeEnd(String updateTimeEnd){
        this.updateTimeEnd = updateTimeEnd;
    }

    public String getUpdateTimeEnd(){
        return this.updateTimeEnd;
    }

}
