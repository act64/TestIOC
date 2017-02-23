package com.jkyssocial.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jkys.jkysbase.data.NetWorkResult;
import com.jkys.jkyswidget.ConfirmTipsDialog;
import com.jkys.jkyswidget.MyListView;
import com.jkyssocial.activity.CircleMainActivity;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.Circle;
import com.jkyssocial.data.CircleListResult;
import com.jkyssocial.event.ChangeUserInfoEvent;
import com.jkyssocial.event.FollowCircleEvent;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mintcode.util.ImageManager;

import org.jsoup.helper.StringUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.dreamplus.wentang.BuildConfig;
import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;


/**
 * Created by on
 * Author: Zern
 * DATE: 2015/12/15
 * Time: 11:33
 * email: AndroidZern@163.com
 */
public class AllCircleTypeAdapter extends BaseAdapter implements MyListView.OnLoadListener, SwipeRefreshLayout.OnRefreshListener, RequestManager.RequestListener<CircleListResult>, AdapterView.OnItemClickListener {
    //    private Activity activity;
    private WeakReference<Activity> activityWR;
    private Context context;
    private List<Circle> datas;
    private int REFRESH_CODE = 1;

    private int LOAD_MORE_CODE = 2;

    final int COUNT = 20;

    private String baseLine;

    MyListView mListView;

    SwipeRefreshLayout swipeRefreshLayout;

    String code;

    public AllCircleTypeAdapter(Activity activity, MyListView mListView, SwipeRefreshLayout swipeRefreshLayout, String code) {
//        this.activity = activity;
        activityWR = new WeakReference<Activity>(activity);
        context = activity.getApplicationContext();
        this.mListView = mListView;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.code = code;
        datas = new ArrayList<Circle>();
        getData(null);
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (datas != null) {
            ret = datas.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void getData(final String baseLine) {
        int requestCode = (baseLine == null) ? REFRESH_CODE : LOAD_MORE_CODE;
        swipeRefreshLayout.setRefreshing(baseLine == null);
        ApiManager.listCircleForClass(this, requestCode, context, baseLine, COUNT, code);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        // 复用
        if (convertView != null) {
            view = convertView;
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_allcircle_type, parent, false);
        }
        // ViewHolder
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.item_allcircle_img = (RoundedImageView) view.findViewById(R.id.item_allcircle_img);
            holder.item_allcircle_member = (TextView) view.findViewById(R.id.item_allcircle_member);
            holder.item_allcircle_name = (TextView) view.findViewById(R.id.item_allcircle_name);
            holder.item_allcircle_decribute = (TextView) view.findViewById(R.id.item_allcircle_decribute);
            holder.item_allcircle_enter = (TextView) view.findViewById(R.id.item_allcircle_enter);
            view.setTag(holder);
        }
        holder.position = position;
        // 数据填充
        Circle circle = datas.get(position);
        // 圈子描述:
        if (!TextUtils.isEmpty(circle.getSummary())) {
            holder.item_allcircle_decribute.setText(circle.getSummary() + "");
        }
        // 圈子名字 title
        holder.item_allcircle_name.setText(circle.getTitle() + "");
        // 加入的按钮
        holder.item_allcircle_enter.setTag(holder);
        if (circle.getHasMe() == 1) {
            holder.item_allcircle_enter.setText("已加入");
            holder.item_allcircle_enter.setBackgroundDrawable(null);
            holder.item_allcircle_enter.setTextColor(ContextCompat.getColor(context, R.color.text_999999));
        } else {
            holder.item_allcircle_enter.setText("加入");
            holder.item_allcircle_enter.setBackgroundResource(R.drawable.shape_care);
            holder.item_allcircle_enter.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
        holder.item_allcircle_enter.setOnClickListener(new AttentionClickListener());
        // 圈子成员数量
        int memberCount = circle.getStat() == null ? 0 : circle.getStat().getMemberCount();
        holder.item_allcircle_member.setText("成员: " + memberCount);
        if (!TextUtils.isEmpty(circle.getAvatar())) {
            ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + circle.getAvatar(), null,
                    holder.item_allcircle_img, ImageManager.circleAvatarOptions);
        }

        return view;
    }

