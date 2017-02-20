package com.mintcode.util;

import com.jkys.tools.Action;

public interface Keys {
    /**
     * token
     */
    public static final String TOKEN = "token";
    /**
     * token
     */
    public static final String NEW_TOKEN = "newToken";
    /**
     * sysconf-push
     */
    public static final String SYSCONF_PUSH = "sysconf-push";
    /**
     * token 失效时间
     */
    public static final String TOKEN_EXPIREAT = "token_expireAt";
    /**
     * 服务器系统时间与本地时间差
     */
    public static final String TIME_GAP = "time_gap";
    /**
     * uid
     */
    public static final String UID = "uid";
    /**
     * mobile
     */
    public static final String MOBILE = "mobile";
    /**
     * password
     */
    public static final String PASSWORD = "password";

    /**
     * 客服信息
     */
    public static final String MY_SERVICE = "MY_SERVICE";

    /**
     * 在医生界面用的病人的uid
     */
    public static final String CID = "cid";

    // 个人资料
    /**
     * 个人信息
     */
    public static final String MY_INFO = "MY_INFO";
    /**
     * 医生个人个人信息
     */
    public static final String DR_INFO = "DR_INFO";

    /**
     * 生活处方
     */
    public static final String LAST_SYNC = "last_sync";
    /**
     * 餐前后血糖底限
     */
    public static final String SUGAR_LOWEST = "sugar_lowest";
    /**
     * 餐前血糖高限
     */
    public static final String SUGAR_HIGHEST_BEFOR_EAT = "sugar_highest_befor_eat";
    /**
     * 餐后血糖高限
     */
    public static final String SUGAR_HIGHEST_AFTER_EAT = "sugar_highest_after_eat";
    /**
     * 客服时间
     */
    public static final String SERVICE_TIME = "service_time";
    /**
     * 订单地址
     */
    public static final String ADDRESS = "address";

    /**
     * 首页banner图片
     */
    public static final String BANNERS = "banners";

    /**
     * 任务
     */
    public static final String TASK = "task";
    /**
     * 医生列表
     */
    public static final String DOCTOR_LIST = "doctor_list";
    /**
     * 医生详情
     */
    public static final String DOCTOR_DETAIL_ID = "doctor_detail_ID";
    /**
     * 生活处方列表
     */
    public static final String REPORT_LIST = "report_list";
    /**
     * 我的糖币
     */
    public static final String MYCOIN = "mycoin";
    public static final String WX_NICKNAME = "wx_nickname";
    public static final String WX_HEADIMGURL = "wx_headimgurl";
    public static final String SERVER_RED_POINT = "server_red_point"; //客服红点 1：有消息；0：无消息
    public static final String DOCTOR_RED_POINT = "doctor_red_point"; //医生列表红点 1：有消息；0：无消息
    public static final String SYSCONF_SYSTEMSETTING = "sysconf-systemSetting";
    public static final String SYSCONF_HEALTHGUIDE = Action.SYSCONF_HEALTHGUIDE;
    public static final String SYSCONF_HEALTHGUIDE_VERSION = "sysconf-healthGuide-version";
    public static final String DIETARY_RECOMMEND = "dietary_recommend";
    public static final String DIETARY_RECOMMEND_VERSION = "dietary_recommend_version";
    public static final String DIETARY_LIST = "dietary_LIST";
    public static final String DIETARY_LIST_VERSION = "dietary_list_version";
    public static final String REPORT_RECEIVER_DATA = "ReportReceiverData";
    public static final String WX_OPENID = "wx_openid";
    public static final String NOTICE_BASEID = "notice_pressed";
    public static final String IS_NOTICE_PRESSED = "is_notice_read"; //1：已读；0：未读
    public static final String DIABETES_TYPE = "diabetes_type";


    //糖友圈缓存字段
    public static final String SOCIAL_MY_USER_INFO = "social_my_user_info";
    public static final String SOCIAL_LIST_EXP_PATIENT = "social_list_exp_patient";
    public static final String SOCIAL_MESSAGE_UNREAD_NUM = "social_message_unread_num";
    public static final String SOCIAL_LATEST_DYNAMIC = "social_latest_dynamic";

    public static final String SHOW_NEW_ICON = "show_new_icon";

    public static final String CALENDAR_FILE_NAME = "calendar"; //日历列表文件名字
    public static final String SP_FILE_NAME = "spdata"; //日历列表文件名字
    public static final String COMMIT_ORDER_CASHE = "commit_order_cashe"; //日历列表文件名字

    public static final String SHARED_GUIDANCE = "guidance"; //引导页是否已出现


    //食谱
    public static final String FOOD_SEARCH_WORDS = "food_search_word";

    //医学服务的请求缓存
    public static final String MEDICAL_INDEX_CASH_KEY = "MEDICAL_INDEX_CASH_KEY";

}
