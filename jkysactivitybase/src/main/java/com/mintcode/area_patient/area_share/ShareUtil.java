package com.mintcode.area_patient.area_share;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jkys.jkysactivitybase.R;
import com.jkys.jkysbase.ThreadPoolTools;
import com.jkys.jkysim.database.KeyValueDBService;
import com.jkys.proxy.AppImpl;
import com.jkysshop.model.ShareStatus;
import com.mintcode.util.Const;
import com.mintcode.util.ImageManager;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;


import java.io.ByteArrayOutputStream;

import de.greenrobot.event.EventBus;

/**
 * Created by tom on 15/8/31.
 */
public class ShareUtil extends LinearLayout implements IUiListener, View.OnClickListener {
    private Tencent mTencent;
//    private AuthInfo mAuthInfo;
    private Oauth2AccessToken mAccessToken;
    private IWXAPI api;
    private TextView tv_weixinfriend;
    private TextView tv_weixinquan;
    private TextView tv_qq;
//    private TextView tv_sina;
    private TextView tv_cancel;
    private PopupWindow mPopupWindow;
    private LinearLayout shareLayout;
    private Context context;
    private Activity activity;
    private String webpageUrl = "http://static.91jkys.com/html/drinvite.html";
    private String title = "加入掌上糖医，免费获糖币，兑换精美礼品";
    private String textContent = "";
    private String imgUrl;


    public ShareUtil(Activity context, String url, String title, String textContent) {
        super(context);
        this.activity = context;
        this.context = context.getApplicationContext();

        if (!TextUtils.isEmpty(title)) {
            this.title = title;
        }
        if (!TextUtils.isEmpty(url)) {
            this.webpageUrl = url;
        }
        if (!TextUtils.isEmpty(textContent)) {
            this.textContent = textContent;
        }
        init();
    }

//    public void shareWeb(String taskId){
//        Intent weiboIntent = new Intent(Intent.ACTION_SEND);
//        weiboIntent.setType("text/plain");
//        PackageManager pm = context.getPackageManager();
//        List<ResolveInfo> matches = pm.queryIntentActivities(weiboIntent,
//                PackageManager.MATCH_DEFAULT_ONLY);
//        String packageName = "com.sina.weibo";
//        ResolveInfo info = null;
//        for (ResolveInfo each : matches) {
//            String pkgName = each.activityInfo.applicationInfo.packageName;
//            if (packageName.equals(pkgName)) {
//                info = each;
//                break;
//            }
//        }
//        if (info == null) {
//            context.getPackageManager().setComponentEnabledSetting(new ComponentName(context.getApplicationContext(), SinaShareActivity.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
//            share2Sina();
//            return;
//        }
//        context.getPackageManager().setComponentEnabledSetting(new ComponentName(context.getApplicationContext(), SinaShareActivity.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//        Intent i = new Intent(context, SinaShareActivity.class);
//        i.putExtra("maiDianCanSu", maiDianCanSu);
//        i.putExtra("textContent", textContent);
//        i.putExtra("webpageUrl", webpageUrl);
//        i.putExtra("title", title);
//        i.putExtra("imgUrl", imgUrl);
//        i.putExtra("taskId",taskId);
//        context.startActivity(i);
//        return;
//    }

    //埋点的参数
    //比如 -url-
    // -菜谱id-
    //-动态id-
    private String maiDianCanSu;
    //区分微信朋友圈和微信好友
    private String wxPath;

    public void setWxPath(String wxPath) {
        this.wxPath = wxPath;
    }

    public void setMaiDianCanSu(String maiDianCanSu) {
        this.maiDianCanSu = maiDianCanSu;
    }

    public ShareUtil(Activity activity, String url, String title, String textContent, String imgUrl) {
        super(activity);
        this.activity = activity;
        this.context = activity.getApplicationContext();
        if (!TextUtils.isEmpty(title)) {
            this.title = title;
        }
        if (!TextUtils.isEmpty(url)) {
            this.webpageUrl = url;
        }
        if (!TextUtils.isEmpty(textContent)) {
            this.textContent = textContent;
        }
        if (!TextUtils.isEmpty(imgUrl)) {
            this.imgUrl = imgUrl;
        }

        init();
    }

