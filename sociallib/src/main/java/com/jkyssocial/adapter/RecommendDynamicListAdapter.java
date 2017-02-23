package com.jkyssocial.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jkys.tools.MainSelector;
import com.jkyshealth.tool.ViewUtil;
import com.jkyslogin.LoginHelper;
import com.jkyssocial.Fragment.HotRecommendFragment;
import com.jkyssocial.activity.MyEnterCircleActivity;
import com.jkyssocial.activity.NewPersonalSpaceActivity;
import com.jkyssocial.activity.SugarControlStarActivity;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.SocialMainHeadData;
import com.jkyssocial.listener.ListUIListener;
import com.mintcode.base.BaseTopActivity;
import com.mintcode.util.ImageManager;
import com.mintcode.util.LogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.dreamplus.wentang.BuildConfig;
import cn.dreamplus.wentang.R;

/**
 * Sugar
 * Created by yangxiaolong on 15/9/2.
 */
public class RecommendDynamicListAdapter extends NewDynamicListAdapter {
    public static final int TYPE_HEAD = 4;
    private SugarFriendCirclesAdapter mAdapter;
    private static SocialMainHeadData socialMainHeadData;

    public SocialMainHeadData getSocialMainHeadData() {
        return socialMainHeadData;
    }

    public void setSocialMainHeadData(SocialMainHeadData socialMainHeadData) {
        this.socialMainHeadData = socialMainHeadData;
        mAdapter = new SugarFriendCirclesAdapter((Activity) context, this.socialMainHeadData.getCircleList());
    }


    public RecommendDynamicListAdapter(Activity context, ListUIListener listUIListener) {
        super(context, listUIListener);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public void onBindViewHolder(DynamicTextViewHolder holder, int position) {
        if (position == 0 && holder instanceof SocialMainHeadHolder) { // 如果是0 那么是头部的数据
            SocialMainHeadHolder headHolder = (SocialMainHeadHolder) holder;
            headHolder.mGridView.setAdapter(mAdapter);
            headHolder.headerLinear.removeAllViews();
            int size = this.socialMainHeadData.getBuddyList().size() < 4 ? this.socialMainHeadData.getBuddyList().size() : 4;
            for (int i = 0; i < size; ++i) {
                final Buddy buddy1 = this.socialMainHeadData.getBuddyList().get(i);
                View view = mLayoutInflater.inflate(R.layout.social_include_listitem_social_dynamic_header, headHolder.headerLinear, false);
                ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
                ImageView vFlag = (ImageView) view.findViewById(R.id.vFlag);
                TextView userTitle = (TextView) view.findViewById(R.id.userTitle);
                TextView userName = (TextView) view.findViewById(R.id.userName);
                ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + buddy1.getImgUrl(), null, avatar, ImageManager.avatarOptions);
                ImageManager.setVFlag(vFlag, buddy1);
                userName.setText(buddy1.getUserName());
                userTitle.setText(buddy1.getUserType() == 1 ? "专家医生" : "资深糖友");
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (LoginHelper.getInstance().showLoginActivity((Activity) context)) return;
                        if (AppImpl.getAppRroxy().isNeedNewMain() )
                            return;
                        AppImpl.getAppRroxy().addLog(context, "event-forum-recommend-expert-" + buddy1.getBuddyId());
                        Intent intent = new Intent(context, NewPersonalSpaceActivity.class);
                        intent.putExtra("otherBuddy", buddy1);
                        context.startActivity(intent);
                    }
                });
                headHolder.headerLinear.addView(view);
            }
        } else {
            holder.renqiTV.setVisibility(View.GONE);
            if (position == 1) {
                holder.headerLine.setVisibility(View.GONE);
                holder.headerPad.setVisibility(View.GONE);
            } else if (position > 1) {
                holder.headerLine.setVisibility(View.VISIBLE);
                holder.headerPad.setVisibility(View.VISIBLE);
            }
            super.onBindViewHolder(holder, position);
        }
//        if (dynamic.getViewCount() >= 10000) {
//            holder.renqiTV.setText("人气 " + String.format("%.1f", ((float) dynamic.getViewCount()) / 10000) + "万");
//        } else {
//            holder.renqiTV.setText("人气 " + dynamic.getViewCount());
//        }
    }

    @Override
    public DynamicTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            return new SocialMainHeadHolder(mLayoutInflater.inflate(R.layout.zern_zern_zern, parent, false));
        } else {
            return super.onCreateViewHolder(parent, viewType);
        }
    }

    public static class SocialMainHeadHolder extends DynamicTextViewHolder {
        @BindView(R2.id.social_main_gridView)
        public GridView mGridView;

        @BindView(R2.id.recommend_rl)
        public RelativeLayout recommendRl;

        @BindView(R2.id.headerLinear)
        public LinearLayout headerLinear;

        @BindView(R2.id.findMoreSuperStar)
        public TextView findMoreSuperStar;

        @BindView(R2.id.social_main_mycircle_icon)
        public ImageView social_main_mycircle_icon;

        @BindView(R2.id.social_main_mycircle)
        public TextView social_main_mycircle;

        public SocialMainHeadHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            findMoreSuperStar.setOnClickListener(this);
            social_main_mycircle_icon.setOnClickListener(this);
            social_main_mycircle.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Activity topActivity = BaseTopActivity.getTopActivity();
            if (topActivity == null) {
                return;
            }
            if (LoginHelper.getInstance().showLoginActivity(topActivity)) return;
            switch (v.getId()) {
                case R2.id.social_main_mycircle:
                case R2.id.social_main_mycircle_icon:
                    if (!ViewUtil.singleClick()) return;
                    Intent intent = new Intent(topActivity, MyEnterCircleActivity.class);
                    intent.putExtra("myBuddy", HotRecommendFragment.myBuddy);
                    topActivity.startActivity(intent);
                    break;
                case R2.id.findMoreSuperStar:
                    topActivity.startActivity(new Intent(topActivity, SugarControlStarActivity.class));
                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        } else {
            return super.getItemViewType(position);
        }
    }

    public void create() {
        ApiManager.listDynamicForRecommend(this, CREATE_CODE, context, null, PAGING_COUNT);
    }

    public void loadmore() {
        ApiManager.listDynamicForRecommend(this, LOAD_MORE_CODE, context, baseLine, PAGING_COUNT);
    }

    public void refresh() {
        ApiManager.listDynamicForRecommend(this, REFRESH_CODE, context, null, PAGING_COUNT);
    }

}