package com.mintcode.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.jkys.jkysim.database.KeyValueDBService;
import com.jkys.jkysim.util.IMLog;
import com.jkys.tools.UuidUtils;
import com.mintcode.App;
import com.mintcode.area_doctor.area_main.DoctorAPI;
import com.mintcode.area_patient.AppAPI;
import com.mintcode.area_patient.area_login.LoginAPI;
import com.mintcode.database.SQLiteHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.dreamplus.wentang.wxapi.WechatAPI;

public class Const {
    static App mApp;

    public static String DEVICE_UUID;
    public static String DEVICE_NAME;
    public static String VERSION_NAME;
    public static String OS = "Android";
    public static String OS_VER;
    public static String APP_VER;
    public static String BLUETOOTH_CLEAN;
    public static String WX_LOGIN = "wxLoginFlag";
    public static int LIMIT = 20;
    public static String KEY_LOGINNAME = "uid";
    public static String KEY_LOGINUSERINFO = "userinfo";
    public static String KEY_LOGINNAME_PRIVATE = "uid_private";
    public static String KEY_WX_CODE = "wxCode";
    public static String SharedPreferences_name = "cn.dreamplus.wentang";
    public static String SharedPreferences_name2 = "cn.dreamplus.wentang2";
    public static String PACKAGE_NAME = "cn.dreamplus.wentang";

    public static String HIGH_BEFORE = "high_before";
    public static String HIGH_AFTER = "high_after";
    public static String LOW_LINE = "low_line";
    public static String NET_CHECK_SHOW = "网络异常，请稍后再试";

    public static String TASK_CHECK_DOC_PRIVATE = "task_check_doc_private";

    public static String SMS_NUMBER = "106905705937891216";

    public static String SHARE_URL = "http://server.91jkys.com/app/share";
    public static String SHARE_ICON_URL = "http://static.91jkys.com/share/share_icon.png";

    public static String CHR_QQ_APP_ID = "1104591738"; // QQ应用的APP_ID

    public static String CHR_WEIBO_AccessToken;
    public static String CHR_WEIBO_APP_KEY = "2684781519"; // 新浪微博应用的APP_KEY
    public static String CHR_WEIBO_REDIRECT_URL = SHARE_URL;// 新浪微博应用的回调页
    public static String CHR_WEIBO_SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";// 新浪微博应用申请的高级权限

    public static long ONE_WEEK_MILLIS = 1000 * 60 * 60 * 24 * 7;
    public static long ONE_DAY_MILLIS = 1000 * 60 * 60 * 24;
    public static long ONE_MINUTE_MILLIS = 1000 * 60;

    public static String MSG_CENTER_BROADCAST_ACTION = "cn.dreamplus.wentang.msgCenter.broadcast";


    public static int TYPE = 1;
    public static int DORCTOR = 0;
    public static int PATIENT = 1;
    public static String CHR = "clt";

    public static String CHANNEL = "";

    public static String MOBILE = "";

    public static final String MIDNIGHT = "MIDNIGHT";  //1
    public static final String PRE_BREAKFAST = "PRE_BREAKFAST";//2
    public static final String POST_BREAKFAST = "POST_BREAKFAST";//3
    public static final String PRE_LAUNCH = "PRE_LAUNCH";//4
    public static final String POST_LAUNCH = "POST_LAUNCH";//5
    public static final String PRE_SUPPER = "PRE_SUPPER";//6
    public static final String POST_SUPPER = "POST_SUPPER";//7
    public static final String PRE_SLEEP = "PRE_SLEEP";//8

    public static ArrayList<String> PERIOD_MAPS = new ArrayList<String>(){
        {add(Const.MIDNIGHT);
           add(Const.PRE_BREAKFAST);
            add(Const.POST_BREAKFAST);
           add(Const.PRE_LAUNCH);
            add(Const.POST_LAUNCH);
          add(Const.PRE_SUPPER);
           add(Const.POST_SUPPER);
           add(Const.PRE_SLEEP);
        }
    };


    //最近一次点击咨询消息的时间
    public static final String IM_LASTRED_TIME = "im_lastReadTIme";

    // 康檬静态图片库地址
    // 康檬静态图片库地址
//	public static  String STATIC_PIC_PATH = "http://static.91jkys.com";

//	public static String LITTLE_Q_SERVER_PATH = "http://api.91jkys.com:8099";