    public void init() {
        mTencent = Tencent.createInstance(Const.CHR_QQ_APP_ID, context);
        regToWx(context);
//        try {
//            mAuthInfo = new AuthInfo(context, Const.CHR_WEIBO_APP_KEY,
//                    Const.CHR_WEIBO_REDIRECT_URL, Const.CHR_WEIBO_SCOPE);
//            mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context, Const.CHR_WEIBO_APP_KEY);
//            mWeiboShareAPI.registerApp(); // 将应用注册到微博客户端
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void regToWx(Context context) {
        api = WXAPIFactory.createWXAPI(context.getApplicationContext(), AppImpl.getAppRroxy().getWXEntryActivity_APPID(), false);
        api.registerApp(AppImpl.getAppRroxy().getWXEntryActivity_APPID());
        // api.handleIntent(getIntent(), this);
    }

    public void showSharePop(View parent) {
        shareLayout = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.popupwindow_share, null);
        tv_cancel = (TextView) shareLayout.findViewById(R.id.tv_cancel);
        tv_weixinfriend = (TextView) shareLayout
                .findViewById(R.id.tv_weixinfriend);
        tv_weixinquan = (TextView) shareLayout.findViewById(R.id.tv_weixinquan);
//        tv_sina = (TextView) shareLayout.findViewById(R.id.tv_sina);
        tv_qq = (TextView) shareLayout.findViewById(R.id.tv_qq);
        tv_cancel.setOnClickListener(this);
        tv_weixinfriend.setOnClickListener(this);
        tv_weixinquan.setOnClickListener(this);
//        tv_sina.setOnClickListener(this);
        tv_qq.setOnClickListener(this);
        tv_qq.setVisibility(View.VISIBLE);

        mPopupWindow = new PopupWindow(shareLayout, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setOutsideTouchable(false);
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();

        activity.getWindow().setAttributes(lp);
        mPopupWindow.showAtLocation(parent, Gravity.CENTER | Gravity.BOTTOM,
                0, 0);

    }

    // 重载一个方法，只仅仅局限于H5那边回调。
    public void showSharePopForH5(View xWalkView) {
        OnClickListener shareListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
                int i = v.getId();
                if (i == R.id.tv_weixinquan) {
                    sendWX(SendMessageToWX.Req.WXSceneTimeline);
                    wxPath = "朋友圈";

                } else if (i == R.id.tv_weixinfriend) {
                    sendWX(SendMessageToWX.Req.WXSceneSession);
                    wxPath = "微信好友";

                } else if (i == R.id.tv_qq) {
                    share2QQ();

                } else if (i == R.id.tv_cancel) {
                    EventBus.getDefault().post(new ShareStatus(ShareStatus.ShareCancel, "按取消键分享取消"));
                    dismissPop();

                } else {
                }
            }
        };
        shareLayout = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.popupwindow_share, null);
        tv_cancel = (TextView) shareLayout.findViewById(R.id.tv_cancel);
        tv_weixinfriend = (TextView) shareLayout
                .findViewById(R.id.tv_weixinfriend);
        tv_weixinquan = (TextView) shareLayout.findViewById(R.id.tv_weixinquan);
//        tv_sina = (TextView) shareLayout.findViewById(R.id.tv_sina);
        tv_qq = (TextView) shareLayout.findViewById(R.id.tv_qq);
        tv_cancel.setOnClickListener(shareListener);
        tv_weixinfriend.setOnClickListener(shareListener);
        tv_weixinquan.setOnClickListener(shareListener);
//        tv_sina.setOnClickListener(this);
        tv_qq.setOnClickListener(shareListener);
        tv_qq.setVisibility(View.VISIBLE);

        mPopupWindow = new PopupWindow(shareLayout, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setOutsideTouchable(false);
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();

        activity.getWindow().setAttributes(lp);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopupWindow.showAtLocation(xWalkView, Gravity.CENTER | Gravity.BOTTOM,
                0, 0);

    }

    private IWeiboShareAPI mWeiboShareAPI;

    public IWeiboShareAPI getmWeiboShareAPI() {
        return mWeiboShareAPI;
    }

