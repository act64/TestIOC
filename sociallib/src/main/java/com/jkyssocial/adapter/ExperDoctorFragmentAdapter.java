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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jkys.jkysbase.data.NetWorkResult;
import com.jkys.jkyswidget.ConfirmTipsDialog;
import com.jkys.jkyswidget.MyListView;
import com.jkys.tools.MainSelector;
import com.jkyssocial.activity.NewPersonalSpaceActivity;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.ListBuddyResult;
import com.jkyssocial.event.ChangeUserInfoEvent;
import com.jkyssocial.event.FollowUserEvent;
import com.mintcode.util.ImageManager;
import com.mintcode.util.LogUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.dreamplus.wentang.BuildConfig;
import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;


/**
 * Created by on
 * Author: Zern
 * DATE: 2015/12/14
 * Time: 10:32
 * email: AndroidZern@163.com
 */
public class ExperDoctorFragmentAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, MyListView.OnLoadListener, RequestManager.RequestListener<ListBuddyResult>, SwipeRefreshLayout.OnRefreshListener {
    //    private Activity activity;
    private WeakReference<Activity> activityWR;
    private Context context;
    private List<Buddy> datas;

    MyListView mListView;

    SwipeRefreshLayout swipeRefreshLayout;

    private int REFRESH_CODE = 1;

    private int LOAD_MORE_CODE = 2;

    final int COUNT = 20;

    private String baseLine;

    Buddy myBuddy;

