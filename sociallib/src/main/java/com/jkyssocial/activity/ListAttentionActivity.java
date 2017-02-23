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
 * 关注的列表页
 *
 * @author yangxiaolong
 */
public class ListAttentionActivity extends BaseActivity {

    TextView emptyView;

    MyListView mListView;

    ListAttentionAdapter mAdapter;

    SwipeRefreshLayout swipeRefreshLayout;

    Buddy buddy;

    Buddy myBuddy;

    TextView toolbarTitle;

    int type;

    /**
     * 取消关注
     */
    static class CancelFollowRequestListener implements RequestManager.RequestListener<NetWorkResult> {
        private WeakReference<ListAttentionActivity> activityWR;
        private ListAttentionAdapter.ViewHolder holder;
        private ListAttentionAdapter adapter;
        private List<Buddy> list;

        public CancelFollowRequestListener(ListAttentionActivity activity,
                                           ListAttentionAdapter.ViewHolder holder,
                                           List<Buddy> list, ListAttentionAdapter adapter) {
            activityWR = new WeakReference<ListAttentionActivity>(activity);
            this.adapter = adapter;
            this.holder = holder;
            this.list = list;
        }

        @Override
        public void processResult(int requestCode, int resultCode, NetWorkResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            ListAttentionActivity activity = activityWR.get();

            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                EventBus.getDefault().post(new ChangeUserInfoEvent());
                EventBus.getDefault().post(new FollowUserEvent(activity.buddy.getBuddyId(), 0));
                if (activity.type == 1) {
                    if (list != null && requestCode < list.size() && holder != null) {
                        list.get(requestCode).setIdolFlag((byte) 0);
                        if (holder.position == requestCode) {
                            holder.attentionBtn.setBackgroundColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.social_primary));
                            holder.attentionBtn.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.white));
                            holder.attentionBtn.setText("关注");
//                                                holder.attentionBtn.setImageResource(R.drawable.social_attention_selected);
                        }
                    }
                } else if (activity.type == 0) {
                    //取消"我关注的人",刷新"我关注的人"列表
                    adapter.onRefresh();
                }
            }
        }
    }

    /**
     * 关注
     */
    static class FollowRequestListener implements RequestManager.RequestListener<NetWorkResult> {
        private WeakReference<ListAttentionActivity> activityWR;
        private ListAttentionAdapter.ViewHolder holder;
        private List<Buddy> list;

        public FollowRequestListener(ListAttentionActivity activity,
                                     ListAttentionAdapter.ViewHolder holder,
                                     List<Buddy> list) {
            activityWR = new WeakReference<ListAttentionActivity>(activity);
            this.holder = holder;
            this.list = list;
        }

        @Override
        public void processResult(int requestCode, int resultCode, NetWorkResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            ListAttentionActivity activity = activityWR.get();

            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                EventBus.getDefault().post(new ChangeUserInfoEvent());
                EventBus.getDefault().post(new FollowUserEvent(activity.buddy.getBuddyId(), 1));
                if (activity.type == 1) {
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
    }

    /**
     * 获取成员列表
     */
    static class ListBuddyRequestListener implements RequestManager.RequestListener<ListBuddyResult> {
        private WeakReference<ListAttentionActivity> activityWR;
        private List<Buddy> list;
        private ListAttentionAdapter mAdapter;

        public ListBuddyRequestListener(ListAttentionActivity activity,
                                        List<Buddy> list,
                                        ListAttentionAdapter adapter) {
            activityWR = new WeakReference<ListAttentionActivity>(activity);
            this.list = list;
            mAdapter = adapter;

        }


        @Override
        public void processResult(int requestCode, int resultCode, ListBuddyResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            final ListAttentionActivity activity = activityWR.get();
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                if (data.getReturnCode().equals("0000")) {
                    if (data.getBuddyList() != null && !data.getBuddyList().isEmpty()) {
                        if (requestCode == mAdapter.REFRESH_CODE) {
                            list = new ArrayList<Buddy>();
                            mAdapter.getList().clear();
                            activity.mListView.resumeLoad();
                        }
//                        if(data.getBuddyList().size() < COUNT)
//                            mListView.forbidLoad("已经全部加载完毕", true);
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
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_activity_list_exp_patient);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        toolbarTitle = (TextView) findViewById(R.id.title_toolbar);
        myBuddy = CommonInfoManager.getInstance().getUserInfo(this);
        buddy = (Buddy) getIntent().getSerializableExtra("myBuddy");
        if (buddy != null) {
            type = 0;
            toolbarTitle.setText("我关注的人(" + buddy.getIdolCount() + ")");
        } else {
            type = 1;
            buddy = (Buddy) getIntent().getSerializableExtra("otherBuddy");
            toolbarTitle.setText("他关注的人(" + buddy.getIdolCount() + ")");
        }

        // Set the adapter
        mListView = (MyListView) findViewById(R.id.listView);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        emptyView = (TextView) findViewById(R.id.empty);
        emptyView.setText("还没有关注的人哦");
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
        AppImpl.getAppRroxy().addLog(this, "page-forum-concern-list");


    }

    class ListAttentionAdapter extends BaseAdapter implements OnRefreshListener,
            MyListView.OnLoadListener, OnItemClickListener, View.OnClickListener {

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
            ApiManager.listIdol(new ListBuddyRequestListener(ListAttentionActivity.this, list, this),
                    requestCode, context, buddy.getBuddyId(), baseTime, COUNT);
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
                holder.vFlag = (ImageView) convertView.findViewById(R.id.vFlag);
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
                    holder.attentionBtn.setBackgroundColor(ContextCompat.getColor(ListAttentionActivity.this, R.color.social_primary));
                    holder.attentionBtn.setTextColor(ContextCompat.getColor(ListAttentionActivity.this, R.color.white));
                    holder.attentionBtn.setText("关注");
                } else {
                    holder.attentionBtn.setBackgroundColor(ContextCompat.getColor(ListAttentionActivity.this, R.color.white));
                    holder.attentionBtn.setTextColor(ContextCompat.getColor(ListAttentionActivity.this, R.color.text_999999));
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
            Intent intent = new Intent(ListAttentionActivity.this, NewPersonalSpaceActivity.class);
            intent.putExtra("otherBuddy", list.get(holder.position));
            startActivity(intent);
        }

        public void delete(String buddyId) {
            for (Buddy buddy : list) {
                if (buddy.getBuddyId() != null && buddy.getBuddyId().equals(buddyId)) {
                    list.remove(buddy);
                    notifyDataSetChanged();
                    break;
                }
            }
        }

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
                    ConfirmTipsDialog dialog = new ConfirmTipsDialog(ListAttentionActivity.this, "确认取消吗？", new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            AppImpl.getAppRroxy().addLog(ListAttentionActivity.this, "event-forum-cancel-concern");
                            ApiManager.followUser(new CancelFollowRequestListener(
                                            ListAttentionActivity.this, holder,
                                            list, ListAttentionAdapter.this), holder.position,
                                    context, buddy.getBuddyId(), 0);
                        }
                    });
                    dialog.setTag(holder);
                    dialog.show();

                } else {
                    AppImpl.getAppRroxy().addLog(ListAttentionActivity.this, "event-forum-concern");
                    ApiManager.followUser(new FollowRequestListener(ListAttentionActivity.this,
                                    holder, list), holder.position, context,
                            buddy.getBuddyId(), 1);
                }
            }
        }

    }

    public void onEventMainThread(FollowUserEvent event) {
        if (toolbarTitle != null && mAdapter != null) {
            if (type == 0 && event.follow == 0) {
                buddy.setIdolCount(buddy.getIdolCount() - 1);
                toolbarTitle.setText("我关注的人(" + buddy.getIdolCount() + ")");
                mAdapter.delete(event.buddyId);
            } else if (type == 0 && event.follow == 1) {
                buddy.setIdolCount(buddy.getIdolCount() + 1);
                toolbarTitle.setText("我关注的人(" + buddy.getIdolCount() + ")");
                mAdapter.onRefresh();
            }
        }
    }

}