    @Override
    public void onLoad() {
        getData(baseLine);
    }

    @Override
    public void onRefresh() {
        getData(null);
    }

    public void followCircle(String circleId, int follow) {
        for (Circle circle : datas) {
            if (circle != null && circle.getId() != null && circle.getId().equals(circleId)) {
                circle.setHasMe(follow);
                notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void processResult(int requestCode, int resultCode, CircleListResult data) {
        if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
            if (data.getReturnCode().equals("0000")) {
                baseLine = data.getBaseLine();
                if (data.getCircleList() != null && !data.getCircleList().isEmpty()) {
                    if (requestCode == REFRESH_CODE) {
                        datas = new ArrayList<Circle>();
                        mListView.resumeLoad();
                    }
                    datas.addAll(data.getCircleList());
                    notifyDataSetChanged();
                } else {
                    if (requestCode != REFRESH_CODE)
                        mListView.forbidLoad("已经全部加载完毕", true);
                }
            }
        } else {
            //TODO
        }
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        mListView.endLoad();
    }

    private class ViewHolder {
        private RoundedImageView item_allcircle_img;
        private TextView item_allcircle_name, item_allcircle_decribute, item_allcircle_member;
        private TextView item_allcircle_enter;
        private int position;
    }

    class AttentionClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            final ViewHolder holder = (ViewHolder) v.getTag();
            final Circle circle = datas.get(holder.position);
            if (circle.getHasMe() == 1) {
                if (activityWR == null || activityWR.get() == null)
                    return;
                ;
                Activity activity = activityWR.get();
                ConfirmTipsDialog dialog = new ConfirmTipsDialog(activity, "确认取消吗？", new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        ApiManager.followCircle(new RequestManager.RequestListener<NetWorkResult>() {
                            @Override
                            public void processResult(int requestCode, int resultCode, NetWorkResult data) {
                                if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                                    ViewHolder holder = (ViewHolder) v.getTag();
                                    if (datas != null && requestCode < datas.size() && holder != null) {
                                        datas.get(requestCode).setHasMe(0);
                                        if (holder.position == requestCode) {
                                            holder.item_allcircle_enter.setBackgroundResource(R.drawable.shape_care);
                                            holder.item_allcircle_enter.setTextColor(ContextCompat.getColor(context, R.color.white));
                                            holder.item_allcircle_enter.setText("加入");
                                            EventBus.getDefault().post(new FollowCircleEvent(circle.getId(), 0));
                                            EventBus.getDefault().post(new ChangeUserInfoEvent());
                                        }
                                    }
                                }
                            }
                        }, holder.position, context, circle.getId(), 0);
                    }
                });
                dialog.setTag(holder);
                dialog.show();

            } else {
                ApiManager.followCircle(new RequestManager.RequestListener<NetWorkResult>() {
                    @Override
                    public void processResult(int requestCode, int resultCode, NetWorkResult data) {
                        if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                            if (datas != null && requestCode < datas.size()) {
                                datas.get(requestCode).setHasMe((byte) 1);
                                ViewHolder holder = (ViewHolder) v.getTag();
                                if (holder.position == requestCode) {
                                    holder.item_allcircle_enter.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                                    holder.item_allcircle_enter.setTextColor(ContextCompat.getColor(context, R.color.text_999999));
                                    holder.item_allcircle_enter.setText("已加入");
                                    EventBus.getDefault().post(new FollowCircleEvent(circle.getId(), 1));
                                    EventBus.getDefault().post(new ChangeUserInfoEvent());

                                }
                            }
                        }
                    }
                }, holder.position, context, circle.getId(), 1);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (activityWR == null || activityWR.get() == null)
            return;
        ;
        Activity activity = activityWR.get();
        Intent intent = new Intent(activity, CircleMainActivity.class);
        Circle circle = datas.get(position);
        if (circle != null) {
            intent.putExtra("circle", circle);
        }
        activity.startActivity(intent);
    }

}
