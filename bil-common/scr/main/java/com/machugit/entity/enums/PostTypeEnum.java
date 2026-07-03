package com.machugit.entity.enums;

public enum PostTypeEnum {
    ORIGINAL(1, "自制"),
    REPRINT(2, "转载");

    private Integer type;
    private String desc;

    PostTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() { return type; }
    public String getDesc() { return desc; }

    public static PostTypeEnum getByType(Integer type) {
        for (PostTypeEnum e : values()) {
            if (e.getType().equals(type)) return e;
        }
        return ORIGINAL;
    }
}
