package com.jkyssocial.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jkys.tools.DeviceUtil;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.CommonInfoManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.Circle;
import com.jkyssocial.data.ListCircleForUserResult;
import com.jkyssocial.widget.DragSortListView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mintcode.base.BaseActivity;
import com.mintcode.util.ImageManager;
import com.mintcode.util.LogUtil;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamplus.wentang.BuildConfig;
import cn.dreamplus.wentang.R;
import mehdi.sakout.fancybuttons.FancyButton;

public class MyEnterCircleOldActivity extends BaseActivity implements View.OnClickListener,
        DragSortListView.DropListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private DragSortListView dragSortListView;
    private MyEnterCircleAdapter adapter;
    private List<Circle> circles;
    private TextView empty;
    private SwipeRefreshLayout swipeRefreshLayout;
    Buddy buddy;

    @Bind(R.id.title_toolbar)
    TextView toolbarTitle;
    @Bind(R.id.right_rl)
    View rightRL;

    int type; // 0 : 我的； 1： 别人的

    static class ListCircleForUserV2RequestListener implements RequestManager.RequestListener<ListCircleForUserResult> {
        private WeakReference<MyEnterCircleOldActivity> activityWR;

        public ListCircleForUserV2RequestListener(MyEnterCircleOldActivity activity) {
            activityWR = new WeakReference<MyEnterCircleOldActivity>(activity);
        }


        @Override
        public void processResult(int requestCode, int resultCode, ListCircleForUserResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            MyEnterCircleOldActivity activity = activityWR.get();
            activity.swipeRefreshLayout.setRefreshing(false);
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                if (data.getReturnCode().equals("0000")) {
                    if (data.getCircleList() != null && !data.getCircleList().isEmpty()) {
                        if (requestCode == activity.requestCode) {
                            // 清空本地tab栏的缓存数据
                            activity.circles.clear();
                            // 网络端返回,更新本地的tab栏数据
                            activity.circles.addAll(data.getCircleList());
                            if (activity.toolbarTitle != null) {
                                if (activity.type == 1)
                                    activity.toolbarTitle.setText("他加入的圈子(" + activity.buddy.getCircleCount() + ")");
                                else if (activity.type == 0)
                                    activity.toolbarTitle.setText("我加入的圈子(" + activity.buddy.getCircleCount() + ")");
                            }
                            activity.adapter.notifyDataSetChanged();
                            if (activity.circles.isEmpty())
                                activity.empty.setVisibility(View.VISIBLE);
                            else activity.empty.setVisibility(View.GONE);
                        }
                    }
                } else {
                    Toast.makeText(activity.getApplicationContext(), "亲,检查下有没有网络连接啊!", Toast.LENGTH_LONG).show();
                }

            } else if (resultCode == 9960) {
                ApiManager.listCircleForUserV2(this, requestCode, activity.getApplicationContext(),
                        activity.buddy.getBuddyId());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_enter_circle_old);
        ButterKnife.bind(this);
        initView();
        buddy = (Buddy) getIntent().getSerializableExtra("myBuddy");
        if (buddy != null) {
            type = 0;
            toolbarTitle.setText("我加入的圈子(" + buddy.getCircleCount() + ")");
        } else {
            buddy = (Buddy) getIntent().getSerializableExtra("otherBuddy");
            if (buddy == null) {
                finish();
                return;
            }
            type = 1;
            rightRL.setVisibility(View.INVISIBLE);
            toolbarTitle.setText("他加入的圈子(" + buddy.getCircleCount() + ")");
        }
        initData();
        initEvent();
        LogUtil.addLog(this, "page-forum-mycircle");
    }

    private void initEvent() {
        dragSortListView.setOnItemClickListener(this);
    }

    // 请求码
    private final int requestCode = 1;

    private void initData() {
        circles = new LinkedList<>();
        adapter = new MyEnterCircleAdapter(circles, this);
        dragSortListView.setAdapter(adapter);
        // 设置是否可以拖动
        dragSortListView.setDragEnabled(false);
        // 设置拖动监听
//        dragSortListView.setDropListener(this);

    }


    private void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.old_swipeRefreshLayout);
        // dragSortListView
        dragSortListView = (DragSortListView) findViewById(R.id.activity_my_enter_circle_listView);
        empty = (TextView) findViewById(R.id.empty);

        swipeRefreshLayout.setColorSchemeResources(R.color.social_primary, R.color.social_primary, R.color.social_primary, R.color.social_primary);
        swipeRefreshLayout.setProgressViewOffset(true, 0, 50 * DeviceUtil.getDensity());
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(true);
    }

    private int circleNum = 0;

    @Override
    protected void onResume() {
        super.onResume();
        onReflushData();
    }

    private void onReflushData() {
        if (buddy != null) {
            if (buddy.getCircleCount() == 0) empty.setVisibility(View.VISIBLE);
            else empty.setVisibility(View.GONE);
        }
        swipeRefreshLayout.setRefreshing(true);
        ApiManager.listCircleForUserV2(new ListCircleForUserV2RequestListener(this), requestCode,
                getApplicationContext(), buddy.getBuddyId());
        getUserInfoCircleNum();
    }

    @OnClick(R.id.left_rl)
    void onBackClick(View view) {
        finish();
    }

    @OnClick(R.id.right_rl)
    void onRightRLClick(View view) {
        Intent intent = new Intent(this, BuildCircleActivity.class);
        if (circleNum < 3) {
            // 用户已经建立小于2个的时候不需要提示就跳转到新建界面
            startActivity(intent);
        } else if (circleNum >= 3) {
            Toast.makeText(this, "最多只能建3个圈子", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
    }

    // 获取用户建立的圈子个数:
    private void getUserInfoCircleNum() {
        Integer hasCircles = CommonInfoManager.getInstance().getUserInfo(this).getHasCircles();
        if (hasCircles != null) {
            circleNum = hasCircles;
        } else {
            circleNum = 0;
        }
    }

    @Override
    public void drop(int from, int to) {
        if (from != to) {
            rightRL.setVisibility(View.VISIBLE);
            Circle circle = circles.get(from);
            circles.remove(from);
            circles.add(to, circle);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO 跳转到圈子详情界面 肯定有Id 的传入
        Intent intent = new Intent(this, CircleMainActivity.class);
        intent.putExtra("circle", circles.get(position));
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        onReflushData();
    }

    class MyEnterCircleAdapter extends BaseAdapter {
        private List<Circle> circles;
        private Context context;

        public MyEnterCircleAdapter(List<Circle> circles, Context context) {
            this.circles = circles;
            this.context = context;
        }

        @Override
        public int getCount() {
            return circles.size();
        }

        @Override
        public Object getItem(int position) {
            return circles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView != null) {
                view = convertView;
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.item_allcircle_type, parent, false);
            }

            // DragHolder
            DragHolder holder = (DragHolder) view.getTag();
            if (holder == null) {
                holder = new DragHolder();
                holder.item_allcircle_img = (RoundedImageView) view.findViewById(R.id.item_allcircle_img);
                holder.item_allcircle_member = (TextView) view.findViewById(R.id.item_allcircle_member);
                holder.item_allcircle_name = (TextView) view.findViewById(R.id.item_allcircle_name);
                holder.item_allcircle_owner = (FancyButton) view.findViewById(R.id.item_allcircle_owner);
                holder.item_allcircle_decribute = (TextView) view.findViewById(R.id.item_allcircle_decribute);
                holder.item_allcircle_enter = (TextView) view.findViewById(R.id.item_allcircle_enter);
                view.setTag(holder);
            }
            holder.item_allcircle_enter.setVisibility(View.GONE);
            // 数据填充
            Circle circle = circles.get(position);
            // 圈子描述:
            holder.item_allcircle_decribute.setText(circle.getSummary() + "");
            // 圈子名字 title
            holder.item_allcircle_name.setText(circle.getTitle() + "");
            boolean zijian = type == 0 && circle.getOwnerId() != null && circle.getOwnerId().equals(buddy.getBuddyId());
            holder.item_allcircle_owner.setVisibility(zijian ? View.VISIBLE : View.GONE);
            // 圈子成员数量
            if (circle.getStat() != null)
                holder.item_allcircle_member.setText("成员: " + circle.getStat().getMemberCount());
            // 圈子的头像
            if (!TextUtils.isEmpty(circle.getAvatar())) {
                ImageManager.loadImageByDefaultImage(BuildConfig.STATIC_PIC_PATH + circle.getAvatar(),
                        context, holder.item_allcircle_img, R.drawable.social_circle_avatar);
            } else {
                holder.item_allcircle_img.setImageResource(R.drawable.social_circle_avatar);
            }

            return view;
        }

        public void followCircle(String circleId, int follow) {

        }

        private class DragHolder {
            private RoundedImageView item_allcircle_img;
            private TextView item_allcircle_name, item_allcircle_decribute, item_allcircle_member;
            private TextView item_allcircle_enter;
            private FancyButton item_allcircle_owner;
        }

    }

//    public void onEventMainThread(FollowCircleEvent event) {
//        if (type == 0) {
//            if (event.follow == 0) {
//                buddy.setCircleCount(buddy.getCircleCount() - 1);
//            } else if (event.follow == 1) {
//                buddy.setCircleCount(buddy.getCircleCount() + 1);
//            }
//            if (activity_my_enter_circle_title != null)
//                activity_my_enter_circle_title.setText("我加入的圈子(" + buddy.getCircleCount() + ")");
//            ApiManager.listCircleForUserV2(this, requestCode, this, buddy.getBuddyId());
//        }
//    }
}
