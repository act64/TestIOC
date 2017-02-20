package com.jkyssocial.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamplus.wentang.R;

import com.jkys.common.widget.CustomToolbar;
import com.jkyssocial.adapter.MultiItemCommonAdapter;
import com.jkyssocial.adapter.MultiItemTypeSupport;
import com.jkyssocial.adapter.ViewHolder;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.CircleClass;
import com.jkyssocial.data.CircleClassListResult;
import com.jkyssocial.data.CircleResult;
import com.mintcode.base.BaseActivity;

import org.jsoup.helper.StringUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ChooseTypeActivity extends BaseActivity implements AdapterView.OnItemClickListener, MultiItemTypeSupport<CircleClass>, View.OnClickListener {
    private ListView listView;
//    private TextView tv_back;
    private MultiItemCommonAdapter<CircleClass> adapter;
    private List<CircleClass> datas;
    private int resultCode = 998;
    private String CircleType;
    private ImageView checkMaskImg;

    @Bind(R.id.toolbar)
    CustomToolbar toolbar;

    /**
     * 请求圈子类别列表
     */
    static class CircleClassListRequestListener implements RequestManager.RequestListener<CircleClassListResult> {
        WeakReference<ChooseTypeActivity> activityWR;

        public CircleClassListRequestListener(ChooseTypeActivity activity) {
            activityWR = new WeakReference<ChooseTypeActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, CircleClassListResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            final ChooseTypeActivity activity = activityWR.get();
            if (data != null && data.getCircleClassList() != null && !data.getCircleClassList().isEmpty()) {
                activity.datas.addAll(data.getCircleClassList());
                activity.adapter = new MultiItemCommonAdapter<CircleClass>(
                        activity.getApplicationContext(), activity.datas, activity) {
                    @Override
                    public void convert(ViewHolder holder, CircleClass s) {
                        holder.setText(R.id.item_circle_type, s.getName());
                        if (!TextUtils.isEmpty(activity.CircleType)) {
                            if (activity.CircleType.equals(s.getName())) {
                                holder.setVisible(R.id.item_circle_confirm, true);
                                activity.checkMaskImg = (ImageView) (holder.getView(R.id.item_circle_confirm));
                            }
                        }
                    }
                };
                activity.listView = (ListView) activity.findViewById(R.id.activity_choose_circle_lv);
                activity.listView.setAdapter(activity.adapter);
                activity.listView.setOnItemClickListener(activity);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_type);
        ButterKnife.bind(this);
        ApiManager.listCircleClass(new CircleClassListRequestListener(this), 1, this);
//        tv_back = (TextView) findViewById(R.id.activity_choose_circle_back);
//        tv_back.setOnClickListener(this);
        datas = new ArrayList<>();
    }

    @OnClick(R.id.left_rl)
    void onBackClick(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CircleType = getIntent().getStringExtra("CircleType");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (checkMaskImg != null) {
            checkMaskImg.setVisibility(View.GONE);
        }
        ImageView img = (ImageView) view.findViewById(R.id.item_circle_confirm);
        img.setVisibility(View.VISIBLE);
        Intent intent = new Intent();
        intent.putExtra("CircleType", datas.get(position).getName());
        intent.putExtra("circleClassCode", datas.get(position).getCode());
        setResult(resultCode, intent);
        finish();
    }


    @Override
    public int getLayoutId(int position, CircleClass s) {
        return R.layout.item_circle_type;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position, CircleClass s) {
        return 0;
    }

    @Override
    public void onClick(View v) {
//        int id = v.getId();
//        switch (id) {
//            case R.id.activity_choose_circle_back:
//                finish();
//                break;
//        }
    }
}
