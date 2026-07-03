package com.machugit.entity.query;

import java.util.Date;


/**
 * 用户信息表参数
 */
public class UserInfoQuery extends BaseParam {


	/**
	 * 用户id
	 */
	private String userId;

	private String userIdFuzzy;

	/**
	 * 昵称
	 */
	private String useName;

	private String useNameFuzzy;

	/**
	 * 头像
	 */
	private String avatar;

	private String avatarFuzzy;

	/**
	 * 个人背景图
	 */
	private String bannerImage;

	private String bannerImageFuzzy;

	/**
	 * 邮箱
	 */
	private String email;

	private String emailFuzzy;

	/**
	 * 密码
	 */
	private String password;

	private String passwordFuzzy;

	/**
	 * 性别（0：未知 1：男 2：女）
	 */
	private Integer sex;

	/**
	 * 出生日期
	 */
	private String birthday;

	private String birthdayFuzzy;

	/**
	 * 学校
	 */
	private String school;

	private String schoolFuzzy;

	/**
	 * 个人简介
	 */
	private String personProfile;

	private String personProfileFuzzy;

	/**
	 * 注册时间
	 */
	private String joinTime;

	private String joinTimeStart;

	private String joinTimeEnd;

	/**
	 * 最后登录时间
	 */
	private String lastLoginTime;

	private String lastLoginTimeStart;

	private String lastLoginTimeEnd;

	/**
	 * 最后登录ip
	 */
	private String lastLoginIp;

	private String lastLoginIpFuzzy;

	/**
	 * 状态（0：封禁 1：正常）
	 */
	private Integer status;

	/**
	 * 空间公告
	 */
	private String noticeInfo;

	private String noticeInfoFuzzy;

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
	 * 大会员类型（0：无 1：月度 2：季度 3：年度）
	 */
	private Integer vipType;

	/**
	 * 大会员过期时间
	 */
	private String vipExpireTime;

	private String vipExpireTimeStart;

	private String vipExpireTimeEnd;

