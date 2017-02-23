package com.jkyssocial.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkys.jkysbase.data.NetWorkResult;
import com.jkys.jkyswidget.ConfirmTipsDialog;
import com.jkys.jkyswidget.MyListView;
import com.jkys.proxy.AppImpl;
import com.jkys.sociallib.R;
import com.jkys.sociallib.R2;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.CommonInfoManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.ListBuddyResult;
import com.jkyssocial.event.ChangeUserInfoEvent;
import com.jkyssocial.event.FollowUserEvent;
import com.mintcode.base.BaseActivity;
import com.mintcode.util.ImageManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import mehdi.sakout.fancybuttons.FancyButton;


/**
 * 被关注的列表页
 *
 * @author yangxiaolong
 */
public class ListBeAttentionActivity extends BaseActivity {

    TextView emptyView;

    MyListView mListView;

    ListAttentionAdapter mAdapter;

    SwipeRefreshLayout swipeRefreshLayout;

    Buddy buddy;

    Buddy myBuddy;

    int type;


    /**
     * 取消关注
     */
    static class CancelFollowRequestListener implements RequestManager.RequestListener<NetWorkResult> {
        private WeakReference<ListBeAttentionActivity> activityWR;
        private ListAttentionAdapter.ViewHolder holder;
        private List<Buddy> list;

        public CancelFollowRequestListener(ListBeAttentionActivity activity,
                                           ListAttentionAdapter.ViewHolder holder,
                                           List<Buddy> list) {
            activityWR = new WeakReference<ListBeAttentionActivity>(activity);
            this.holder = holder;
            this.list = list;
        }

