package com.machugit.entity.vo;

import com.machugit.entity.po.CommentInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentVO extends CommentInfo {
    private String videoName;
    private String userName;
}