    public ExperDoctorFragmentAdapter(Activity activity, MyListView mListView, SwipeRefreshLayout swipeRefreshLayout) {
        activityWR = new WeakReference<Activity>(activity);
        context = activity.getApplicationContext();
//        this.activity = activity;
        this.mListView = mListView;
        this.swipeRefreshLayout = swipeRefreshLayout;
        datas = new ArrayList<Buddy>();
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


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        // 1.复用
        if (convertView != null) {
            view = convertView;
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_zjys, parent, false);
        }
        // 2.ViewHolder
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.avatar = (ImageView) view.findViewById(R.id.avatar);
            holder.vFlag = (ImageView) view.findViewById(R.id.vFlag);
            holder.item_zjys_name = (TextView) view.findViewById(R.id.item_zjys_name);
            holder.item_zjys_type = (TextView) view.findViewById(R.id.item_zjys_type);
            holder.item_zjys_illage = (TextView) view.findViewById(R.id.item_zjys_illage);
            holder.item_zjys_care = (TextView) view.findViewById(R.id.item_zjys_care);
            view.setTag(holder);
        }
        holder.position = position;
        //3.数据填充
        // 得到当前item的buddy对象
        Buddy buddy = datas.get(position);
        // 给CheckBox设置标签
        holder.item_zjys_care.setTag(holder);
        if (myBuddy != null && myBuddy.getBuddyId().equals(buddy.getBuddyId())) {
            holder.item_zjys_care.setVisibility(View.GONE);
        } else {
            if (buddy.getIdolFlag() == 1) {
                holder.item_zjys_care.setText("已关注");
                holder.item_zjys_care.setBackgroundDrawable(null);
                holder.item_zjys_care.setTextColor(ContextCompat.getColor(context, R.color.text_999999));
            } else {
                holder.item_zjys_care.setText("关注");
                holder.item_zjys_care.setBackgroundResource(R.drawable.shape_care);
                holder.item_zjys_care.setTextColor(ContextCompat.getColor(context, R.color.white));
            }
        }
        holder.item_zjys_care.setOnClickListener(new AttentionClickListener());
        // 专家医生的头像
        if (!TextUtils.isEmpty(buddy.getImgUrl())) {
            ImageManager.loadImageByDefaultImage(BuildConfig.STATIC_PIC_PATH + buddy.getImgUrl(), null,
                    holder.avatar, R.drawable.social_new_avatar);
        } else {
            holder.avatar.setImageResource(R.drawable.social_new_avatar);
        }

        ImageManager.setVFlag(holder.vFlag, buddy);
        // 专家医生的名字
        holder.item_zjys_name.setText(buddy.getUserName() + "");
        // 专家医生的职称
        if (buddy.getTitle() == null || buddy.getTitle().equals("")) {
            holder.item_zjys_type.setText("未认证");
        } else {
            holder.item_zjys_type.setText(buddy.getTitle() + "");
        }
        // 专家医生的医院
        holder.item_zjys_illage.setText(buddy.getHospital() + "");

        return view;
    }

    private void getData(final String baseLine) {
        int requestCode = baseLine == null ? REFRESH_CODE : LOAD_MORE_CODE;
//        if(requestCode == REFRESH_CODE)
//            mListView.forbidLoad("", true);
        ApiManager.listExpDoctor(this, requestCode, context, baseLine, COUNT);
    }

    @Override
    public void onLoad() {
        if (datas.size() > 0)
            getData(baseLine);
    }

    @Override
    public void processResult(int requestCode, int resultCode, ListBuddyResult data) {
        if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
            if (data.getReturnCode().equals("0000")) {
                baseLine = data.getBaseLine();
                if (data.getBuddyList() != null && !data.getBuddyList().isEmpty()) {
                    if (requestCode == REFRESH_CODE) {
                        datas = new ArrayList<Buddy>();
                        mListView.resumeLoad();
                    }
                    datas.addAll(data.getBuddyList());
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

    @Override
    public void onRefresh() {
        getData(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (activityWR == null || activityWR.get() == null)
            return;
        Activity activity = activityWR.get();
        if (MainSelector.isNeedNewMain())
            return;
        Intent intent = new Intent(activity, NewPersonalSpaceActivity.class);
        Buddy buddy = datas.get(position);
        if (buddy != null) {
            intent.putExtra("otherBuddy", buddy);
        } else {
            Toast.makeText(context, "不好意思,发生未知的错误!欢迎向我们开发人员反馈.", Toast.LENGTH_SHORT).show();
        }
        activity.startActivity(intent);
    }

    public void followUser(String buddyId, int follow) {
        for (Buddy buddy : datas) {
            if (buddy.getBuddyId() != null && buddy.getBuddyId().equals(buddyId)) {
                buddy.setIdolFlag((byte) follow);
                notifyDataSetChanged();
                break;
            }
        }
    }

    class ViewHolder {
        public ImageView avatar, vFlag;
        public TextView item_zjys_name, item_zjys_type, item_zjys_illage;
        public TextView item_zjys_care;
        public int position;
    }

    class AttentionClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            final ViewHolder holder = (ViewHolder) v.getTag();
            final Buddy buddy = datas.get(holder.position);
            if (buddy.getIdolFlag() == 1) {
                if (activityWR == null || activityWR.get() == null)
                    return;
                Activity activity = activityWR.get();
                ConfirmTipsDialog dialog = new ConfirmTipsDialog(activity, "确认取消吗？", new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        LogUtil.addLog(context, "event-forum-cancel-concern");
                        ApiManager.followUser(new RequestManager.RequestListener<NetWorkResult>() {
                            @Override
                            public void processResult(int requestCode, int resultCode, NetWorkResult data) {
                                if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                                    EventBus.getDefault().post(new ChangeUserInfoEvent());
                                    EventBus.getDefault().post(new FollowUserEvent(buddy.getBuddyId(), 0));
                                    ViewHolder holder = (ViewHolder) v.getTag();
                                    if (datas != null && requestCode < datas.size() && holder != null) {
                                        datas.get(requestCode).setIdolFlag((byte) 0);
                                        if (holder.position == requestCode) {
                                            holder.item_zjys_care.setBackgroundResource(R.drawable.shape_care);
                                            holder.item_zjys_care.setTextColor(ContextCompat.getColor(context, R.color.white));
                                            holder.item_zjys_care.setText("关注");
                                        }
                                    }
                                }
                            }
                        }, holder.position, context, buddy.getBuddyId(), 0);
                    }
                });
                dialog.setTag(holder);
                dialog.show();

            } else {
                LogUtil.addLog(context, "event-forum-concern");
                ApiManager.followUser(new RequestManager.RequestListener<NetWorkResult>() {
                    @Override
                    public void processResult(int requestCode, int resultCode, NetWorkResult data) {
                        if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                            EventBus.getDefault().post(new ChangeUserInfoEvent());
                            EventBus.getDefault().post(new FollowUserEvent(buddy.getBuddyId(), 1));
                            if (datas != null && requestCode < datas.size()) {
                                datas.get(requestCode).setIdolFlag((byte) 1);
                                ViewHolder holder = (ViewHolder) v.getTag();
                                if (holder.position == requestCode) {
                                    holder.item_zjys_care.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                                    holder.item_zjys_care.setTextColor(ContextCompat.getColor(context, R.color.text_999999));
                                    holder.item_zjys_care.setText("已关注");
                                }
                            }
                        }
                    }
                }, holder.position, context, buddy.getBuddyId(), 1);
            }
        }
    }

}