	/**
	 * 直播状态（0：离线 1：直播中）
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

	public void setUserIdFuzzy(String userIdFuzzy){
		this.userIdFuzzy = userIdFuzzy;
	}

	public String getUserIdFuzzy(){
		return this.userIdFuzzy;
	}

	public void setUseName(String useName){
		this.useName = useName;
	}

	public String getUseName(){
		return this.useName;
	}

	public void setUseNameFuzzy(String useNameFuzzy){
		this.useNameFuzzy = useNameFuzzy;
	}

	public String getUseNameFuzzy(){
		return this.useNameFuzzy;
	}

	public void setAvatar(String avatar){
		this.avatar = avatar;
	}

	public String getAvatar(){
		return this.avatar;
	}

	public void setAvatarFuzzy(String avatarFuzzy){
		this.avatarFuzzy = avatarFuzzy;
	}

	public String getAvatarFuzzy(){
		return this.avatarFuzzy;
	}

	public void setBannerImage(String bannerImage){
		this.bannerImage = bannerImage;
	}

	public String getBannerImage(){
		return this.bannerImage;
	}

	public void setBannerImageFuzzy(String bannerImageFuzzy){
		this.bannerImageFuzzy = bannerImageFuzzy;
	}

	public String getBannerImageFuzzy(){
		return this.bannerImageFuzzy;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return this.email;
	}

	public void setEmailFuzzy(String emailFuzzy){
		this.emailFuzzy = emailFuzzy;
	}

	public String getEmailFuzzy(){
		return this.emailFuzzy;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return this.password;
	}

	public void setPasswordFuzzy(String passwordFuzzy){
		this.passwordFuzzy = passwordFuzzy;
	}

	public String getPasswordFuzzy(){
		return this.passwordFuzzy;
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

	public void setBirthdayFuzzy(String birthdayFuzzy){
		this.birthdayFuzzy = birthdayFuzzy;
	}

	public String getBirthdayFuzzy(){
		return this.birthdayFuzzy;
	}

	public void setSchool(String school){
		this.school = school;
	}

	public String getSchool(){
		return this.school;
	}

	public void setSchoolFuzzy(String schoolFuzzy){
		this.schoolFuzzy = schoolFuzzy;
	}

	public String getSchoolFuzzy(){
		return this.schoolFuzzy;
	}

	public void setPersonProfile(String personProfile){
		this.personProfile = personProfile;
	}

	public String getPersonProfile(){
		return this.personProfile;
	}

	public void setPersonProfileFuzzy(String personProfileFuzzy){
		this.personProfileFuzzy = personProfileFuzzy;
	}

	public String getPersonProfileFuzzy(){
		return this.personProfileFuzzy;
	}

	public void setJoinTime(String joinTime){
		this.joinTime = joinTime;
	}

	public String getJoinTime(){
		return this.joinTime;
	}

	public void setJoinTimeStart(String joinTimeStart){
		this.joinTimeStart = joinTimeStart;
	}

	public String getJoinTimeStart(){
		return this.joinTimeStart;
	}
	public void setJoinTimeEnd(String joinTimeEnd){
		this.joinTimeEnd = joinTimeEnd;
	}

	public String getJoinTimeEnd(){
		return this.joinTimeEnd;
	}

	public void setLastLoginTime(String lastLoginTime){
		this.lastLoginTime = lastLoginTime;
	}

	public String getLastLoginTime(){
		return this.lastLoginTime;
	}

	public void setLastLoginTimeStart(String lastLoginTimeStart){
		this.lastLoginTimeStart = lastLoginTimeStart;
	}

	public String getLastLoginTimeStart(){
		return this.lastLoginTimeStart;
	}
	public void setLastLoginTimeEnd(String lastLoginTimeEnd){
		this.lastLoginTimeEnd = lastLoginTimeEnd;
	}

	public String getLastLoginTimeEnd(){
		return this.lastLoginTimeEnd;
	}

	public void setLastLoginIp(String lastLoginIp){
		this.lastLoginIp = lastLoginIp;
	}

	public String getLastLoginIp(){
		return this.lastLoginIp;
	}

	public void setLastLoginIpFuzzy(String lastLoginIpFuzzy){
		this.lastLoginIpFuzzy = lastLoginIpFuzzy;
	}

	public String getLastLoginIpFuzzy(){
		return this.lastLoginIpFuzzy;
	}

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

	public void setNoticeInfoFuzzy(String noticeInfoFuzzy){
		this.noticeInfoFuzzy = noticeInfoFuzzy;
	}

	public String getNoticeInfoFuzzy(){
		return this.noticeInfoFuzzy;
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

	public void setLevel(Integer level){
		this.level = level;
	}

	public Integer getLevel(){
		return this.level;
	}

	public void setExp(Integer exp){
		this.exp = exp;
	}

	public Integer getExp(){
		return this.exp;
	}

	public void setVipType(Integer vipType){
		this.vipType = vipType;
	}

	public Integer getVipType(){
		return this.vipType;
	}

	public void setVipExpireTime(String vipExpireTime){
		this.vipExpireTime = vipExpireTime;
	}

	public String getVipExpireTime(){
		return this.vipExpireTime;
	}

	public void setVipExpireTimeStart(String vipExpireTimeStart){
		this.vipExpireTimeStart = vipExpireTimeStart;
	}

	public String getVipExpireTimeStart(){
		return this.vipExpireTimeStart;
	}

	public void setVipExpireTimeEnd(String vipExpireTimeEnd){
		this.vipExpireTimeEnd = vipExpireTimeEnd;
	}

	public String getVipExpireTimeEnd(){
		return this.vipExpireTimeEnd;
	}

	public void setLiveStatus(Integer liveStatus){
		this.liveStatus = liveStatus;
	}

	public Integer getLiveStatus(){
		return this.liveStatus;
	}

	public void setTheme(Integer theme){
		this.theme = theme;
	}

	public Integer getTheme(){
		return this.theme;
	}

}
