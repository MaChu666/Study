package com.machugit.entity.query;


/**
 * 系列视频表参数
 */
public class SeriesVideoQuery extends BaseParam {


    /**
     * id
     */
    private Integer id;

    /**
     * 系列id
     */
    private Integer seriesId;

    /**
     * 视频id
     */
    private String videoId;

    private String videoIdFuzzy;

    /**
     * 排序
     */
    private Integer sort;


    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return this.id;
    }

    public void setSeriesId(Integer seriesId){
        this.seriesId = seriesId;
    }

    public Integer getSeriesId(){
        return this.seriesId;
    }

    public void setVideoId(String videoId){
        this.videoId = videoId;
    }

    public String getVideoId(){
        return this.videoId;
    }

    public void setVideoIdFuzzy(String videoIdFuzzy){
        this.videoIdFuzzy = videoIdFuzzy;
    }

    public String getVideoIdFuzzy(){
        return this.videoIdFuzzy;
    }

    public void setSort(Integer sort){
        this.sort = sort;
    }

    public Integer getSort(){
        return this.sort;
    }

}
