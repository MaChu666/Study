package com.machugit.entity.po;

import java.io.Serializable;


/**
 * 系列视频表
 */
public class SeriesVideo implements Serializable {


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

    public void setSort(Integer sort){
        this.sort = sort;
    }

    public Integer getSort(){
        return this.sort;
    }

    @Override
    public String toString (){
        return "id:"+(id == null ? "空" : id)+"，系列id:"+(seriesId == null ? "空" : seriesId)+"，视频id:"+(videoId == null ? "空" : videoId)+"，排序:"+(sort == null ? "空" : sort);
    }
}
