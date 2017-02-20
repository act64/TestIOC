package com.jkys.proxy;


import android.content.Context;

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
    }
}
