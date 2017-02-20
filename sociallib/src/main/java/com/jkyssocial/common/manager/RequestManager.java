package com.jkyssocial.common.manager;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jkys.jkysbase.data.NetWorkResult;
import com.jkys.jkysbase.Constant;
import com.google.gson.reflect.TypeToken;
import com.jkyshealth.manager.BaseMedicalVolleyRequest;
import com.jkyslogin.LoginHelper;
import com.mintcode.App;
import com.mintcode.database.CasheDBService;
import com.jkys.jkysim.database.KeyValueDBService;
import com.mintcode.util.Const;
import com.mintcode.util.Keys;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.helper.StringUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import cn.dreamplus.wentang.BuildConfig;

/**
 * Created by yangxiaolong on 15/9/2.
 */
public class RequestManager {

    public static final int RESULT_SUCCESS_CODE = 0;

    public static final int RESULT_ERROR_CODE = 1;

    private static final String ACTIVITY_TAG = "RequestManager";

    private static final int MY_SOCKET_TIMEOUT_MS = 10000;

    private static String appAgent;

    private static RequestQueue requestQueue;

    private static CasheDBService casheDBService;

    static KeyValueDBService mValueDBService = KeyValueDBService.getInstance();

    private RequestManager() {
        requestQueue = Volley.newRequestQueue(App.getInstence().getApplicationContext());
        casheDBService = CasheDBService.getInstance(App.getInstence().getApplicationContext());
    }

    private static class SingletonHolder {
        /**
         * 单例变量
         */
        private static RequestManager instance = new RequestManager();
    }

    public static RequestManager getInstance() {
        return SingletonHolder.instance;
    }

    public <T extends NetWorkResult> void loadRequestWithCache(Class<T> clazz, String key, String action, String rootPath,
                                                               final RequestListener<T> listener, final int requestCode, final Context context, Map<String, Object> params) {
        String value = casheDBService.findValue(key);
        if (value != null) {
            Type typeOfT = new TypeToken<T>() {
            }.getType();
            T t = Constant.GSON.fromJson(value, clazz);
            listener.processResult(requestCode, 0, t);
        } else
            loadRequestWithNetWork(clazz, key, action, rootPath, listener, requestCode, context, params);

    }

    public <T extends NetWorkResult> void loadRequestUpdateCache(Class<T> clazz, String key, String action, String rootPath,
                                                                 final RequestListener<T> listener, final int requestCode, final Context context, Map<String, Object> params) {
        loadRequestWithNetWork(clazz, key, action, rootPath, listener, requestCode, context, params);

    }

    public <T extends NetWorkResult> void loadRequestWithNetWork(Class<T> clazz, String action, String rootPath,
                                                                 final RequestListener<T> listener, final int requestCode, final Context context, Map<String, Object> params) {
        loadRequestWithNetWork(clazz, null, action, rootPath, listener, requestCode, context, params);
    }

    public <T extends NetWorkResult> void loadRequestWithNetWork(Class<T> clazz, String action, String rootPath,
                                                                 final RequestListener<T> listener, final int requestCode, final Context context, Map<String, Object> params, boolean shouldCache) {
        loadRequestWithNetWork(clazz, null, action, rootPath, listener, requestCode, context, params, shouldCache);
    }

    public <T extends NetWorkResult> void loadRequestWithNetWork(final Class<T> clazz, final String key, final String action, final String rootPath,
                                                                 final RequestListener<T> listener, final int requestCode, final Context context, Map<String, Object> params) {
        loadRequestWithNetWork(clazz, key, action, rootPath, listener, requestCode, context, params, false);
    }

