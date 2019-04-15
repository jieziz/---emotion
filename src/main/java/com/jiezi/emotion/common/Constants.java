package com.jiezi.emotion.common;

public class Constants {


    /**
     * 地址
     */
    public static String LOGIN_URL = "login";
    
    public static String INDEX_URL = "index";

    public static String INDEX_BACK_URL = "backIndex";

    /**
     * 不需要拦截的地址
     */
    public static String CURRENT_URL = "view";


    /**
     * 当前登录的用户
     */
    public static String CURRENT_USER = "user";
    public static String CURRENT_USERNAME = "username";

    public static String ENCODING = "UTF-8";
    
    public static final String PARAM_TOKEN = "Token";
    public static final String PARAM_USERNAME = "username";
    
    
    /**
     * redis-OK
     */
    public final static String OK = "OK";

    /**
     * redis过期时间，以秒为单位，一分钟
     */
    public final static int EXRP_MINUTE = 60;

    /**
     * redis过期时间，以秒为单位，一小时
     */
    public final static int EXRP_HOUR = 60 * 60;

    /**
     * redis过期时间，以秒为单位，一天
     */
    public final static int EXRP_DAY = 60 * 60 * 24;

    /**
     * redis-key-前缀-shiro:cache:
     * shiro里面的权限缓存
     */
    public final static String PREFIX_SHIRO_CACHE = "shiro:cache:";

    /**
     * redis-key-前缀-shiro:access_token:
     *　access token 是客户端访问资源服务器的令牌
     */
    public final static String PREFIX_SHIRO_ACCESS_TOKEN = "shiro:access_token:";

    /**
     * redis-key-前缀-shiro:refresh_token:
     * refresh token 的作用 ：refresh token的时长会很长，当refresh token未过期时，可以用来刷新 access token
     */
    public final static String PREFIX_SHIRO_REFRESH_TOKEN = "shiro:refresh_token:";

    /**
     * JWT-account:
     */
    public final static String ACCOUNT = "account";

    /**
     * JWT-currentTimeMillis:
     */
    public final static String CURRENT_TIME_MILLIS = "currentTimeMillis";

    

    


}
