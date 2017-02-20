package com.jkyssocial.pageradapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jkys.jkysim.chat.emoji.MsgFaceUtils;
import com.jkys.jkysim.chat.emoji.ParseEmojiMsgUtil;

import cn.dreamplus.wentang.R;

/**
 * ImagePagerAdapter
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-23
 */
public class MessageBoxPagerAdapter extends RecyclingPagerAdapter implements OnClickListener{

    private Activity mActivity;
    private Context context;

    private OnClickListener itemOnClickListener;

    private OnFaceOprateListener onFaceOprateListener;

    private int count = MsgFaceUtils.faceImgs.length / 20 + 1;

    public MessageBoxPagerAdapter(Activity activity, OnFaceOprateListener onFaceOprateListener1) {
        this.mActivity = activity;
        this.context = activity.getApplicationContext();
        this.onFaceOprateListener = onFaceOprateListener1;
        itemOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = MsgFaceUtils.faceImgNames[(int) v.getTag()];
                onFaceOprateListener.onFaceSelected(ParseEmojiMsgUtil.convertUnicode(s));
            }
        };
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(
                    R.layout.social_slider_message_box, container, false);
            holder = new ViewHolder();
            holder.linearLayouts = new LinearLayout[3];
            holder.imageViews = new ImageView[21];
            for(int i = 0; i < 3; ++i) {
                holder.linearLayouts[i] = new LinearLayout(context);
                holder.linearLayouts[i].setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 50);
                holder.linearLayouts[i].setLayoutParams(params);
                for(int j = 0; j < 7; j++){
                    holder.imageViews[i * 7 +j] = new ImageView(context);
                    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                    holder.imageViews[i * 7 +j].setLayoutParams(lparams);
                    holder.imageViews[i * 7 +j].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                    holder.imageViews[i * 7 +j].setOnClickListener(itemOnClickListener);
                    holder.linearLayouts[i].addView(holder.imageViews[i * 7 + j]);
                }
                ((ViewGroup) convertView).addView(holder.linearLayouts[i]);
            }
            holder.imageViews[20].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFaceOprateListener.onFaceDeleted();
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        int startIndex = position * 20;
        int curIndex = 0;
        int length = MsgFaceUtils.faceImgs.length;
        for(int i = 0; i < 3; ++i) {
            if(i < 2) {
                for (int k = 0; k < 7; ++k) {
                    if (startIndex + curIndex < length) {
                        holder.imageViews[curIndex].setTag(startIndex + curIndex);
                        holder.imageViews[curIndex].setOnClickListener(itemOnClickListener);
                        holder.imageViews[curIndex].setImageResource(MsgFaceUtils.faceImgs[startIndex + curIndex]);
                    }else{
                        holder.imageViews[curIndex].setTag(null);
                        holder.imageViews[curIndex].setOnClickListener(null);
                        holder.imageViews[curIndex].setImageResource(R.color.transparent);
                    }
                    curIndex++;
                }
            }else{
                for (int k = 0; k < 6; ++k) {
                    if (startIndex + curIndex< length) {
                        holder.imageViews[curIndex].setTag(startIndex + curIndex);
                        holder.imageViews[curIndex].setOnClickListener(itemOnClickListener);
                        holder.imageViews[curIndex].setImageResource(MsgFaceUtils.faceImgs[startIndex + curIndex]);
                    }else{
                        holder.imageViews[curIndex].setTag(null);
                        holder.imageViews[curIndex].setOnClickListener(null);
                        holder.imageViews[curIndex].setImageResource(R.color.transparent);
                    }
                    curIndex++;
                }
                holder.imageViews[curIndex].setImageResource(R.drawable.social_cancel);
            }
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        mActivity.finish();
    }

    private class ViewHolder {
        LinearLayout[] linearLayouts;
        ImageView[] imageViews;
    }

    public interface OnFaceOprateListener {

        void onFaceSelected(String unicode);

        void onFaceDeleted();
    }
}