    public <T extends NetWorkResult> void loadRequestWithNetWork(final Class<T> clazz, final String key, final String action, final String rootPath,
                                                                 final RequestListener<T> listener, final int requestCode,
                                                                 Context context, Map<String, Object> params, final boolean shouldCache) {
        final Context ctx = context.getApplicationContext();
        String newToken = mValueDBService.findValue(Keys.NEW_TOKEN);
        if (TextUtils.isEmpty(newToken) || "-1000".equals(newToken)) {
            newToken = "anonymous";
        }
        String uid = mValueDBService.findValue(Keys.UID);
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        params.put("chr", Const.CHR);
        // 测试用户
        params.put("token", newToken);
        params.put("timestamp", System.currentTimeMillis());
        params.put("uid", uid);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(Constant.GSON.toJson(params));
            Log.i("Social_request:", jsonObject + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Response.Listener<T> successListener = new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                try {
//                    com.mintcode.bluetooth.activeandroid.util.Log.e("内存", "监听2=" + listener);
//                    Log.e("内存", "listener=" + listener);
                    if (BuildConfig.DEBUG)
                        Log.d("BaseRequest", "response -> " + response.toString());
                    if (response == null && TextUtils.isEmpty(response.getReturnCode()))
                        return;
                    switch (response.getReturnCode()) {
                        case "0000":
                            if (!TextUtils.isEmpty(key))
                                casheDBService.put(key, Constant.GSON.toJson(response));
                            if (listener != null)
                                listener.processResult(requestCode, RESULT_SUCCESS_CODE, response);
                            break;
                        case "0100":
                        case "0101":
                            if (listener != null)
                                listener.processResult(requestCode, RESULT_ERROR_CODE, response);
                            // TODO  !!! 此处写的急，没有时间和大家商量，一下代码能够成功运行的前提是保证context 是Activity和Fragment 而不是App的
                            //为了防止内存泄漏，暂时通过
                            Activity activity = LoginHelper.getInstance().ChangeToActivity(ctx);
                            LoginHelper.getInstance().ForcedReLogin(activity);
                            //                        showLoginDialog(ctx, response.getReturnMsg());
                            break;
                        case "9960":
                            if (listener != null)
                                listener.processResult(requestCode, 9960, response);
                            break;
                        default:
                            if (ctx != null) {
                                Toast toast = Toast.makeText(ctx, response.getReturnMsg(), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            if (listener != null)
                                listener.processResult(requestCode, RESULT_ERROR_CODE, response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        final JSONObject finalJsonObject = jsonObject;
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
//                Log.e("BaseRequest", arg0.getMessage());
                Log.i("Zern-----", arg0.getMessage() == null ? " null " : "!null ");
                if (listener != null)
                    listener.processResult(requestCode, RESULT_ERROR_CODE, null);
            }
        };
        BaseMedicalVolleyRequest<T> baseGsonRequest = new BaseMedicalVolleyRequest<T>(clazz, Request.Method.POST, rootPath + action, jsonObject, successListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json; charset=UTF-8");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                if (appAgent == null) {
                    appAgent = android.os.Build.MODEL + "/Android" + android.os.Build.VERSION.RELEASE + "; " + Const.CHR + "/" + BuildConfig.VERSION_CODE;
                }
                headers.put("appAgent", appAgent);
                return headers;
            }
        };
        baseGsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        baseGsonRequest.setShouldCache(shouldCache);
        requestQueue.add(baseGsonRequest);
    }

//    public void showLoginDialog(final Context context, String returnMsg){
//        ConfirmTipsDialog dialog = new ConfirmTipsDialog(context, returnMsg, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PublishDynamicManager.sendingDynamicList = new ArrayList<>();
//                KeyValueDBService.getInstance().delete();
//                Intent intent = new Intent(context, LoginActivity.class);
//                intent.putExtra("forceLogin", true);
//                context.startActivity(intent);
//            }
//        });
////        dialog.setConfirmText("登录");
//        dialog.setCancelVisibility(View.GONE);
//        dialog.show();
//    }

//    public void showLoginDialog(final Context context, String returnMsg) {
//        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
//        myDialog.show();
//        myDialog.getWindow().setContentView(R.layout.dialog_login);
//        myDialog.getWindow().findViewById(R.id.tv_login_ok)
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        myDialog.dismiss();
//                        PublishDynamicManager.sendingDynamicList = new ArrayList<>();
//                        KeyValueDBService.getInstance().delete();
//                        Intent intent = new Intent(context,
//                                LoginActivity.class);
//                        //强制登录
//                        intent.putExtra("forceLogin", true);
//                        context.startActivity(intent);
////                        finish();
//                    }
//                });
//        myDialog.getWindow().findViewById(R.id.tv_login_cancel)
//                .setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        myDialog.dismiss();
//
//                    }
//                });

//    }

    public interface RequestListener<T> {
        public void processResult(int requestCode, int resultCode, T data);
    }

    private String getToken() {
        String token = KeyValueDBService.getInstance().find(Keys.NEW_TOKEN);
        if (token == null || token.equals("anonymous")) {
            return null;
        }
        return token;
    }
}
