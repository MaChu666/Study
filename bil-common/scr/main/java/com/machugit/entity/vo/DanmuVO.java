package com.machugit.entity.vo;

import com.machugit.entity.po.DanmuInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DanmuVO extends DanmuInfo {
    private String videoName;   // 视频名称
    private String userName;    // 用户昵称
}