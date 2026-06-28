package com.machugit.entity.po;

import java.io.Serializable;


/**
 * 系统设置表
 */
public class SysSetting implements Serializable {


    /**
     * id
     */
    private Integer id;

    /**
     * 最大文件大小
     */
    private Long maxFileSize;

    /**
     * 最大分片大小
     */
    private Long maxChunkSize;

    /**
     * 评论开关（0：关闭 1：开启）
     */
    private Integer commentOpen;

    /**
     * 弹幕开关（0：关闭 1：开启）
     */
    private Integer danmuOpen;

    /**
     * 视频审核开关（0：关闭 1：开启）
     */
    private Integer videoAudit;

    /**
     * 注册开关（0：关闭 1：开启）
     */
    private Integer registerOpen;

    /**
     * 系统名称
     */
    private String sysName;


    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return this.id;
    }

    public void setMaxFileSize(Long maxFileSize){
        this.maxFileSize = maxFileSize;
    }

    public Long getMaxFileSize(){
        return this.maxFileSize;
    }

    public void setMaxChunkSize(Long maxChunkSize){
        this.maxChunkSize = maxChunkSize;
    }

    public Long getMaxChunkSize(){
        return this.maxChunkSize;
    }

    public void setCommentOpen(Integer commentOpen){
        this.commentOpen = commentOpen;
    }

    public Integer getCommentOpen(){
        return this.commentOpen;
    }

    public void setDanmuOpen(Integer danmuOpen){
        this.danmuOpen = danmuOpen;
    }

    public Integer getDanmuOpen(){
        return this.danmuOpen;
    }

    public void setVideoAudit(Integer videoAudit){
        this.videoAudit = videoAudit;
    }

    public Integer getVideoAudit(){
        return this.videoAudit;
    }

    public void setRegisterOpen(Integer registerOpen){
        this.registerOpen = registerOpen;
    }

    public Integer getRegisterOpen(){
        return this.registerOpen;
    }

    public void setSysName(String sysName){
        this.sysName = sysName;
    }

    public String getSysName(){
        return this.sysName;
    }

    @Override
    public String toString (){
        return "id:"+(id == null ? "空" : id)+"，最大文件大小:"+(maxFileSize == null ? "空" : maxFileSize)+"，最大分片大小:"+(maxChunkSize == null ? "空" : maxChunkSize)+"，评论开关（0：关闭 1：开启）:"+(commentOpen == null ? "空" : commentOpen)+"，弹幕开关（0：关闭 1：开启）:"+(danmuOpen == null ? "空" : danmuOpen)+"，视频审核开关（0：关闭 1：开启）:"+(videoAudit == null ? "空" : videoAudit)+"，注册开关（0：关闭 1：开启）:"+(registerOpen == null ? "空" : registerOpen)+"，系统名称:"+(sysName == null ? "空" : sysName);
    }
}
