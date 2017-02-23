package com.jkyssocial.common.manager;

import android.app.Activity;
import android.content.Context;
import com.jkyssocial.data.SubmitAskResult;
import com.jkys.jkysbase.data.NetWorkResult;
import com.jkyssocial.adapter.NewDynamicListAdapter;
import com.jkyssocial.data.AddCommentResult;
import com.jkyssocial.data.AddDynamicResult;
import com.jkyssocial.data.AddReplyResult;
import com.jkyssocial.data.CircleClassListResult;
import com.jkyssocial.data.CircleFansResult;
import com.jkyssocial.data.CircleListAppIndexResult;
import com.jkyssocial.data.CircleListResult;
import com.jkyssocial.data.CircleResult;
import com.jkyssocial.data.CommentListResult;
import com.jkyssocial.data.DynamicListResult;
import com.jkyssocial.data.GetDynamicResult;
import com.jkyssocial.data.GetUserInfoResult;
import com.jkyssocial.data.ListBuddyResult;
import com.jkyssocial.data.ListCircleForUserResult;
import com.jkyssocial.data.ListMessageResult;
import com.jkyssocial.data.OrderCircleForUserResult;
import com.jkyssocial.data.TopicListResult;
import com.jkyssocial.data.TrumpHomeSocialData;
import com.mintcode.area_patient.area_task.TaskListener;
import com.mintcode.util.Keys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.dreamplus.wentang.BuildConfig;

//import com.jkyssocial.data.SumbitAskResult;

/**
 * Created by yangxiaolong on 15/9/2.
 */
public class ApiManager {

    public static final int TYPE_REQUEST_WITH_CACHE = 1;
    public static final int TYPE_REQUEST_UPDATE_CACHE = 2;
    public static final int TYPE_REQUEST_WITHOUT_CACHE = 3;

    public static void listBuddyForRecommend(final RequestManager.RequestListener<ListBuddyResult> listener,
                                             final int requestCode, final Context context) {
        String action = "20/listBuddyForRecommend";
        Map<String, Object> params = new HashMap<String, Object>();
        RequestManager.getInstance().loadRequestWithNetWork(ListBuddyResult.class, action,
                BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context.getApplicationContext(), params);
    }

