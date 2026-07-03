package com.machugit.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 用户信息表
 */
public class UserInfo implements Serializable {


	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 昵称
	 */
	private String useName;

	/**
	 * 头像URL
	 */
	private String avatar;

	/**
	 * 个人空间头图
	 */
	private String bannerImage;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 性别（0：未知 1：男 2：女）
	 */
	private Integer sex;

	/**
	 * 出生日期
	 */
	private String birthday;

	/**
	 * 学校
	 */
	private String school;

	/**
	 * 个人简介
	 */
	private String personProfile;

	/**
	 * 注册时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date joinTime;

	/**
	 * 最后登录时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastLoginTime;

	/**
	 * 最后登录ip
	 */
	private String lastLoginIp;

	/**
	 * 最后阅读消息时间
	 */
	private Date lastReadTime;

	/**
	 * 状态（0：封禁 1：正常）
	 */
	private Integer status;

	/**
	 * 空间公告
	 */
	private String noticeInfo;

	/**
	 * 硬币总数
	 */
	private Integer totalCoinCount;

	/**
	 * 当前硬币数
	 */
	private Integer currentCoinCount;

	/**
	 * 用户等级（0-6级）
	 */
	private Integer level;

	/**
	 * 当前经验值
	 */
	private Integer exp;

	/**
	 * 大会员类型
	 */
	private Integer vipType;

	/**
	 * 大会员过期时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date vipExpireTime;

	/**
	 * 直播状态
	 */
	private Integer liveStatus;


	/**
	 * 主题
	 */
	private Integer theme;


	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return this.userId;
	}

	public void setUseName(String useName){
		this.useName = useName;
	}

	public String getUseName(){
		return this.useName;
	}

	public void setAvatar(String avatar){ this.avatar = avatar; }
	public String getAvatar(){ return this.avatar; }
	public void setBannerImage(String bannerImage){ this.bannerImage = bannerImage; }
	public String getBannerImage(){ return this.bannerImage; }

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return this.email;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return this.password;
	}

	public void setSex(Integer sex){
		this.sex = sex;
	}

	public Integer getSex(){
		return this.sex;
	}

	public void setBirthday(String birthday){
		this.birthday = birthday;
	}

	public String getBirthday(){
		return this.birthday;
	}

	public void setSchool(String school){
		this.school = school;
	}

	public String getSchool(){
		return this.school;
	}

	public void setPersonProfile(String personProfile){
		this.personProfile = personProfile;
	}

	public String getPersonProfile(){
		return this.personProfile;
	}

	public void setJoinTime(Date joinTime){
		this.joinTime = joinTime;
	}

	public Date getJoinTime(){
		return this.joinTime;
	}

	public void setLastLoginTime(Date lastLoginTime){
		this.lastLoginTime = lastLoginTime;
	}

	public Date getLastLoginTime(){
		return this.lastLoginTime;
	}

	public void setLastLoginIp(String lastLoginIp){
		this.lastLoginIp = lastLoginIp;
	}

	public String getLastLoginIp(){
		return this.lastLoginIp;
	}

	public void setLastReadTime(Date lastReadTime){ this.lastReadTime = lastReadTime; }
	public Date getLastReadTime(){ return this.lastReadTime; }

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return this.status;
	}

	public void setNoticeInfo(String noticeInfo){
		this.noticeInfo = noticeInfo;
	}

	public String getNoticeInfo(){
		return this.noticeInfo;
	}

	public void setTotalCoinCount(Integer totalCoinCount){
		this.totalCoinCount = totalCoinCount;
	}

	public Integer getTotalCoinCount(){
		return this.totalCoinCount;
	}

	public void setCurrentCoinCount(Integer currentCoinCount){
		this.currentCoinCount = currentCoinCount;
	}

	public Integer getCurrentCoinCount(){
		return this.currentCoinCount;
	}

	public void setLevel(Integer level){ this.level = level; }
	public Integer getLevel(){ return this.level; }
	public void setExp(Integer exp){ this.exp = exp; }
	public Integer getExp(){ return this.exp; }
	public void setVipType(Integer vipType){ this.vipType = vipType; }
	public Integer getVipType(){ return this.vipType; }
	public void setLiveStatus(Integer liveStatus){ this.liveStatus = liveStatus; }
	public Integer getLiveStatus(){ return this.liveStatus; }
	public void setVipExpireTime(Date vipExpireTime){ this.vipExpireTime = vipExpireTime; }
	public Date getVipExpireTime(){ return this.vipExpireTime; }

	public void setTheme(Integer theme){
		this.theme = theme;
	}

	public Integer getTheme(){
		return this.theme;
	}

	@Override
	public String toString (){
		return "用户id:"+(userId == null ? "空" : userId)+"，昵称:"+(useName == null ? "空" : useName)+"，邮箱:"+(email == null ? "空" : email)+"，密码:"+(password == null ? "空" : password)+"，性别（0：未知 1：男 2：女）:"+(sex == null ? "空" : sex)+"，出生日期:"+(birthday == null ? "空" : birthday)+"，学校:"+(school == null ? "空" : school)+"，个人简介:"+(personProfile == null ? "空" : personProfile)+"，注册时间:"+(joinTime == null ? "空" : DateUtil.format(joinTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，最后登录时间:"+(lastLoginTime == null ? "空" : DateUtil.format(lastLoginTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，最后登录ip:"+(lastLoginIp == null ? "空" : lastLoginIp)+"，状态（0：封禁 1：正常）:"+(status == null ? "空" : status)+"，空间公告:"+(noticeInfo == null ? "空" : noticeInfo)+"，硬币总数:"+(totalCoinCount == null ? "空" : totalCoinCount)+"，当前硬币数:"+(currentCoinCount == null ? "空" : currentCoinCount)+"，主题:"+(theme == null ? "空" : theme);
	}
}
