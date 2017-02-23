package com.jkyssocial.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.Circle;
import com.jkyssocial.data.ListCircleForUserResult;
import com.jkyssocial.data.OrderCircleForUserResult;
import com.jkyssocial.event.ChangeMyCircleOrderEvent;
import com.jkyssocial.event.CircleChangeEvent;
import com.jkyssocial.widget.DragSortListView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mintcode.base.BaseActivity;
import com.mintcode.util.ImageManager;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import cn.dreamplus.wentang.BuildConfig;
import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;

public class SortMyCircleActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, DragSortListView.DropListener {
    private DragSortListView dragSortListView;
    private SortMyCircleAdapter adapter;
    private View acitivity_sort_my_back;
    View acitivity_sort_my_confirm;
    private List<Circle> circles;

    static class OrderCirleForUserRequestListener implements RequestManager.RequestListener<OrderCircleForUserResult> {
        private WeakReference<SortMyCircleActivity> activityWR;
        private LinkedList<String> list;

        public OrderCirleForUserRequestListener(SortMyCircleActivity activity, LinkedList<String> list) {
            this.activityWR = new WeakReference<SortMyCircleActivity>(activity);
            this.list = list;
        }

        @Override
        public void processResult(int requestCode, int resultCode, OrderCircleForUserResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            SortMyCircleActivity activity = activityWR.get();
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                if (data.getReturnCode().equals("0000")) {
                    Toast.makeText(activity.getApplicationContext(), "排序成功!", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new ChangeMyCircleOrderEvent(list));
                    activity.finish();
                }
            } else {
                if (data != null) {
                    Toast.makeText(activity.getApplicationContext(), data.getReturnMsg(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    static class CirleListForUserRequestListener implements RequestManager.RequestListener<ListCircleForUserResult> {
        private WeakReference<SortMyCircleActivity> activityWR;

        public CirleListForUserRequestListener(SortMyCircleActivity activity) {
            this.activityWR = new WeakReference<SortMyCircleActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, ListCircleForUserResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            SortMyCircleActivity activity = activityWR.get();
            activity.hideLoadDialog();
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                if (data.getReturnCode().equals("0000")) {
                    if (data.getCircleList() != null && !data.getCircleList().isEmpty()) {
                        if (requestCode == activity.requestCode) {
                            // 清空本地tab栏的缓存数据
                            activity.circles.clear();
                            // 网络端返回,更新本地的tab栏数据
                            activity.circles.addAll(data.getCircleList());
                            activity.adapter.notifyDataSetChanged();
                            for (Circle c : activity.circles) {
                                Log.i("Zern----circle", c.getId());
                            }
                        }
                    }
                } else {
                    Toast.makeText(activity.getApplicationContext(), "亲,检查下有没有网络连接啊!", Toast.LENGTH_LONG).show();
                }

            } else if (resultCode == 9960) {
                ApiManager.listCircleForUserV2(this, requestCode, activity.getApplicationContext(), null);
            }
        }
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_my_circle);
        EventBus.getDefault().register(this);
        initView();
        initEvent();
        initData();
    }

    private void initEvent() {
        acitivity_sort_my_back.setOnClickListener(this);
        acitivity_sort_my_confirm.setOnClickListener(this);
        dragSortListView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // 请求码
    private final int requestCode = 1;

    private void initData() {
        circles = new LinkedList<>();
        // 网络数据请求
        showLoadDialog();
        ApiManager.listCircleForUserV2(new CirleListForUserRequestListener(this), requestCode, context, null);
        adapter = new SortMyCircleAdapter(circles, context);
        dragSortListView.setAdapter(adapter);
        // 设置是否可以拖动
        dragSortListView.setDragEnabled(true);
        // 设置拖动监听
        dragSortListView.setDropListener(this);

    }


    private void initView() {
        // 导航栏回退按钮
        acitivity_sort_my_back = findViewById(R.id.left_rl);
        // 确认按钮
        acitivity_sort_my_confirm = findViewById(R.id.right_rl);
        // dragSortListView
        dragSortListView = (DragSortListView) findViewById(R.id.acitivity_sort_my_listView);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R2.id.right_rl:
                final LinkedList<String> list = new LinkedList<>();
                if (circles.size() != 0 && !circles.isEmpty()) {
                    for (Circle circle : circles) {
                        list.add(circle.getId());
                    }
                }
                ApiManager.orderCircleForUser(new OrderCirleForUserRequestListener(SortMyCircleActivity.this, list),
                        requestCode, SortMyCircleActivity.this, list);

                break;
            case R2.id.left_rl:
                finish();
                break;
        }
    }

    @Override
    public void drop(int from, int to) {
        if (from != to) {
            acitivity_sort_my_confirm.setVisibility(View.VISIBLE);
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

    // 用来刷新本页面的所有加入和推荐圈子的数据
    public void onEventMainThread(CircleChangeEvent event) {
        showLoadDialog();
        ApiManager.listCircleForUserV2(new CirleListForUserRequestListener(this), requestCode, this, null);
    }

    class SortMyCircleAdapter extends BaseAdapter {
        private List<Circle> circles;
        private Context context;

        public SortMyCircleAdapter(List<Circle> circles, Context context) {
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
                holder.sortNum = (TextView) view.findViewById(R.id.sortNum);
                holder.item_allcircle_member = (TextView) view.findViewById(R.id.item_allcircle_member);
                holder.item_allcircle_name = (TextView) view.findViewById(R.id.item_allcircle_name);
                holder.item_allcircle_decribute = (TextView) view.findViewById(R.id.item_allcircle_decribute);
                holder.item_allcircle_enter = (TextView) view.findViewById(R.id.item_allcircle_enter);
                holder.sortIcon = (ImageView) view.findViewById(R.id.sortIcon);
                view.setTag(holder);
            }
            holder.sortNum.setText("" + (position + 1));
            holder.sortNum.setVisibility(View.VISIBLE);
            holder.sortIcon.setVisibility(View.VISIBLE);
            holder.item_allcircle_enter.setVisibility(View.GONE);
            // 数据填充
            Circle circle = circles.get(position);
            // 圈子描述:
            holder.item_allcircle_decribute.setText(circle.getSummary() + "");
            // 圈子名字 title
            holder.item_allcircle_name.setText(circle.getTitle() + "");
            // 圈子成员数量
            if (circle.getStat() != null)
                holder.item_allcircle_member.setText("成员: " + circle.getStat().getMemberCount());
            // 圈子的头像
            if (!TextUtils.isEmpty(circle.getAvatar())) {
                ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + circle.getAvatar(), context,
                        holder.item_allcircle_img, ImageManager.circleAvatarOptions);
            }


            return view;
        }

        private class DragHolder {
            private TextView sortNum;
            private RoundedImageView item_allcircle_img;
            private TextView item_allcircle_name, item_allcircle_decribute, item_allcircle_member;
            private TextView item_allcircle_enter;
            private ImageView sortIcon;
        }

    }
}