//    private void share2Sina() {
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                sendMultiMessage();
//            }
//        }).start();
//
//    }

    private void sendMultiMessage() {
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.mediaObject = getTextObj();
        weiboMessage.imageObject = getImageObj();
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        // mWeiboShareAPI.sendRequest(ShareActivity.this,request);
        // //发送请求消息到微博，唤起微博分享界面

        String token = "";
        if (mAccessToken != null) {
            token = mAccessToken.getToken();
        }
//        mWeiboShareAPI.sendRequest(context, request, mAuthInfo, token,
//                new AuthListener());
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {

        TextObject textObject = new TextObject();
        textObject.text = textContent + " " + webpageUrl;
        textObject.title = title;
        textObject.actionUrl = webpageUrl;
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        Bitmap bitmap = ImageManager.getBitmap(imgUrl, new ImageSize(50, 50));
        if (bitmap == null)
            bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.icon_launcher);
        imageObject.setImageObject(bitmap);
        imageObject.title = title;
        imageObject.description = textContent;
        return imageObject;
    }

    private void share2QQ() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
                QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, textContent);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, webpageUrl);
        if (!TextUtils.isEmpty(imgUrl))
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
        else
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, Const.SHARE_ICON_URL);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "掌上糖医");
        // params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, "其他附加功能");
        mTencent.shareToQQ(activity, params, this);
        dismissPop();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        mTencent.onActivityResult(requestCode, resultCode, data);
//        // SSO 授权回调
//        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
//        // if (mSsoHandler != null) {
//        // mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//        // }
//    }

    private void sendWX(final int scene) {
        dismissPop();
        if (!api.isWXAppInstalled()) {
            EventBus.getDefault().post(new ShareStatus(ShareStatus.ShareFail, "微信没有安装，分享失败"));
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast(context.getString(R.string.check_weixin));
                }
            });
            return;
        }
        ThreadPoolTools.getInstance().postWorkerTask(new Runnable() {
            @Override
            public void run() {
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = webpageUrl;
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = title;
                msg.description = textContent;
                Bitmap bitmap = ImageManager.getBitmap(imgUrl, new ImageSize(50, 50));
                if (bitmap != null) {
                    msg.thumbData = bmpToByteArray(bitmap, true);
                } else {
                    Bitmap thumb = BitmapFactory.decodeResource(getResources(),
                            R.drawable.zhangshangtangyilogo);
                    Bitmap thumbBmp = Bitmap.createScaledBitmap(thumb, 50,
                            50, true);
                    msg.thumbData = bmpToByteArray(thumbBmp, true);
                }

                // 构造一个Req
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage"); // transaction字段用于唯一标识一个请求
                req.message = msg;
                req.scene = scene;

                // 调用api接口发送数据到微信
                boolean sd = api.sendReq(req);
                if (sd) {
                    AppImpl.getAppRroxy().setWXEntryActivityShareReward(false);
                    AppImpl.getAppRroxy().setWXEntryActivityisShare(true);
                    AppImpl.getAppRroxy().setWXEntryActivityloged(false);
                    AppImpl.getAppRroxy().setWXEntryActivitysharedmessage(maiDianCanSu + wxPath);
//                    WXEntryActivity.isShareReward = false;
//                    WXEntryActivity.isShare = true;
//                    WXEntryActivity.loged = false;
//                    WXEntryActivity.sharedmessage = maiDianCanSu + wxPath;
                }
            }
        });
    }

    public byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }


    public void dismissPop() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.alpha = 1f;
            activity.getWindow().setAttributes(lp);
            mPopupWindow = null;

        }

    }

    @Override
    public void onCancel() {
        // TODO Auto-generated method stub
        Toast("取消分享");

    }

    private void Toast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        EventBus.getDefault().post(new ShareStatus(ShareStatus.ShareCancel, "QQ分享用户在QQ分享界面回退取消分享"));