    //用户行为地址
//	public static String USER_BEHAVIOR_PATH = "http://deliver.91jkys.com/index.php/Home/";
//		public static String USER_BEHAVIOR_PATH = "http://10.0.5.187/deliveraaa/index.php/Home/";

    //平安好医生H5页面地址
//		public static String PINGAN_DOCTOR_PATH = "http://static.91jkys.com/events/20150804/";


    // ws 生产地址
    // public static  String IM_SERVER_PATH =
    // "http://121.41.96.47:20001/api/";
    // public static  String WS_SERVER_PATH =
    //	"ws://121.41.96.47:20000";
    // public static  String IMAGE_SERVER_PATH =
    // "http://121.41.96.47:20001";
    // public static  String IM_UPLOAD_PATH =
    // IM_SERVER_PATH + "upload";

    // im测试地址
//	public static  String IM_SERVER_PATH = "http://115.29.179.22:20001/api/";
//	public static  String WS_SERVER_PATH = "ws://115.29.179.22:20000";
//	public static  String IMAGE_SERVER_PATH = "http://115.29.179.22:20001";
//	public static  String IM_UPLOAD_PATH = IM_SERVER_PATH + "upload";


    /**
     * 血糖仪读写uuid
     */
    public static class GlucoseMeter {

        public static String UUID_KEY_DATA = "82d669f4-3b55-4b00-9241-7cb2c33b13b6";
        // private  static String UUID_KEY_DATA =
        // "826d69f4-3b55-4b00-9241-7cb2c33b13b6";
        public static String UUID_KEY_SERVICE = "6ed025d2-622e-4b18-a14c-12999af2e0a0";
        public static String UUID_KEY_WRITE = "b22e4c83-3fe0-4ba4-be15-cafeb00fda79";
        public static String UUID_KEY_READ1 = "186bb418-5351-48b5-bf6d-2167639bc867";
        public static String UUID_KEY_READ = "00002902-0000-1000-8000-00805f9b34fb";

        public static String UUID_KEY_PROP_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb";// 属性service

        public static String UUID_KEY_PROP_TYPE = "00002a24-0000-1000-8000-00805f9b34fb";// 读血糖仪品牌

        public static String UUID_KEY_PROP_HARD = "00002a27-0000-1000-8000-00805f9b34fb";// 硬件版本信息

        public static String UUID_KEY_PROP_FIRMWARE = "00002a26-0000-1000-8000-00805f9b34fb";// 固件版本信息

        public static String UUID_KEY_PROP_SOFT = "00002a28-0000-1000-8000-00805f9b34fb";// 软件版本信息

        public static String TYPE_JNJ = "JNJ-Moudle";// 强生
        public static String TYPE_ABBOTT = "ABBOTT-Moudle";// 雅培
        public static String TYPE_BAYER = "BAYER-Moudle";// 拜耳
    }

    public static String APP_NAME = "91jkys";

    public static int MODIFYMOBILE = 999;

