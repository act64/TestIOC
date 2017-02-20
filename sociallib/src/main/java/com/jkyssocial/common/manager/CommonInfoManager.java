package com.jkyssocial.common.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.jkys.jkysbase.Constant;
import com.google.gson.reflect.TypeToken;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.GetUserInfoResult;
import com.mintcode.App;
import com.mintcode.database.CasheDBService;
import com.jkys.jkysim.database.KeyValueDBService;
import com.mintcode.util.Keys;

import java.lang.reflect.Type;

/**
 * Created by yangxiaolong on 15/9/14.
 */
public class CommonInfoManager implements RequestManager.RequestListener<GetUserInfoResult> {

    private static CasheDBService casheDBService = CasheDBService.getInstance(App.getInstence().getApplicationContext());

    private static CommonInfoManager sInstance;

    public CommonInfoManager() {
    }

    public static synchronized CommonInfoManager getInstance() {
        if(sInstance == null)
            sInstance = new CommonInfoManager();
        return sInstance;
    }

    public Buddy getUserInfo(Context context){
        String uid = KeyValueDBService.getInstance().findValue(Keys.UID);
        if (uid != null && !"-1000".equals(uid)) {
            String value = casheDBService.findValue(Keys.SOCIAL_MY_USER_INFO);
            if(value != null){
                Type typeOfT = new TypeToken<GetUserInfoResult>() {
                }.getType();
                GetUserInfoResult getUserInfoResult = Constant.GSON.fromJson(value, typeOfT);
                if(getUserInfoResult != null && getUserInfoResult.getBuddy() != null)
                    return getUserInfoResult.getBuddy();
            }else
                ApiManager.getUserInfo(ApiManager.TYPE_REQUEST_UPDATE_CACHE, this, 1, context, null);
        }
        return null;

    }

    public static String getDiabetesType(int code, String name){
        if(code == 0)
            return "糖尿病类型 ：不知道/未确诊";
        if(name == null)
            name = "";
        return name;
    }

    @Override
    public void processResult(int requestCode, int resultCode, GetUserInfoResult data) {
        if(data != null)
            casheDBService.put(Keys.SOCIAL_MY_USER_INFO, Constant.GSON.toJson(data));
    }

    public static boolean showGuidance(Context context, String keys){
        Context ctx = context.getApplicationContext();
        SharedPreferences sp = ctx.getSharedPreferences(Keys.SHARED_GUIDANCE, ctx.MODE_PRIVATE);
        if(sp.getBoolean(keys, false)){
            return false;
        }
        SharedPreferences.Editor editor = ctx.getSharedPreferences(Keys.SHARED_GUIDANCE,
                Context.MODE_PRIVATE).edit();
        editor.putBoolean(keys, true);
        editor.apply();
        return true;
    }
}
