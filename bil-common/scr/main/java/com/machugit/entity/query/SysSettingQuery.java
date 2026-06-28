package com.machugit.entity.query;



/**
 * 系统设置表参数
 */
public class SysSettingQuery extends BaseParam {


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

    private String sysNameFuzzy;


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

    public void setSysNameFuzzy(String sysNameFuzzy){
        this.sysNameFuzzy = sysNameFuzzy;
    }

    public String getSysNameFuzzy(){
        return this.sysNameFuzzy;
    }

}
