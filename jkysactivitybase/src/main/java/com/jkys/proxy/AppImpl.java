package com.jkys.proxy;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.jkys.common.listeners.SocailTaskListener;
import com.jkyshealth.manager.MedicalVolleyListener;
import com.mintcode.base.BaseTopActivity;

/**
 * Created by ylei on 2017/2/20.
 */

public class AppImpl {

    public static AppRroxy appRroxy;

    public static AppRroxy getAppRroxy() {
        return appRroxy;
    }

    public static void setAppRroxy(AppRroxy appRroxy) {
        AppImpl.appRroxy = appRroxy;
    }

    public interface AppRroxy{
       void registerReceiverByNeed();
         void initHeartbeat();
         boolean timeCountistart();
       boolean isNeedNewMain();
        void startIntent(String target, BaseTopActivity activity, String param);
       void showLoginActivity(Context context);

        /**
         * PublishDynamicManager.sendingDynamicList = new ArrayList<>();
         * KeyValueDBService.getInstance().delete();
         */
        void socialLogout();
        void addLog(Context context,String action);
        void addLog(Context context,String action,String action_trump);
        String getSTATIC_PIC_PATH();
        Context getApplicationContext();
        String geBuildType();
        String getH5_PATH();
        void getTaskReward(SocailTaskListener socailTaskListener, String str);
        String getWXEntryActivity_APPID();
        void setWXEntryActivityShareReward(boolean isShareReward);
        void setWXEntryActivityisShare(boolean isShare);
        void setWXEntryActivityloged(boolean loged);
        void setWXEntryActivitysharedmessage(String sharedmessage);
        void CallAPITaskReward(final Activity activity, final String taskDescOnce, final String taskDescDaily, final String taskNameOnce, final String taskNameDaily);
        void handleTask(Context context,Object object, String rewardType);
        String getTASKD_GET_REWARD();

         <T extends MyInfoUtilProxy> Class<T> getMyInfoProxyClazz();
        Class getUpLoadWxAvaTarTaskProxy();
        void uploadAvatar(Context context, Handler handler,String headIconPath);
        void  deleteCashDBServiceKey(String key);

         String getSOCIAL_BANNER_PATH();
       void getSocialBanner(MedicalVolleyListener listener);
        boolean getIsHideMall();

    }
}
