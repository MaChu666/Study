package com.machugit.entity.enums;

public enum UserSexEnum {
    UNKNOWN(0, "未知"),
    MAN(1, "男"),
    WOMAN(2, "女");

    private Integer sex;
    private String desc;

    UserSexEnum(Integer sex, String desc) {
        this.sex = sex;
        this.desc = desc;
    }

    public static UserSexEnum getBySex(Integer sex) {
        for (UserSexEnum item : UserSexEnum.values()) {
            if (item.getSex().equals(sex)){
                return item;
            }
        }
        return null;
    }
    public Integer getSex() {return sex;}
    public String getDesc() {return desc;}
}
