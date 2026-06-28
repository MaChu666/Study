package com.machugit.entity.constants;

public class Constants {
    //密码规则
    public static final String REGEX_PASSWORD = "^(?=.*[a-zA-Z])(?=.*[0-9])[0-9A-Za-z!@#$%^&*_+]{6,20}$";
    //失效时间（1分钟）
    public static final Integer REDIS_KEY_EXPIRE_TIME_ONE_MIN = 60000;
    //失效时间（1天）
    public static final Integer REDIS_KEY_EXPIRE_TIME_ONE_DAY = REDIS_KEY_EXPIRE_TIME_ONE_MIN*60*24;
    //失效时间天（秒为单位）
    public static final Integer TIME_SECONDS_ONE_DAY = 60*60*24;
    //redis前缀
    public static final String REDIS_KEY_PREFIX = "machugit";
    //校验码
    public static String REDIS_KEY_CHECK_CODE =REDIS_KEY_PREFIX+ "checkCode:";

    public static final Integer length_10 = 10;

    public static final Integer ZERO = 0;
    public static final Integer ONE = 1;

    public static final String REDIS_KEY_TOKEN_WEB= REDIS_KEY_PREFIX+"token:web:";
    public static final String REDIS_KEY_TOKEN_ADMIN= REDIS_KEY_PREFIX+"token:admin:";
    public static final String TOKEN_WEB="thoken";

    //管理员用户ID列表
    public static final String[] ADMIN_USER_IDS = {"5896561791"};

    public static boolean isAdmin(String userId) {
        if (userId == null) return false;
        for (String adminId : ADMIN_USER_IDS) {
            if (adminId.equals(userId)) return true;
        }
        return false;
    }

}