        @Override
        public void processResult(int requestCode, int resultCode, NetWorkResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            ListBeAttentionActivity activity = activityWR.get();

            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                EventBus.getDefault().post(new ChangeUserInfoEvent());
                EventBus.getDefault().post(new FollowUserEvent(activity.buddy.getBuddyId(), 0));
                if (list != null && requestCode < list.size() && holder != null) {
                    list.get(requestCode).setIdolFlag((byte) 0);
                    if (holder.position == requestCode) {
                        holder.attentionBtn.setBackgroundColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.social_primary));
                        holder.attentionBtn.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.white));
                        holder.attentionBtn.setText("关注");
//                                                holder.attentionBtn.setImageResource(R.drawable.social_attention_selected);
                    }
                }
            }
        }
    }

    /**
     * 关注
     */
    static class FollowRequestListener implements RequestManager.RequestListener<NetWorkResult> {
        private WeakReference<ListBeAttentionActivity> activityWR;
        private ListAttentionAdapter.ViewHolder holder;
        private List<Buddy> list;

        public FollowRequestListener(ListBeAttentionActivity activity,
                                     ListAttentionAdapter.ViewHolder holder,
                                     List<Buddy> list) {
            activityWR = new WeakReference<ListBeAttentionActivity>(activity);
            this.holder = holder;
            this.list = list;
        }

        @Override
        public void processResult(int requestCode, int resultCode, NetWorkResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            ListBeAttentionActivity activity = activityWR.get();

            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                EventBus.getDefault().post(new ChangeUserInfoEvent());
                EventBus.getDefault().post(new FollowUserEvent(activity.buddy.getBuddyId(), 1));
                if (list != null && requestCode < list.size()) {
                    list.get(requestCode).setIdolFlag((byte) 1);
                    if (holder.position == requestCode) {
                        holder.attentionBtn.setBackgroundColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.white));
                        holder.attentionBtn.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.text_999999));
                        holder.attentionBtn.setText("已关注");
                    }
                }
            }

        }
    }

    /**
     * 获取成员列表
     */
    static class ListBuddyRequestListener implements RequestManager.RequestListener<ListBuddyResult> {
        private WeakReference<ListBeAttentionActivity> activityWR;
        private List<Buddy> list;
        private ListAttentionAdapter mAdapter;

        public ListBuddyRequestListener(ListBeAttentionActivity activity,
                                        List<Buddy> list,
                                        ListAttentionAdapter adapter) {
            activityWR = new WeakReference<ListBeAttentionActivity>(activity);
            this.list = list;
            mAdapter = adapter;

        }


        @Override
        public void processResult(int requestCode, int resultCode, ListBuddyResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            final ListBeAttentionActivity activity = activityWR.get();
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                if (data.getReturnCode().equals("0000")) {
                    if (data.getBuddyList() != null && !data.getBuddyList().isEmpty()) {
                        if (requestCode == mAdapter.REFRESH_CODE) {
                            list = new ArrayList<Buddy>();
                            activity.mListView.resumeLoad();
                            mAdapter.getList().clear();
                        }
                        mAdapter.getList().addAll(data.getBuddyList());
                        mAdapter.notifyDataSetChanged();
                    } else {
                        if (requestCode == mAdapter.REFRESH_CODE) {
                            activity.emptyView.setVisibility(View.VISIBLE);
                        } else
                            activity.mListView.forbidLoad("已经全部加载完毕", true);
                    }
                }
            } else {
                //TODO
            }
            mAdapter.isLoadingData = false;
            activity.swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    activity.swipeRefreshLayout.setRefreshing(false);
                }
            });
            activity.mListView.endLoad();
        }
    }

    @OnClick(R2.id.left_rl)
    void onBackClick(View view) {
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_activity_list_exp_patient);
        ButterKnife.bind(this);
        TextView toolbarTitle = (TextView) findViewById(R.id.title_toolbar);
        myBuddy = CommonInfoManager.getInstance().getUserInfo(this);
        buddy = (Buddy) getIntent().getSerializableExtra("myBuddy");
        if (buddy != null) {
            type = 0;
            toolbarTitle.setText("关注我的人(" + buddy.getFansCount() + ")");
            EventBus.getDefault().post(new ChangeUserInfoEvent());
        } else {
            type = 1;
            buddy = (Buddy) getIntent().getSerializableExtra("otherBuddy");
            toolbarTitle.setText("关注他的人(" + buddy.getFansCount() + ")");
        }
        // Set the adapter
        mListView = (MyListView) findViewById(R.id.listView);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        emptyView = (TextView) findViewById(R.id.empty);
        emptyView.setText("还没有人关注你哦");
        mAdapter = new ListAttentionAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mAdapter);
        mListView.setOnLoadListener(mAdapter);
        swipeRefreshLayout.setOnRefreshListener(mAdapter);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        AppImpl.getAppRroxy().addLog(this, "page-forum-concerned-list");


    }

    class ListAttentionAdapter extends BaseAdapter implements OnRefreshListener, MyListView.OnLoadListener, OnItemClickListener, View.OnClickListener {

        private static final int COUNT = 20;

        /**
         * 是否正在加载
         */
        private boolean isLoadingData = false;

        private int REFRESH_CODE = 1;

        private int LOAD_MORE_CODE = 2;

        List<Buddy> list;

        public List<Buddy> getList() {
            return list;
        }

        public ListAttentionAdapter() {
            list = new ArrayList<Buddy>();
            getData(null);
        }

        private void getData(final Long baseTime) {
            int requestCode = baseTime == null ? REFRESH_CODE : LOAD_MORE_CODE;
            if (requestCode == REFRESH_CODE)
                mListView.forbidLoad("", true);
            ApiManager.listFans(new ListBuddyRequestListener(ListBeAttentionActivity.this, list, this), requestCode, context, buddy.getBuddyId(), baseTime, COUNT);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Buddy buddy = list.get(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(
                        R.layout.social_item_list_buddy, parent, false);
                holder = new ViewHolder();
                holder.avatar = (ImageView) convertView
                        .findViewById(R.id.avatar);
                holder.vFlag = (ImageView) convertView
                        .findViewById(R.id.vFlag);
                holder.nickname = (TextView) convertView
                        .findViewById(R.id.nickname);
                holder.signature = (TextView) convertView
                        .findViewById(R.id.signature);
                holder.attentionBtn = (FancyButton) convertView
                        .findViewById(R.id.attentionBtn);
                convertView.setOnClickListener(this);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.position = position;
            if (!TextUtils.isEmpty(buddy.getImgUrl())) {
                ImageManager.loadImageByDefaultImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + buddy.getImgUrl(),
                        null, holder.avatar, R.drawable.social_new_avatar);
            } else {
                holder.avatar.setImageResource(R.drawable.social_new_avatar);
            }

            ImageManager.setVFlag(holder.vFlag, buddy);
            holder.nickname.setText(buddy.getUserName());
            holder.signature.setText(buddy.getSignature());
            if (myBuddy != null && myBuddy.getBuddyId().equals(buddy.getBuddyId())) {
                holder.attentionBtn.setVisibility(View.GONE);
            } else {
                holder.attentionBtn.setVisibility(View.VISIBLE);
                if (buddy.getIdolFlag() == 0) {
                    holder.attentionBtn.setBackgroundColor(ContextCompat.getColor(ListBeAttentionActivity.this, R.color.social_primary));
                    holder.attentionBtn.setTextColor(ContextCompat.getColor(ListBeAttentionActivity.this, R.color.white));
                    holder.attentionBtn.setText("关注");
                } else {
                    holder.attentionBtn.setBackgroundColor(ContextCompat.getColor(ListBeAttentionActivity.this, R.color.white));
                    holder.attentionBtn.setTextColor(ContextCompat.getColor(ListBeAttentionActivity.this, R.color.text_999999));
                    holder.attentionBtn.setText("已关注");
                }
            }
//            holder.attentionBtn.setImageResource(buddy.getIdolFlag() == 0 ? R.drawable.social_attention_selected : R.drawable.social_attention);
            holder.attentionBtn.setTag(holder);
            holder.attentionBtn.setOnClickListener(new AttentionClickListener());
            return convertView;
        }

        @Override
        public void onClick(View v) {
            if (AppImpl.getAppRroxy().isNeedNewMain() )
                return;
            ViewHolder holder = (ViewHolder) v.getTag();
            Intent intent = new Intent(ListBeAttentionActivity.this, NewPersonalSpaceActivity.class);
            intent.putExtra("otherBuddy", list.get(holder.position));
            startActivity(intent);
        }

//        @Override
//        public void processResult(int requestCode, int resultCode, ListBuddyResult data) {
//            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
//                if (data.getReturnCode().equals("0000")) {
//                    if (data.getBuddyList() != null && !data.getBuddyList().isEmpty()) {
//                        if (requestCode == REFRESH_CODE) {
//                            list = new ArrayList<Buddy>();
//                            mListView.resumeLoad();
//                        }
////                        if(data.getBuddyList().size() < COUNT)
////                            mListView.forbidLoad("已经全部加载完毕", true);
//                        list.addAll(data.getBuddyList());
//                        emptyView.setVisibility(View.GONE);
//                        notifyDataSetChanged();
//                    } else {
//                        if (requestCode == REFRESH_CODE) {
//                            list = new ArrayList<Buddy>();
//                            emptyView.setVisibility(View.VISIBLE);
//                            notifyDataSetChanged();
//                        } else
//                            mListView.forbidLoad("已经全部加载完毕", true);
//                    }
//                }
//            } else {
//                //TODO
//            }
//            isLoadingData = false;
//            swipeRefreshLayout.setRefreshing(false);
//            mListView.endLoad();
//        }

        class ViewHolder {
            ImageView avatar;
            ImageView vFlag;
            TextView nickname;
            TextView signature;
            FancyButton attentionBtn;
            int position;
        }

        @Override
        public void onRefresh() {
            getData(null);
        }

        @Override
        public void onLoad() {
            if (list.size() > 0)
                getData(list.get(list.size() - 1).getFollowTime());
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
        }

        class AttentionClickListener implements View.OnClickListener {

            @Override
            public void onClick(final View v) {
                final ViewHolder holder = (ViewHolder) v.getTag();
                final Buddy buddy = list.get(holder.position);
                if (buddy.getIdolFlag() == 1) {
                    ConfirmTipsDialog dialog = new ConfirmTipsDialog(ListBeAttentionActivity.this, "确认取消吗？", new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            AppImpl.getAppRroxy().addLog(ListBeAttentionActivity.this, "event-forum-cancel-concern");
                            ApiManager.followUser(new CancelFollowRequestListener(ListBeAttentionActivity.this, holder, list), holder.position, context, buddy.getBuddyId(), 0);

//                            ApiManager.followUser(new RequestManager.RequestListener<NetWorkResult>() {
//                                @Override
//                                public void processResult(int requestCode, int resultCode, NetWorkResult data) {
//                                    if(resultCode == RequestManager.RESULT_SUCCESS_CODE) {
//                                        EventBus.getDefault().post(new ChangeUserInfoEvent());
//                                        EventBus.getDefault().post(new FollowUserEvent(buddy.getBuddyId(), 0));
//                                        ViewHolder holder = (ViewHolder) v.getTag();
//                                        if (list != null && requestCode < list.size() && holder != null) {
//                                            list.get(requestCode).setIdolFlag((byte) 0);
//                                            if (holder.position == requestCode){
//                                                holder.attentionBtn.setBackgroundColor(ContextCompat.getColor(ListBeAttentionActivity.this, R.color.social_primary));
//                                                holder.attentionBtn.setTextColor(ContextCompat.getColor(ListBeAttentionActivity.this, R.color.white));
//                                                holder.attentionBtn.setText("关注");
//                                            }
////                                                holder.attentionBtn.setImageResource(R.drawable.social_attention_selected);
//                                        }
//                                    }
//                                }
//                            }, holder.position, ListBeAttentionActivity.this, buddy.getBuddyId(), 0);
                        }
                    });
                    dialog.setTag(holder);
                    dialog.show();

                } else {
                    ApiManager.followUser(new FollowRequestListener(ListBeAttentionActivity.this, holder, list), holder.position, context, buddy.getBuddyId(), 1);
//                    ApiManager.followUser(new RequestManager.RequestListener<NetWorkResult>() {
//                        @Override
//                        public void processResult(int requestCode, int resultCode, NetWorkResult data) {
//                            AppImpl.getAppRroxy().addLog(ListBeAttentionActivity.this, "event-forum-concern");
//                            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
//                                EventBus.getDefault().post(new ChangeUserInfoEvent());
//                                EventBus.getDefault().post(new FollowUserEvent(buddy.getBuddyId(), 1));
//                                if (list != null && requestCode < list.size()) {
//                                    list.get(requestCode).setIdolFlag((byte) 1);
//                                    ViewHolder holder = (ViewHolder) v.getTag();
//                                    if (holder.position == requestCode) {
//                                        holder.attentionBtn.setBackgroundColor(ContextCompat.getColor(ListBeAttentionActivity.this, R.color.white));
//                                        holder.attentionBtn.setTextColor(ContextCompat.getColor(ListBeAttentionActivity.this, R.color.text_999999));
//                                        holder.attentionBtn.setText("已关注");
//                                    }
////                                    if (holder.position == requestCode)
////                                        ((ImageView) v).setImageResource(R.drawable.social_attention);
//                                }
//                            }
//                        }
//                    }, holder.position, ListBeAttentionActivity.this, buddy.getBuddyId(), 1);
                }
            }
        }

    }

}
