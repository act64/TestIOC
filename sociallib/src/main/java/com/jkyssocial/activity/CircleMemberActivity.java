package com.jkyssocial.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamplus.wentang.BuildConfig;
import cn.dreamplus.wentang.R;
import de.hdodenhof.circleimageview.CircleImageView;

import com.jkys.jkyswidget.MyListView;
import com.jkys.tools.DeviceUtil;
import com.jkyssocial.adapter.MultiItemCommonAdapter;
import com.jkyssocial.adapter.MultiItemTypeSupport;
import com.jkyssocial.adapter.ViewHolder;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.Circle;
import com.jkyssocial.data.CircleFansResult;
import com.mintcode.base.BaseActivity;
import com.mintcode.util.ImageManager;
import com.mintcode.util.LogUtil;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class CircleMemberActivity extends BaseActivity implements MultiItemTypeSupport<Buddy>, AdapterView.OnItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, MyListView.OnLoadListener {
    private MultiItemCommonAdapter adapter;
    private MyListView listView;
    private List<Buddy> datas;
    private SwipeRefreshLayout acivity_circle_member_swipeLayout;
    private Circle circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_member);
        ButterKnife.bind(this);
        initView();
        initEvent();
        initData();
        // 网络请求 加载 Circle 的详情
        getData(null);
        LogUtil.addLog(this, "page-forum-circle-member");
    }

    @OnClick(R.id.left_rl)
    void back(View view) {
        finish();
    }

    // 加载码
    private final int REFRESH_CODE = 1;
    private final int LOAD_MORE_CODE = 2;
    // 一次加载多少行
    private static final int COUNT = 20;
    private String baseLine;

    /**
     * 获取某个圈子成员列表的监听
     */
    static class CircleFansRequestListener implements RequestManager.RequestListener<CircleFansResult> {
        WeakReference<CircleMemberActivity> activityWR;

        public CircleFansRequestListener(CircleMemberActivity activity) {
            activityWR = new WeakReference<CircleMemberActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, CircleFansResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            CircleMemberActivity activity = activityWR.get();
            activity.acivity_circle_member_swipeLayout.setRefreshing(false);
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                if (data.getReturnCode().equals("0000")) {
                    activity.baseLine = data.getBaseLine();
                    if (data.getBuddyList() != null && !data.getBuddyList().isEmpty()) {
                        if (requestCode == activity.REFRESH_CODE) {
                            activity.datas.clear();
                            activity.datas.addAll(data.getBuddyList());
                            activity.adapter.notifyDataSetChanged();
                        } else if (requestCode == activity.LOAD_MORE_CODE) {
                            activity.datas.addAll(data.getBuddyList());
                            activity.adapter.notifyDataSetChanged();
                        }
                    } else {
                        if (requestCode == activity.REFRESH_CODE) {
                            Toast.makeText(activity.getApplicationContext(), "网络刷新错误,刷新失败", Toast.LENGTH_SHORT).show();
                        } else
                            activity.listView.forbidLoad("已经全部加载完毕", true);
                    }
                }
            } else {
                Toast.makeText(activity.getApplicationContext(), data.getReturnMsg(), Toast.LENGTH_SHORT).show();
            }
            activity.acivity_circle_member_swipeLayout.setRefreshing(false);
            activity.listView.endLoad();
        }
    }

    private void getData(final String baseLine) {
        int requestCode = (baseLine == null) ? REFRESH_CODE : LOAD_MORE_CODE;
        if (circle.getId() != null) {
            ApiManager.listCircleFans(new CircleFansRequestListener(this), requestCode, this, baseLine, COUNT, circle.getId());
        } else {
            Toast.makeText(this, "跳转进来的界面没有携带circleId进来!", Toast.LENGTH_LONG).show();
        }
    }

    private void initData() {
        datas = new LinkedList<>();
        adapter = new MultiItemCommonAdapter<Buddy>(this, datas, this) {
            @Override
            public void convert(ViewHolder holder, Buddy buddy) {
                if (buddy != null) {
                    if (buddy.getBuddyId().equals(circle.getOwnerId())) {
                        holder.setVisible(R.id.item_circlemember_tag, true);
//                        holder.setText(R.id.item_circlemember_tag, "圈主");
                    } else {
                        holder.setVisible(R.id.item_circlemember_tag, false);
                    }
                    // 加载成员的头像
                    if (!TextUtils.isEmpty(buddy.getImgUrl())) {
                        ImageManager.loadImage(
                                BuildConfig.STATIC_PIC_PATH + buddy.getImgUrl(),
                                CircleMemberActivity.this,
                                (CircleImageView) holder.getView(R.id.avatar), ImageManager.avatarOptions);
                    }

                    ImageManager.setVFlag((ImageView) holder.getView(R.id.vFlag), buddy);
                    holder.setText(R.id.item_circlemember_name, buddy.getUserName());
                }
            }
        };
        listView.setAdapter(adapter);
    }

    private void initEvent() {
        listView.setOnItemClickListener(this);
    }

    private void initView() {
        circle = (Circle) getIntent().getSerializableExtra("circle");
        acivity_circle_member_swipeLayout = (SwipeRefreshLayout) findViewById(R.id.acivity_circle_member_swipeLayout);
        acivity_circle_member_swipeLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        acivity_circle_member_swipeLayout.setProgressViewOffset(true, 190 * DeviceUtil.getDensity(), 240 * DeviceUtil.getDensity());
        acivity_circle_member_swipeLayout.setOnRefreshListener(this);
        acivity_circle_member_swipeLayout.setRefreshing(true);
        listView = (MyListView) findViewById(R.id.acivity_circle_member_listView);
        listView.setOnLoadListener(this);
    }

    @Override
    public int getLayoutId(int position, Buddy buddy) {
        return R.layout.item_circlemember;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position, Buddy buddy) {
        return 0;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(this, NewPersonalSpaceActivity.class);
        Buddy buddy = datas.get(position);
        if (buddy != null) {
            i.putExtra("otherBuddy", buddy);
        }
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
    }


    private boolean isBottom = false;


    @Override
    public void onRefresh() {
        getData(null);
    }

    @Override
    public void onLoad() {
        getData(baseLine);
    }
}
