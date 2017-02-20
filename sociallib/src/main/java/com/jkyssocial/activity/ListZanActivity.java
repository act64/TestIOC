package com.jkyssocial.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.jkys.jkyswidget.MyListView;
import com.jkys.tools.MainSelector;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.ListBuddyResult;
import com.mintcode.base.BaseActivity;
import com.mintcode.util.ImageManager;
import com.mintcode.util.LogUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamplus.wentang.BuildConfig;
import cn.dreamplus.wentang.R;


/**
 * 点赞列表页
 *
 * @author yangxiaolong
 */
public class ListZanActivity extends BaseActivity {

    View emptyView;

    MyListView mListView;

    ListZanAdapter mAdapter;

    SwipeRefreshLayout swipeRefreshLayout;

    String dynamicId;

    @OnClick(R.id.left_rl)
    void onBackClick(View view) {
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_activity_list_exp_patient);
        ButterKnife.bind(this);
        dynamicId = getIntent().getStringExtra("dynamicId");
        TextView toolbarTitle = (TextView) findViewById(R.id.title_toolbar);
        toolbarTitle.setText("点赞(" + getIntent().getIntExtra("zanCount", 0) + ")");

        // Set the adapter
        mListView = (MyListView) findViewById(R.id.listView);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        emptyView = findViewById(R.id.empty);
        mAdapter = new ListZanAdapter();
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
        LogUtil.addLog(this, "page-forum-praise-list");


    }

    static class ListLikerRequestListener implements RequestManager.RequestListener<ListBuddyResult> {
        private WeakReference<ListZanActivity> activityWR;
        private ListZanAdapter mAdapter;

        public ListLikerRequestListener(ListZanActivity activity,
                                        ListZanAdapter adapter) {
            activityWR = new WeakReference<ListZanActivity>(activity);
            mAdapter = adapter;

        }


        @Override
        public void processResult(int requestCode, int resultCode, ListBuddyResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            final ListZanActivity activity = activityWR.get();
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                if (data.getReturnCode().equals("0000")) {
                    if (data.getBuddyList() != null && !data.getBuddyList().isEmpty()) {
                        if (data.getBuddyList() != null && !data.getBuddyList().isEmpty()) {
                            if (requestCode == mAdapter.REFRESH_CODE) {
                                mAdapter.setList(new ArrayList<Buddy>());
                                activity.mListView.resumeLoad();
                            }

                            mAdapter.getList().addAll(data.getBuddyList());
                            activity.emptyView.setVisibility(View.GONE);
                            mAdapter.notifyDataSetChanged();
                            if (data.getBuddyList().size() < mAdapter.COUNT) {
                                activity.mListView.forbidLoad("已经全部加载完毕", true);
                            }
                        } else {
                            if (requestCode == mAdapter.REFRESH_CODE) {
                                mAdapter.setList(new ArrayList<Buddy>());
                                activity.emptyView.setVisibility(View.VISIBLE);
                                mAdapter.notifyDataSetChanged();
                            } else
                                activity.mListView.forbidLoad("已经全部加载完毕", true);
                        }
                    }
                }
            } else {
                //TODO
            }
            activity.swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    activity.swipeRefreshLayout.setRefreshing(false);
                }
            });
            activity.mListView.endLoad();
        }
    }

    class ListZanAdapter extends BaseAdapter implements OnRefreshListener, MyListView.OnLoadListener,
            OnItemClickListener, View.OnClickListener {

        private static final int COUNT = 20;

        private int REFRESH_CODE = 1;

        private int LOAD_MORE_CODE = 2;

        List<Buddy> list;

        public ListZanAdapter() {
            list = new ArrayList<Buddy>();
            getData(null);
        }

        private void getData(final Long baseTime) {
            int requestCode = baseTime == null ? REFRESH_CODE : LOAD_MORE_CODE;
            if (requestCode == REFRESH_CODE)
                mListView.forbidLoad("", true);
            ApiManager.listLiker(new ListLikerRequestListener(ListZanActivity.this, this),
                    requestCode, ListZanActivity.this, dynamicId, baseTime, COUNT);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        public List<Buddy> getList() {
            return list;
        }

        public void setList(List<Buddy> list) {
            this.list = list;
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
                        R.layout.social_item_list_zan_buddy, parent, false);
                holder = new ViewHolder();
                holder.avatar = (ImageView) convertView
                        .findViewById(R.id.avatar);
                holder.vFlag = (ImageView) convertView
                        .findViewById(R.id.vFlag);
                holder.nickname = (TextView) convertView
                        .findViewById(R.id.nickname);
                convertView.setOnClickListener(this);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.position = position;
            if (!TextUtils.isEmpty(buddy.getImgUrl())) {
                ImageManager.loadImageByDefaultImage(BuildConfig.STATIC_PIC_PATH + buddy.getImgUrl(),
                        null, holder.avatar, R.drawable.social_new_avatar);
            } else {
                holder.avatar.setImageResource(R.drawable.social_new_avatar);
            }

            ImageManager.setVFlag(holder.vFlag, buddy);
            holder.nickname.setText(buddy.getUserName());
            return convertView;
        }

        @Override
        public void onClick(View v) {
            if (MainSelector.isNeedNewMain())
                return;
            ViewHolder holder = (ViewHolder) v.getTag();
            Intent intent = new Intent(ListZanActivity.this, NewPersonalSpaceActivity.class);
            intent.putExtra("otherBuddy", list.get(holder.position));
            startActivity(intent);
        }

        class ViewHolder {
            ImageView avatar;
            ImageView vFlag;
            TextView nickname;
            int position;
        }

        @Override
        public void onRefresh() {
            getData(null);
        }

        @Override
        public void onLoad() {
            if (list.size() > 0) {
                int index = list.size() - 1;
                getData(list.get(index).getLikeTime());
            }
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
//            Buddy buddy = list.get(position);
//            Intent intent = new Intent(getActivity(), BuddyDetailNewActivity.class);
//            intent.putExtra("buddy",buddy);
//            startActivity(intent);
        }

    }

}
