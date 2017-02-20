package com.jkyssocial.handler;

import android.app.Activity;
import android.content.Context;

import com.jkys.jkysbase.data.NetWorkResult;
import com.jkys.jkysbase.data.UploadNetWorkResult;
import com.jkys.jkysbase.Constant;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.common.util.FileImageUpload;
import com.jkyssocial.data.AddDynamicResult;
import com.jkyssocial.data.Dynamic;
import com.jkyssocial.event.PublishDynamicEvent;
import com.jkyssocial.event.PublishFinishEvent;
import com.mintcode.area_patient.area_task.TaskAPI;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cn.dreamplus.wentang.BuildConfig;
import de.greenrobot.event.EventBus;

/**
 * Created by yangxiaolong on 15/9/21.
 */
public class PublishDynamicManager extends Thread implements RequestManager.RequestListener<AddDynamicResult> {

    public static List<Dynamic> sendingDynamicList = new ArrayList<>();

    /**
     * 上传图片异步程序
     */
    private AtomicInteger imageUploadingCount;

    /**
     * 是否正在发布
     */
    private boolean isPublishing;

    private Dynamic sendingDynamic;

    /**
     * 这里的path指的是服务器传回的地址
     */
    private List<String> picUpList;

    private Context mContext;

    private WeakReference<Context> weakReference;

    public PublishDynamicManager(Dynamic sendingDynamic, Context context) {
        imageUploadingCount = new AtomicInteger(0);
        picUpList = new ArrayList<String>();
        isPublishing = false;
        this.sendingDynamic = sendingDynamic;
        this.mContext = context.getApplicationContext();
        weakReference = new WeakReference<Context>(context);
    }

    @Override
    public void run() {
        if(sendingDynamic.getImages() != null) {
            for (int i = 0; i < sendingDynamic.getImages().size(); ++i) {
                File file = new File(sendingDynamic.getImages().get(i));
                if (file == null)
                    continue;
                String data = FileImageUpload.upLoadImage(1, file,
                        BuildConfig.SOCIAL_SERVER_PATH + "10/upload");
                UploadNetWorkResult jsonData;
                if (data != null) {
                    jsonData = Constant.GSON.fromJson(data, UploadNetWorkResult.class);
                    if (jsonData != null && NetWorkResult.SUCCESS.equals(jsonData.getReturnCode())) {
                        picUpList.add(jsonData.getUrl());
                    } else {
                        picUpList.add(null);
                    }
                }
            }
        }
        String circleId = sendingDynamic.getCircle() == null ? null : sendingDynamic.getCircle().getId();
        String topicId = sendingDynamic.getTopic() == null ? null : sendingDynamic.getTopic().getId();
        if(topicId != null)
            ApiManager.addDynamicNew(PublishDynamicManager.this, 1, mContext, sendingDynamic.getContent(), picUpList, topicId, sendingDynamic.getTitle());
        else
            ApiManager.addDynamic(PublishDynamicManager.this, 1, mContext, sendingDynamic.getContent(), picUpList, circleId, sendingDynamic.getTitle());
    }

    @Override
    public void processResult(int requestCode, int resultCode, AddDynamicResult data) {
        if (data != null && data.getDynamic() != null) {
            EventBus.getDefault().post(new PublishDynamicEvent(data.getDynamic(), sendingDynamic));
            sendingDynamicList.remove(sendingDynamic);
            if (weakReference!=null&&weakReference.get()!=null) {
                Context context = weakReference.get();
                TaskAPI.getInstance(context).CallAPITaskReward((Activity) context, "首次发动态", "每日发动态", "write_post/first", "article/post");
            }
        } else{ // TODO 服务器无法连接等异常，存草稿
            sendingDynamic.setStatusAndroid(2);
            EventBus.getDefault().post(new PublishFinishEvent(false));
        }
    }
}