//        Toast(msg);
    }

    @Override
    public void onComplete(Object arg0) {
        // TODO Auto-generated method stub
       AppImpl.getAppRroxy().addLog(context, maiDianCanSu + "QQ好友");
        Toast("QQ分享成功");
        EventBus.getDefault().post(new ShareStatus(ShareStatus.ShareSuccess, "QQ分享成功"));
    }

    @Override
    public void onError(UiError arg0) {
        // TODO Auto-generated method stub
        String msg = arg0.errorMessage;
        if (TextUtils.isEmpty(msg)) {
            msg = "分享失败";
        }
        Toast(msg);
        EventBus.getDefault().post(new ShareStatus(ShareStatus.ShareFail, "QQ分享失败"));
    }

    @Override
    public void onClick(View v) {
        dismissPop();

        int i = v.getId();
        if (i == R.id.tv_weixinquan) {
            sendWX(SendMessageToWX.Req.WXSceneTimeline);
            wxPath = "朋友圈";

        } else if (i == R.id.tv_weixinfriend) {
            sendWX(SendMessageToWX.Req.WXSceneSession);
            wxPath = "微信好友";

        } else if (i == R.id.tv_qq) {
            share2QQ();

//            case R.id.tv_sina:
//                Intent weiboIntent = new Intent(Intent.ACTION_SEND);
//                weiboIntent.setType("text/plain");
//                PackageManager pm = context.getPackageManager();
//                List<ResolveInfo> matches = pm.queryIntentActivities(weiboIntent,
//                        PackageManager.MATCH_DEFAULT_ONLY);
//                String packageName = "com.sina.weibo";
//                ResolveInfo info = null;
//                for (ResolveInfo each : matches) {
//                    String pkgName = each.activityInfo.applicationInfo.packageName;
//                    if (packageName.equals(pkgName)) {
//                        info = each;
//                        break;
//                    }
//                }
//                if (info == null) {
//                    context.getPackageManager().setComponentEnabledSetting(new ComponentName(context.getApplicationContext(), SinaShareActivity.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
//                    share2Sina();
//                    break;
//                }
//                context.getPackageManager().setComponentEnabledSetting(new ComponentName(context.getApplicationContext(), SinaShareActivity.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//                Intent i = new Intent(context, SinaShareActivity.class);
//                i.putExtra("maiDianCanSu", maiDianCanSu);
//                i.putExtra("textContent", textContent);
//                i.putExtra("webpageUrl", webpageUrl);
//                i.putExtra("title", title);
//                i.putExtra("imgUrl", imgUrl);
//                context.startActivity(i);
//                break;
        } else if (i == R.id.tv_cancel) {
            dismissPop();

        } else {
        }
    }

//    ShareOnListener shareListener = new ShareOnListener() {
//        @Override
//        public void onClick(View v) {
//            dismissPop();
//            switch (v.getId()) {
//                case R.id.tv_weixinquan:
//                    sendWX(SendMessageToWX.Req.WXSceneTimeline);
//                    wxPath = "朋友圈";
//                    break;
//                case R.id.tv_weixinfriend:
//                    sendWX(SendMessageToWX.Req.WXSceneSession);
//                    wxPath = "微信好友";
//                    break;
//                case R.id.tv_qq:
//                    share2QQ();
//                    break;
//                case R.id.tv_cancel:
//                    HandlerH5Utils.jsOnFailCallBack();
//                    dismissPop();
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    };

    /**
     * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #} 中调用
     * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
     * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
     * SharedPreferences 中。
     */
    @SuppressLint("NewApi")
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values); // 从
            // Bundle
            // 中解析
            // Token
            if (mAccessToken.isSessionValid()) {
                // AccessTokenKeeper.writeAccessToken(WBAuthActivity.this,
                // mAccessToken); //保存Token
               AppImpl.getAppRroxy().addLog(context, maiDianCanSu + "新浪微博");
                Toast("新浪微博分享成功");
                try {
                    KeyValueDBService.getInstance().put(
                            Const.CHR_WEIBO_AccessToken, mAccessToken.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // .........

            } else {
                // 当您注册的应用程序签名不正确时，就会收到错误Code，请确保签名正确
                String code = values.getString("code", "");
                Toast.makeText(context, "新浪微博授权失败",
                        Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onCancel() {
            Toast.makeText(context, "取消新浪微博分享", Toast.LENGTH_LONG)
                    .show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(context,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }
}
