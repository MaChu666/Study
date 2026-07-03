package com.machugit.entity.es;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "bil_user")
@Setting(settingPath = "es/bil-user-setting.json")
public class UserDoc {

    @Id
    private String userId;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String useName;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String personProfile;

    @Field(type = FieldType.Keyword)
    private String avatar;

    @Field(type = FieldType.Integer)
    private Integer sex;

    @Field(type = FieldType.Keyword)
    private String school;

    @Field(type = FieldType.Integer)
    private Integer fansCount;

    @Field(type = FieldType.Integer)
    private Integer followCount;

    @Field(type = FieldType.Integer)
    private Integer totalCoinCount;

    public UserDoc() {}

    public static UserDoc from(com.machugit.entity.po.UserInfo u) {
        UserDoc doc = new UserDoc();
        doc.userId = u.getUserId();
        doc.useName = u.getUseName();
        doc.personProfile = u.getPersonProfile();
        doc.avatar = u.getAvatar();
        doc.sex = u.getSex();
        doc.school = u.getSchool();
        doc.totalCoinCount = u.getTotalCoinCount();
        return doc;
    }

    // getters & setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUseName() { return useName; }
    public void setUseName(String useName) { this.useName = useName; }
    public String getPersonProfile() { return personProfile; }
    public void setPersonProfile(String personProfile) { this.personProfile = personProfile; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Integer getSex() { return sex; }
    public void setSex(Integer sex) { this.sex = sex; }
    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }
    public Integer getFansCount() { return fansCount; }
    public void setFansCount(Integer fansCount) { this.fansCount = fansCount; }
    public Integer getFollowCount() { return followCount; }
    public void setFollowCount(Integer followCount) { this.followCount = followCount; }
    public Integer getTotalCoinCount() { return totalCoinCount; }
    public void setTotalCoinCount(Integer totalCoinCount) { this.totalCoinCount = totalCoinCount; }
}