    public static void listExpPatient(final RequestManager.RequestListener<ListBuddyResult> listener, final int requestCode, final Context context,
                                      String baseLine, int count) {
        String action = "20/listExpPatient";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("baseLine", baseLine);
        params.put("count", count);
        RequestManager.getInstance().loadRequestWithNetWork(ListBuddyResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    // Zern(yuwenzhe) 社区全部圈子 2.0之后的 --------------------------------------------------------------------------------------------------------------

    public static void getCircleInfo(final RequestManager.RequestListener<CircleResult> listener, final int requestCode, final Context context,
                                     String circleId) {
        String action = "20/getCircleInfo";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("circleId", circleId);
        RequestManager.getInstance().loadRequestWithNetWork(CircleResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    // 请求圈子类别列表
    public static void listCircleClass(final RequestManager.RequestListener<CircleClassListResult> listener, final int requestCode, final Context context) {
        String action = "10/listCircleClass";
        RequestManager.getInstance().loadRequestWithNetWork(CircleClassListResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, null);
    }

    // 请求分类圈子列表
    public static void listCircleForClass(final RequestManager.RequestListener<CircleListResult> listener, final int requestCode, final Context context,
                                          String baseLine, int count, String classCode) {
        String action = "20/listCircleForClass";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("baseLine", baseLine);
        params.put("count", count);
        params.put("classCode", classCode);
        RequestManager.getInstance().loadRequestWithNetWork(CircleListResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    // 请求某个圈子的成员列表
    public static void listCircleFans(final RequestManager.RequestListener<CircleFansResult> listener, final int requestCode, final Context context, String baseLine, int count, String circleId) {
        String action = "20/listCircleFans";
        Map<String, Object> params = new HashMap<>();
        params.put("baseLine", baseLine);
        params.put("count", count);
        params.put("circleId", circleId);
        RequestManager.getInstance().loadRequestWithNetWork(CircleFansResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    // 请求我加入圈子列表的数据 2.0
    public static void listCircleForUserV2(final RequestManager.RequestListener<ListCircleForUserResult> listener, final int requestCode, final Context context, String buddyId) {
        String action = "20/listCircleForUser";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("buddyId", buddyId);
        RequestManager.getInstance().loadRequestWithNetWork(ListCircleForUserResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
//        RequestManager.getInstance().loadRequestWithNetWork(ListCircleForUserResult.class, action, "http://10.0.50.162:8095/api/", listener, requestCode, context, params);
    }

    // 上传调整我加入圈子顺序
    public static void orderCircleForUser(final RequestManager.RequestListener<OrderCircleForUserResult> listener, final int requestCode, final Context context, final List<String> circleIdList) {
        String action = "20/orderCircleForUser";
        Map<String, Object> params = new HashMap<>();
        params.put("circleIdList", circleIdList);
//        RequestManager.getInstance().loadRequestWithNetWork(OrderCircleForUserResult.class, action, "http://10.0.50.162:8095/api/", listener, requestCode, context, params);
        RequestManager.getInstance().loadRequestWithNetWork(OrderCircleForUserResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    // 新建我的圈子
    public static void addCircle(final RequestManager.RequestListener<CircleResult> listener, final int requestCode, final Context context,
                                 String circlePic, String circleName, String circleClassCode, String circleDesc) {
        String action = "10/addCircle";
        Map<String, Object> params = new HashMap<>();
        params.put("circlePic", circlePic);
        params.put("circleName", circleName);
        params.put("circleClassCode", circleClassCode);
        params.put("circleDesc", circleDesc);
        RequestManager.getInstance().loadRequestWithNetWork(CircleResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    // 编辑圈子
    public static void editCircle(final RequestManager.RequestListener<CircleResult> listener, final int requestCode, final Context context,
                                  String circleId, String circlePic, String circleName, String circleTypeCode, String circleDesc) {
        String action = "10/editCircle";
        Map<String, Object> params = new HashMap<>();
        params.put("circleId", circleId);
        params.put("circlePic", circlePic);
        params.put("circleName", circleName);
        params.put("circleClassCode", circleTypeCode);
        params.put("circleDesc", circleDesc);
        RequestManager.getInstance().loadRequestWithNetWork(CircleResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);

    }

    // 解散我建立的圈子
    public static void removeCircle(final RequestManager.RequestListener<NetWorkResult> listener, final int requestCode, final Context context,
                                    String circleId) {
        String action = "10/removeCircle";
        Map<String, Object> params = new HashMap<>();
        params.put("circleId", circleId);
        RequestManager.getInstance().loadRequestWithNetWork(NetWorkResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

//    // 申请加入圈子
//    public static void followCircle(final RequestManager.RequestListener<NetWorkResult> listener, final int requestCode ,final Context context ,
//                                    String circleId,byte follow){
//        String action = "10/followCircle" ;
//        Map<String,Object> params = new HashMap<>() ;
//        params.put("circleId", circleId) ;
//        params.put("follow",follow) ;
//        RequestManager.getInstance().loadRequestWithNetWork(NetWorkResult.class,action,BuildConfig.SOCIAL_SERVER_PATH,listener,requestCode,context,params);
//    }

    public static void listExpDoctor(final RequestManager.RequestListener<ListBuddyResult> listener, final int requestCode, final Context context,
                                     String baseLine, int count) {
        String action = "20/listExpDoctor";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("baseLine", baseLine);
        params.put("count", count);
        RequestManager.getInstance().loadRequestWithNetWork(ListBuddyResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void listIdol(final RequestManager.RequestListener<ListBuddyResult> listener, final int requestCode, final Context context,
                                String buddyId, Long baseTime, int count) {
        String action = "10/listIdol";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("buddyId", buddyId);
        params.put("baseTime", baseTime);
        params.put("count", count);
        RequestManager.getInstance().loadRequestWithNetWork(ListBuddyResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void listFans(final RequestManager.RequestListener<ListBuddyResult> listener, final int requestCode, final Context context,
                                String buddyId, Long baseTime, int count) {
        String action = "10/listFans";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("buddyId", buddyId);
        params.put("baseTime", baseTime);
        params.put("count", count);
        RequestManager.getInstance().loadRequestWithNetWork(ListBuddyResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void listLiker(final RequestManager.RequestListener<ListBuddyResult> listener, final int requestCode, final Context context,
                                 String dynamicId, Long baseTime, int count) {
        String action = "10/listLiker";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dynamicId", dynamicId);
        params.put("baseTime", baseTime);
        params.put("count", count);
        RequestManager.getInstance().loadRequestWithNetWork(ListBuddyResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void addDynamic(final RequestManager.RequestListener<AddDynamicResult> listener, final int requestCode, final Context context,
                                  String content, List<String> picUrls, String circleId, String title) {
        String action = "10/addDynamic";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("content", content);
        params.put("picUrls", picUrls);
        params.put("circleId", circleId);
        params.put("title", title);
        RequestManager.getInstance().loadRequestWithNetWork(AddDynamicResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void addDynamicNew(final RequestManager.RequestListener<AddDynamicResult> listener, final int requestCode, final Context context,
                                     String content, List<String> picUrls, String topicId, String title) {
        String action = "30/addDynamic";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("content", content);
        params.put("picUrls", picUrls);
        params.put("topicId", topicId);
        params.put("title", title);
        RequestManager.getInstance().loadRequestWithNetWork(AddDynamicResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void addComment(final RequestManager.RequestListener<AddCommentResult> listener, final int requestCode, final Context context,
                                  String dynamicId, String content) {
        String action = "10/addComment";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("content", content);
        params.put("dynamicId", dynamicId);
        RequestManager.getInstance().loadRequestWithNetWork(AddCommentResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void addReply(final RequestManager.RequestListener<AddReplyResult> listener, final int requestCode, final Context context,
                                String dynamicId, String commentId, String targetBuddyId, String content) {
        String action = "10/addReply";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("content", content);
        params.put("dynamicId", dynamicId);
        params.put("commentId", commentId);
        params.put("targetBuddyId", targetBuddyId);
        RequestManager.getInstance().loadRequestWithNetWork(AddReplyResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void listDynamicForNew(final RequestManager.RequestListener<DynamicListResult> listener, final int requestCode, final Context context,
                                         Long baseTime, int count) {
        String action = "10/listDynamicForNew";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("baseTime", baseTime);
        params.put("count", count);
        RequestManager.getInstance().loadRequestWithNetWork(DynamicListResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void listDynamicForUser(final RequestManager.RequestListener<DynamicListResult> listener, final int requestCode, final Context context,
                                          String buddyId, Long baseTime, int count) {
//        Log.e("内存", "监听1=" + listener);
        String action = "10/listDynamicForUser";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("buddyId", buddyId);
        params.put("baseTime", baseTime);
        params.put("count", count);
        RequestManager.getInstance().loadRequestWithNetWork(DynamicListResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void listDynamicForRecommend(final RequestManager.RequestListener<DynamicListResult> listener, final int requestCode, final Context context,
                                               String baseLine, int count) {
        String action = "20/listDynamicForRecommend";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("baseLine", baseLine);
        params.put("count", count);
        if (requestCode == NewDynamicListAdapter.CREATE_CODE)
            RequestManager.getInstance().loadRequestWithNetWork(DynamicListResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params, true);
        else
            RequestManager.getInstance().loadRequestWithNetWork(DynamicListResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void listDynamicForRecommendNew(final RequestManager.RequestListener<DynamicListResult> listener, final int requestCode, final Context context,
                                               String baseLine, int count) {
        String action = "30/listDynamicForRecommend";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("baseLine", baseLine);
        params.put("count", count);
        if (requestCode == NewDynamicListAdapter.CREATE_CODE)
            RequestManager.getInstance().loadRequestWithNetWork(DynamicListResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params, true);
        else
            RequestManager.getInstance().loadRequestWithNetWork(DynamicListResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void listTopic(final RequestManager.RequestListener<TopicListResult> listener, final Context context,
                                 String baseLine, int count, int status) {
        String action = "30/listTopic";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("baseLine", baseLine);
        params.put("count", count);
        params.put("status", status);
        RequestManager.getInstance().loadRequestWithNetWork(TopicListResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, 1, context, params, true);
    }

    public static void listLatestDynamic(final RequestManager.RequestListener<DynamicListResult> listener, final int requestCode, final Context context,
                                         String baseLine, int count) {
        String action = "30/listLatestDynamic";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("baseLine", baseLine);
        params.put("count", count);
        if (requestCode == NewDynamicListAdapter.CREATE_CODE)
            RequestManager.getInstance().loadRequestWithNetWork(DynamicListResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params, true);
        else
            RequestManager.getInstance().loadRequestWithNetWork(DynamicListResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void listDynamicForCircle(RequestManager.RequestListener<DynamicListResult> listener, final int requestCode, final Context context,
                                            String circleId, String baseLine, int count) {
        String action = "10/listDynamicForCircle";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("circleId", circleId);
        params.put("baseLine", baseLine);
        params.put("count", count);
        RequestManager.getInstance().loadRequestWithNetWork(DynamicListResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void listDynamicForTopic(RequestManager.RequestListener<DynamicListResult> listener, final int requestCode, final Context context,
                                           String circleId, String baseLine, int count) {
        String action = "30/listDynamicForTopic";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("topicId", circleId);
        params.put("baseLine", baseLine);
        params.put("count", count);
        RequestManager.getInstance().loadRequestWithNetWork(DynamicListResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void like(final RequestManager.RequestListener<NetWorkResult> listener, final int requestCode, final Context context,
                            String dynamicId, int like) {
        String action = "10/like";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dynamicId", dynamicId);
        params.put("like", like);
        RequestManager.getInstance().loadRequestWithNetWork(NetWorkResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void listComment(final RequestManager.RequestListener<CommentListResult> listener, final int requestCode, final Context context,
                                   String dynamicId, Long baseTime, int count) {
        String action = "10/listComment";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dynamicId", dynamicId);
        params.put("baseTime", baseTime);
        params.put("count", count);
        RequestManager.getInstance().loadRequestWithNetWork(CommentListResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void listTopDynamic(final RequestManager.RequestListener<TrumpHomeSocialData> listener, final int requestCode, final Context context,
                                      Long baseLine, int count) {
        String action = "30/listTopDynamic";
        Map<String, Object> params = new HashMap<>();
        params.put("baseLine", baseLine);
        params.put("count", count);
        RequestManager.getInstance().loadRequestWithNetWork(TrumpHomeSocialData.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void getUserInfo(int type, final RequestManager.RequestListener<GetUserInfoResult> listener, final int requestCode, final Context context,
                                   String buddyId) {
        String action = "10/getUserInfo";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("buddyId", buddyId);
        switch (type) {
            case TYPE_REQUEST_WITH_CACHE:
                RequestManager.getInstance().loadRequestWithCache(GetUserInfoResult.class, Keys.SOCIAL_MY_USER_INFO, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
                break;
            case TYPE_REQUEST_UPDATE_CACHE:
                RequestManager.getInstance().loadRequestUpdateCache(GetUserInfoResult.class, Keys.SOCIAL_MY_USER_INFO, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
                break;
            case TYPE_REQUEST_WITHOUT_CACHE:
                RequestManager.getInstance().loadRequestWithNetWork(GetUserInfoResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
                break;
            default:
                RequestManager.getInstance().loadRequestWithNetWork(GetUserInfoResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
        }
    }

    public static void getUserInfo(int type, final RequestManager.RequestListener<GetUserInfoResult> listener, final int requestCode, final Context context,
                                   int userId) {
        String action = "10/getUserInfo";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        switch (type) {
            case TYPE_REQUEST_WITH_CACHE:
                RequestManager.getInstance().loadRequestWithCache(GetUserInfoResult.class, Keys.SOCIAL_MY_USER_INFO, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
                break;
            case TYPE_REQUEST_UPDATE_CACHE:
                RequestManager.getInstance().loadRequestUpdateCache(GetUserInfoResult.class, Keys.SOCIAL_MY_USER_INFO, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
                break;
            case TYPE_REQUEST_WITHOUT_CACHE:
                RequestManager.getInstance().loadRequestWithNetWork(GetUserInfoResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
                break;
            default:
                RequestManager.getInstance().loadRequestWithNetWork(GetUserInfoResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
        }
    }

    public static void report(final RequestManager.RequestListener<NetWorkResult> listener, final int requestCode, final Context context,
                              int targetType, String targetId, String content) {
        String action = "10/report";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("targetType", targetType);
        params.put("targetId", targetId);
        params.put("content", content);
        RequestManager.getInstance().loadRequestWithNetWork(NetWorkResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void removeDynamic(final RequestManager.RequestListener<NetWorkResult> listener, final int requestCode, final Context context,
                                     String dynamicId) {
        String action = "10/removeDynamic";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dynamicId", dynamicId);
        RequestManager.getInstance().loadRequestWithNetWork(NetWorkResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void removeComment(final RequestManager.RequestListener<NetWorkResult> listener, final int requestCode, final Context context,
                                     String dynamicId, String commentId) {
        String action = "10/removeComment";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dynamicId", dynamicId);
        params.put("commentId", commentId);
        RequestManager.getInstance().loadRequestWithNetWork(NetWorkResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void removeReply(final RequestManager.RequestListener<NetWorkResult> listener, final int requestCode, final Context context,
                                   String dynamicId, String commentId, String replyId) {
        String action = "10/removeReply";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dynamicId", dynamicId);
        params.put("commentId", commentId);
        params.put("replyId", replyId);
        RequestManager.getInstance().loadRequestWithNetWork(NetWorkResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void modifyMood(final RequestManager.RequestListener<NetWorkResult> listener, final int requestCode, final Context context,
                                  String mood) {
        String action = "10/modifyMood";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mood", mood);
        RequestManager.getInstance().loadRequestWithNetWork(NetWorkResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void modifyName(final RequestManager.RequestListener<NetWorkResult> listener, final int requestCode, final Context context,
                                  String nickname) {
        String action = "10/modifyName";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nickname", nickname);
        RequestManager.getInstance().loadRequestWithNetWork(NetWorkResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void modifySpaceBg(final RequestManager.RequestListener<NetWorkResult> listener, final int requestCode, final Context context,
                                     String bgImgUrl) {
        String action = "10/modifySpaceBg";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("bgImgUrl", bgImgUrl);
        RequestManager.getInstance().loadRequestWithNetWork(NetWorkResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void modifyAvatar(final RequestManager.RequestListener<NetWorkResult> listener, final int requestCode, final Context context,
                                    String imgUrl) {
        String action = "10/modifyAvatar";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("imgUrl", imgUrl);
        RequestManager.getInstance().loadRequestWithNetWork(NetWorkResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void followUser(final RequestManager.RequestListener<NetWorkResult> listener, final int requestCode, final Context context,
                                  String buddyId, int follow) {
        String action = "10/followUser";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("buddyId", buddyId);
        params.put("follow", follow);
        RequestManager.getInstance().loadRequestWithNetWork(NetWorkResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void followCircle(final RequestManager.RequestListener<NetWorkResult> listener, final int requestCode, final Context context,
                                    String circleId, int follow) {
        String action = "10/followCircle";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("circleId", circleId);
        params.put("follow", follow);
        // 如果是加入圈子 那么去判断是否是第一次加入圈子 是有奖励
        if (follow == 1) {
            if (context != null && context instanceof Activity) {
                TaskAPI.getInstance(context.getApplicationContext()).getTaskReward(new TaskListener((Activity) context, "首次加入圈子奖励"), "social/join_circle");
            }
        }
        RequestManager.getInstance().loadRequestWithNetWork(NetWorkResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void listMsg(final RequestManager.RequestListener<ListMessageResult> listener, final int requestCode, final Context context,
                               int qType, Long baseTime, int count) {
        String action = "10/listMsg";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("qType", qType);
        params.put("baseTime", baseTime);
        params.put("count", count);
        RequestManager.getInstance().loadRequestWithNetWork(ListMessageResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void getDynamic(final RequestManager.RequestListener<GetDynamicResult> listener, final int requestCode, final Context context,
                                  String dynamicId) {
        String action = "10/getDynamic";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dynamicId", dynamicId);
        RequestManager.getInstance().loadRequestWithNetWork(GetDynamicResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void listCircleForUser(final RequestManager.RequestListener<CircleListResult> listener, final int requestCode, final Context context) {
        String action = "20/listCircleForUser";
        Map<String, Object> params = new HashMap<String, Object>();
        RequestManager.getInstance().loadRequestWithNetWork(CircleListResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    public static void listCircleForRecommend(final RequestManager.RequestListener<CircleListResult> listener, final int requestCode, final Context context) {
        String action = "20/listCircleForRecommend";
        Map<String, Object> params = new HashMap<String, Object>();
        RequestManager.getInstance().loadRequestWithNetWork(CircleListResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params, true);
    }

    public static void listStar(final RequestManager.RequestListener<ListBuddyResult> listener, final int requestCode, final Context context) {
        String action = "20/listStar";
        Map<String, Object> params = new HashMap<String, Object>();
        RequestManager.getInstance().loadRequestWithNetWork(ListBuddyResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params, true);
    }

    public static void listAnnouncement(final RequestManager.RequestListener<DynamicListResult> listener, final int requestCode, final Context context) {
        String action = "10/listAnnouncement";
        Map<String, Object> params = new HashMap<String, Object>();
        RequestManager.getInstance().loadRequestWithNetWork(DynamicListResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params);
    }

    /* *
    首页糖友圈*/
    public static void listIndexCircleForRecommend(final RequestManager.RequestListener<CircleListAppIndexResult> listener, final int requestCode, final Context context, Byte diabetesType) {
        String action = "20/listIndexCircles";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("diabetesType", diabetesType);
        RequestManager.getInstance().loadRequestWithNetWork(CircleListAppIndexResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params, true);
    }


    /* *
    咨询医生*/
    public static void submitAskDoctor(final RequestManager.RequestListener<Integer> listener, final int requestCode, final Context context, SubmitAskResult submitAsk) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("diabetesType", submitAsk);
//        RequestManager.getInstance().loadRequestWithNetWork(CircleListAppIndexResult.class, action, BuildConfig.SOCIAL_SERVER_PATH, listener, requestCode, context, params, true);
    }
}