    public static void initValues(App app) {
        try {
            mApp = app;
            DEVICE_UUID = getDeviceUUID(app);
            DEVICE_NAME = getDeviceName();
            VERSION_NAME = getDeviceName();
            APP_VER = getAppVer(app);
            OS_VER = getOsVer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//	public static void switchTo(int type) {
//
//		CHR = "clt";
//		TYPE = 1;
//
//	}

    public static String getDeviceName() {
        return Build.MODEL;
    }

    public static String getOsVer() {
        return Build.VERSION.RELEASE;
    }

    public static boolean getBluetoothClean(Context context) {
        boolean clean = context.getSharedPreferences(SharedPreferences_name,
                Context.MODE_PRIVATE).getBoolean(BLUETOOTH_CLEAN, true);
        return clean;
    }

    public static void setBluetoothClean(Context context, boolean bluetoothClean) {
        Editor editor = context.getSharedPreferences(SharedPreferences_name,
                Context.MODE_PRIVATE).edit();
        editor.putBoolean(BLUETOOTH_CLEAN, bluetoothClean);
        editor.commit();
    }

    public static boolean getWXLogin(Context context) {
        boolean clean = context.getSharedPreferences(SharedPreferences_name,
                Context.MODE_PRIVATE).getBoolean(WX_LOGIN, false);
        return clean;
    }

    public static void setWXLogin(Context context, boolean wxlogin) {
        Editor editor = context.getSharedPreferences(SharedPreferences_name,
                Context.MODE_PRIVATE).edit();
        editor.putBoolean(WX_LOGIN, wxlogin);
        editor.commit();
    }

    public static void setUidToGuest(Context context) {
        KeyValueDBService dbService = KeyValueDBService.getInstance();
        try {
            dbService.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbService.put(Keys.TOKEN, "anonymous");
        dbService.put(Keys.NEW_TOKEN, "anonymous");
        dbService.put(Keys.UID, "-1000");
        Const.setUid(context, "-1000");
        SQLiteHelper.getNewInstance(context, "-1000");
    }

    /**
     * e get the application version name.you can edit the version name in
     * AndroidManifest.xml.<br/>
     * example:<br/>
     * android:versionName="0.0.1"
     *
     * @param context the application context
     * @return app version name
     * @author RobinLin
     */
    public static String getAppVer(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getDeviceUUID(Context context) {

        if (!TextUtils.isEmpty(DEVICE_UUID)) {
            return DEVICE_UUID;
        }

//		int sdk = Build.VERSION.SDK_INT;
//		String deviceUUID = null;
//		if (sdk >= Build.VERSION_CODES.GINGERBREAD) {
//			deviceUUID = Build.SERIAL;
//		}
//		if (TextUtils.isEmpty(deviceUUID)) {
//			deviceUUID = android.provider.Settings.Secure.ANDROID_ID;
//		}
//		if (TextUtils.isEmpty(deviceUUID)) {
//			deviceUUID = "guest";
//		}

        DEVICE_UUID = UuidUtils.getUuidFromFile(context, UuidUtils.UUID_FILE_DIR, UuidUtils.UUID_FILE_NAME);
        if (TextUtils.isEmpty(DEVICE_UUID)) {
            DEVICE_UUID = UuidUtils.getUuidFromFile(context, UuidUtils.UUID_HIDE_FILE_DIR, UuidUtils.UUID_FILE_NAME);
        }

        if (TextUtils.isEmpty(DEVICE_UUID)) {
            DEVICE_UUID = UuidUtils.getUuidFromSetting(context);
        }

        if (TextUtils.isEmpty(DEVICE_UUID)) {
            DEVICE_UUID = UuidUtils.getUuidFromSpf(context);
        }

        if (TextUtils.isEmpty(DEVICE_UUID)) {
            DEVICE_UUID = UUID.randomUUID().toString();
        }

        UuidUtils.saveUuidToAllFile(context, DEVICE_UUID);

//		int sdk = Build.VERSION.SDK_INT;
//		TelephonyManager telephonyManager = (TelephonyManager) context
//				.getSystemService(Context.TELEPHONY_SERVICE);
//		String deviceUUID = telephonyManager.getDeviceId();
//		if (deviceUUID == null || deviceUUID.equals("")) {
//			if (sdk >= Build.VERSION_CODES.GINGERBREAD) {
//				deviceUUID = Build.SERIAL;
//			}
//			if (deviceUUID == null || deviceUUID.equals("")) {
//				deviceUUID = android.provider.Settings.Secure.ANDROID_ID;
//			}
//			if (deviceUUID == null || deviceUUID.equals("")) {
//				deviceUUID = "guest";
//			}
//		}

        return DEVICE_UUID;
    }

    @SuppressWarnings("deprecation")
    public static void setUid(Context context, String uid) {
        try {
            IMLog.e("IMTAG", "app setUid");
            Editor editor = context.getSharedPreferences(
                    SharedPreferences_name2, Context.MODE_WORLD_READABLE)
                    .edit();
            editor.putString(KEY_LOGINNAME, uid);
            IMLog.e("IMTAG", "app uid=" + uid);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Editor editor2 = context.getSharedPreferences(
                    SharedPreferences_name, Context.MODE_PRIVATE).edit();
            editor2.putString(KEY_LOGINNAME_PRIVATE, uid);
            editor2.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public static String getUid(Context context) {
        String uid = null;
        try {
            uid = context.getSharedPreferences(SharedPreferences_name2,
                    Context.MODE_WORLD_READABLE).getString(KEY_LOGINNAME, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (uid == null) {
                uid = context.getSharedPreferences(SharedPreferences_name,
                        Context.MODE_PRIVATE)
                        .getString(KEY_LOGINNAME_PRIVATE, null);
            }
        } catch (Exception e) {
            uid = "-1000";
            e.printStackTrace();
        }
        return uid;
    }

    @SuppressWarnings("deprecation")
    public static void setUserInfo(Context context, String userInfoJson) {
        try {
            Editor editor = context.getSharedPreferences(
                    SharedPreferences_name2, Context.MODE_WORLD_READABLE)
                    .edit();
            editor.putString(KEY_LOGINUSERINFO, userInfoJson);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Editor editor2 = context.getSharedPreferences(
                    SharedPreferences_name, Context.MODE_PRIVATE).edit();
            editor2.putString(KEY_LOGINUSERINFO, userInfoJson);
            editor2.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public static String getUserInfo(Context context) {
        String uInfo = null;
        try {
            uInfo = context.getSharedPreferences(SharedPreferences_name2,
                    Context.MODE_WORLD_READABLE).getString(KEY_LOGINUSERINFO,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (uInfo == null) {
            uInfo = context.getSharedPreferences(SharedPreferences_name,
                    Context.MODE_PRIVATE).getString(KEY_LOGINUSERINFO, null);
        }
        return uInfo;
    }

    public static void setWXcode(Context context, String code) {
        Editor editor = context.getSharedPreferences(SharedPreferences_name,
                Context.MODE_PRIVATE).edit();
        editor.putString(KEY_WX_CODE, code);
        editor.commit();
    }

    public static String getWXcode(Context context) {
        String code = context.getSharedPreferences(SharedPreferences_name,
                Context.MODE_PRIVATE).getString(KEY_WX_CODE, "");
        return code;
    }

    public static int getTaskCheckDocPrivate(Context context) {
        int code = context.getSharedPreferences(SharedPreferences_name,
                Context.MODE_PRIVATE).getInt(TASK_CHECK_DOC_PRIVATE, -1);
        return code;
    }

    public static void setTaskCheckDocPrivate(Context context, int taskId) {
        Editor editor = context.getSharedPreferences(SharedPreferences_name,
                Context.MODE_PRIVATE).edit();
        editor.putInt(TASK_CHECK_DOC_PRIVATE, taskId);
        editor.commit();
    }

    public static String getHighBefore(Context context) {
        String code = context.getSharedPreferences(SharedPreferences_name,
                Context.MODE_PRIVATE).getString(HIGH_BEFORE, "7");
        return code;
    }

    public static void setHighBefore(Context context, String high_before) {
        Editor editor = context.getSharedPreferences(SharedPreferences_name,
                Context.MODE_PRIVATE).edit();
        editor.putString(HIGH_BEFORE, high_before);
        editor.commit();
    }

    public static String getHighAfter(Context context) {
        String code = context.getSharedPreferences(SharedPreferences_name,
                Context.MODE_PRIVATE).getString(HIGH_AFTER, "10");
        return code;
    }

    public static void setHighAfter(Context context, String high_after) {
        Editor editor = context.getSharedPreferences(SharedPreferences_name,
                Context.MODE_PRIVATE).edit();
        editor.putString(HIGH_AFTER, high_after);
        editor.commit();
    }

    public static String getLowLine(Context context) {
        String code = context.getSharedPreferences(SharedPreferences_name,
                Context.MODE_PRIVATE).getString(LOW_LINE, "4.4");
        return code;
    }

    public static void setLowLine(Context context, String low_line) {
        Editor editor = context.getSharedPreferences(SharedPreferences_name,
                Context.MODE_PRIVATE).edit();
        editor.putString(LOW_LINE, low_line);
        editor.commit();
    }

    public static int dp2px(Context context, double dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, double pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static DisplayMetrics getDM(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        return dm;
    }

    public static long getCurrentTime() {

        return new Date().getTime();
    }

    private static long mTimeGap = -1;

    public static void setTime(long time) {
        long curTime = System.currentTimeMillis();
        mTimeGap = time - curTime;
        SharedPreferences sp = mApp.getSharedPreferences(mApp.getPackageName(),
                Context.MODE_PRIVATE);
        sp.edit().putLong(Keys.TIME_GAP, mTimeGap).commit();
    }

    public static void setLastSync() {
        long curTime = System.currentTimeMillis();
        SharedPreferences sp = mApp.getSharedPreferences(mApp.getPackageName(),
                Context.MODE_PRIVATE);
        sp.edit().putLong(Keys.LAST_SYNC, curTime).commit();
    }

    public static long getLastSync() {
        long uid = mApp.getSharedPreferences(mApp.getPackageName(),
                Context.MODE_PRIVATE).getLong(Keys.LAST_SYNC, 0);
        return uid;
    }

    /*
     *
     */
    public static ArrayList<String> ALARM_TYPE = new ArrayList<String>() {

        {
            add("血糖测量");
        }

        ;

        {
            add("用药提醒");
        }

        ;

        {
            add("胰岛素提醒");
        }

        ;

        {
            add("运动提醒");
        }

        ;

        {
            add("随访提醒（看病）");
        }

        ;

        {
            add("量血压");
        }

        ;

        {
            add("其他");
        }
    };

    public static HashMap<Integer,String> SYSTEM_ADDMEAL_ALARM_TYPE = new HashMap<Integer, String>() {

        {
            put(ADDMEAL_CLOCKTYPE_AM,"上午加餐提醒");
            put(ADDMEAL_CLOCKTYPE_PM,"下午加餐提醒");
        }


    };

    public static HashMap<Integer,String> SYSTEM_MONITOR_ALARM_TYPE = new HashMap<Integer, String>() {

        {
           put(BEFORE_BREAKFAST,"早餐前空腹");
            put(AFTER_BREAKFAST,"早餐后");
            put(BEFORE_LUNCH,"午餐前空腹");
            put(AFTER_LUNCH,"午餐后");
            put(BEFORE_SUPPER,"晚餐前空腹");
            put(AFTER_SUPPER,"晚餐后");
        }


    };
    public static final int ADDMEAL_CLOCKTYPE_AM=110;
    public static final int ADDMEAL_CLOCKTYPE_PM=111;

    public static final int BEFORE_BREAKFAST=100;
    public static final int AFTER_BREAKFAST=101;
    public static final int BEFORE_LUNCH=102;
    public static final int AFTER_LUNCH=103;
    public static final int BEFORE_SUPPER=104;
    public static final int AFTER_SUPPER=105;

    public static Map<String, String> REPEAT_MAP = new HashMap<String, String>() {


        {
            put("7", "周日");
            put("1", "周一");
            put("2", "周二");
            put("3", "周三");
            put("4", "周四");
            put("5", "周五");
            put("6", "周六");
            put("*", "每天");
            put("周日", "7");
            put("周一", "1");
            put("周二", "2");
            put("周三", "3");
            put("周四", "4");
            put("周五", "5");
            put("周六", "6");
            put("每天", "*");
        }
    };


    public static boolean DEBUG = false;

    /**
     * 将long型时间转化成可以显示的string
     *
     * @param time 传入的时间（long）
     * @return 时间显示的字符串
     */
    public static String formatTime(long time) {
        Calendar ct = Calendar.getInstance();
        ct.setTimeInMillis(time);
        int day = ct.get(Calendar.DAY_OF_YEAR);
        int year = ct.get(Calendar.YEAR);
        ct.setTimeInMillis(System.currentTimeMillis());
        int now_day = ct.get(Calendar.DAY_OF_YEAR);
        int now_year = ct.get(Calendar.YEAR);
        SimpleDateFormat sf = null;
        String timeStr = null;
        if (day == now_day && year == now_year) {
            sf = new SimpleDateFormat("HH:mm");
            timeStr = sf.format(new Date(time));
        } else if (year == now_year) {
            sf = new SimpleDateFormat("MM-dd");
            timeStr = sf.format(new Date(time));
        } else {
            sf = new SimpleDateFormat("yyyy");
            timeStr = sf.format(new Date(time));
        }

        return timeStr;
    }

    /**
     * 将long型时间转化成可以显示的string
     *
     * @param time 传入的时间（long）
     * @return 时间显示的字符串
     */
    public static String formateForImRecords(long time) {
        Calendar ct = Calendar.getInstance();
        ct.setTimeInMillis(time);
        int day = ct.get(Calendar.DAY_OF_YEAR);
        int year = ct.get(Calendar.YEAR);
        ct.setTimeInMillis(System.currentTimeMillis());
        int now_day = ct.get(Calendar.DAY_OF_YEAR);
        int now_year = ct.get(Calendar.YEAR);
        SimpleDateFormat sf = null;
        String timeStr = null;
        if (day == now_day && year == now_year) {
            sf = new SimpleDateFormat("HH:mm");
            timeStr = sf.format(new Date(time));
        } else if (year == now_year && day == now_day - 1) {
            timeStr = "昨天";
        } else {
            sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            timeStr = sf.format(new Date(time));
        }

        return timeStr;
    }

    public static void showMessageNotify(Context context, String title,
                                         String text, int id) {
        // if (!isBackgroundRunning()) {
        // return;
        // }
        // NotificationManager mNotificationManager = (NotificationManager)
        // context
        // .getSystemService(Context.NOTIFICATION_SERVICE);
        // @SuppressWarnings("deprecation")
        // Notification notification = new Notification(
        // R.drawable.tab_service_noselect, text,
        // System.currentTimeMillis());
        //
        // // 发送通知，提示声音
        // // notification.defaults |= Notification.DEFAULT_SOUND;
        // notification.ledARGB = 0x00ff00;
        //
        // notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        // notification.flags |= Notification.FLAG_AUTO_CANCEL
        // | Notification.FLAG_SHOW_LIGHTS;
        // notification.flags |= Notification.FLAG_AUTO_CANCEL;
        // Intent notificationIntent = new Intent(Intent.ACTION_MAIN); // new
        // notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //
        // if (Const.TYPE == Const.DORCTOR) {
        // notificationIntent.setClass(context, MainActivity_dr.class);
        // } else {
        // // notificationIntent.setClass(context, MainActivity_pt.class);
        // notificationIntent.setClass(context, MainActivity_pt_new.class);
        // }
        //
        // notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // // | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        // PendingIntent pendingIntent = PendingIntent.getActivity(context, id,
        // notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // notification.setLatestEventInfo(context, title, text, pendingIntent);
        // mNotificationManager.notify(id, notification);

    }

    public static String[] TouristAction = new String[]{
            DoctorAPI.TASKID.LIST_CONSULTANT,
            AppAPI.TASKID.VALIDATION_APPVER,
            AppAPI.TASKID.PROMOTIONCHANNELCOUNTLIST,
            TaskAPI.TASKID.LIST_TASK,
            "dietary-list",
            "dietary-food-detail",
            "mess_commentList",
            "dietary-detail",
            "dietary-relate-food-list",
            "store_ordersList",
            "show-consultant",
            DoctorAPI.TASKID.SYSCONF_ADDRESS,
            LoginAPI.TASKID.LOGIN,
            WechatAPI.TASKID.WECHAT,
            LoginAPI.TASKID.VERIFY_CODE,
            DoctorAPI.TASKID.LIST_CONSULTANT
    };

    /**
     * 首页 page-index 签到 page-sign 每日食谱 page-dietary 找医生 page-doctor 记血糖
     * page-record 蓝牙导入 page-import 血糖分析 page-analysis 血糖记录 page-table 食谱详情
     * page-dietary-detail 医生详情 page-doctor-detail 聊天咨询 page-im-advice 生活处方
     * page-im-report 我的糖币 page-mycoin 我的任务 page-mytask 我的提醒 page-mywarn 分享
     * page-share 兑换 page-convert 提交 page-submit 兑换记录 page-change-record
     */
    public static class PageAction {
        public static String page_index = "page-index";
        public static String page_sign = "page-sign";
        public static String page_dietary = "page-dietary";
        public static String page_doctor = "page-doctor";
        public static String page_record = "page-record";
        public static String page_import = "page-import";
        public static String page_dietary_detail = "page-dietary-detail";
        public static String page_doctor_detail = "page-doctor-detail";
        public static String page_mycoin = "page-mycoin";
        public static String page_mytask = "page-mytask";
        public static String page_mywarn = "page-mywarn";
        public static String page_submit = "page-submit";
        public static String page_change_record = "page-change-record";
        public static String page_forum_home = "page-forum-home";
    }

    /**
     * 点击每日签到 event-sign 点击糖友菜谱 event-dietary 点击每日食谱（Tab） event-dietary-tab
     * 点击测营养 event-ceyingyang 点击找医生 event-doctor 点击记血糖 event-record 点击记血糖（Tab）
     * event-doctor-tab 点击我的 event-my
     *
     * @author vincent
     */
    public static class EventAction {
        public static String event_sign = "event-sign";
        public static String event_dietary = "event-dietary";
        public static String event_dietary_tab = "event-dietary-tab";
        public static String event_ceyingyang = "event-ceyingyang";
        public static String event_doctor = "event-doctor";
        public static String event_record = "event-record";
        public static String event_record_tab = "event-record-tab";
        public static String event_my = "event-my";
    }

    public static class Cashe {
        public static String MSG_LIST_BASEID = "msg_list_baseId";
        public static String DRUG_SWEEP_LIST_SORTNUM = "drug-sweep-list_sortNum";
        public static String SHARE_FOOD_ID_ = "share_food_id_"; //
    }
}
