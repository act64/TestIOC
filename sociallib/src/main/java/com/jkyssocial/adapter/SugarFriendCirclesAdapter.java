package com.jkyssocial.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jkyslogin.LoginHelper;
import com.jkyssocial.activity.AllCircleActivity;
import com.jkyssocial.activity.CircleMainActivity;
import com.jkyssocial.data.Circle;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mintcode.util.ImageManager;
import com.mintcode.util.LogUtil;

import java.util.List;

import cn.dreamplus.wentang.BuildConfig;
import cn.dreamplus.wentang.R;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/6/3
 * Time: 13:11
 * Email:AndroidZern@163.com
 */
public class SugarFriendCirclesAdapter extends BaseAdapter {

    private Activity activity;
    private List<Circle> circles;

    public SugarFriendCirclesAdapter(Activity activity, List<Circle> circles) {
        this.activity = activity;
        this.circles = circles;
    }

    @Override
    public int getCount() {
        int size = circles.size();
        if (size > 0) {
            return size + 1;
        } else {
            return size;
        }
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.item_social_main_gridview, parent, false);
        }
        CircleHolder holder = (CircleHolder) view.getTag();
        if (holder == null) {
            holder = new CircleHolder();
            holder.mImage = (RoundedImageView) view.findViewById(R.id.item_circle_image);
            holder.mTitle = (TextView) view.findViewById(R.id.item_circle_title);
            view.setTag(holder);
        }

        final int size = circles.size();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 前端判断是否是游客。游客判断 这边登录逻辑 只有这块是前端判断额。
                if (LoginHelper.getInstance().isVisitor()) {
                    Activity activity = LoginHelper.getInstance().ChangeToActivity(
                            SugarFriendCirclesAdapter.this.activity.getApplicationContext());
                    LoginHelper.getInstance().ForcedReLogin(activity);
                } else {
                    if (position == size) { // 最后一行的更多圈子的点击事件
                        activity.startActivity(new Intent(activity, AllCircleActivity.class));
                    } else if (position < size) {
                        LogUtil.addLog(activity, "event-forum-recommend-circle-" + circles.get(position).getId());
                        Intent intent = new Intent(activity, CircleMainActivity.class);
                        intent.putExtra("circle", circles.get(position));
                        activity.startActivity(intent);
                    }
                }
            }
        });

        if (position == size) {
            holder.mTitle.setText("更多圈子");
            holder.mTitle.setTextColor(Color.parseColor("#999999"));
            holder.mImage.setImageResource(R.drawable.icon_more_circle);
        } else if (position < size) {
            Circle circle = circles.get(position);
            holder.mTitle.setText(circle.getTitle() + "");
            holder.mTitle.setTextColor(Color.parseColor("#333333"));
            if (!TextUtils.isEmpty(circle.getAvatar())) {
                ImageManager.loadImageByDefaultImage(BuildConfig.STATIC_PIC_PATH + circle.getAvatar(),
                        activity.getApplicationContext(), holder.mImage, R.drawable.social_circle_avatar);
            } else {
                holder.mImage.setImageResource(R.drawable.social_circle_avatar);
            }
        }

        return view;
    }

    private class CircleHolder {
        private RoundedImageView mImage;
        private TextView mTitle;
    }

}
