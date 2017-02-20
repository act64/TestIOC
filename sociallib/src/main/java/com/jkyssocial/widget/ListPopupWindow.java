package com.jkyssocial.widget;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jkyssocial.common.util.ZernToast;
import com.jkyssocial.data.Circle;


import java.util.List;

import cn.dreamplus.wentang.R;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/9/20
 * Time: 10:36
 * Email:AndroidZern@163.com
 */
public class ListPopupWindow extends PopupWindow {
    private Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private ListPopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public static class Builder {
        private Context context;
        private Activity activityB;
        private List<Circle> circles;
        private AdapterView.OnItemClickListener listener;
        private ListPopupWindow listPopupWindow;
        private DialogAdapter adapter;

        public Builder(Activity activity, List<Circle> circles, AdapterView.OnItemClickListener listener) {
            this.activityB = activity;
            this.context = activity.getApplicationContext();
            this.circles = circles;
            this.listener = listener;
        }

        public DialogAdapter getAdapter() {
            return adapter;
        }

        public ListPopupWindow create(View.OnClickListener cancelListener) {
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.list_popup_window, null);
            ListView listView = (ListView) linearLayout.findViewById(R.id.listView);
            listPopupWindow = new ListPopupWindow(linearLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            listPopupWindow.setActivity(activityB);
            linearLayout.findViewById(R.id.dialog_cancel_btn).setOnClickListener(cancelListener);
            adapter = new DialogAdapter(context, circles);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(listener);
            setListViewHeightBasedOnChildren(listView);
            listPopupWindow.setOutsideTouchable(false);
            listPopupWindow.setFocusable(true);
            listPopupWindow.update();
//            setBackgroundAlpha(activity, 0.2f);
            listPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//            listPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
            return listPopupWindow;
        }
    }

    public void show(View view, float bgAlpha) {
        if (activity == null) {
            return;
        }
        setBackgroundAlpha(activity, bgAlpha);
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    public void showDefaultAlpha(View view) {
        if (activity == null) {
            return;
        }
        setBackgroundAlpha(activity, 0.2f);
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    private void hide() {
        if (activity == null) {
            return;
        }
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 1f;
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 设置页面的透明度
     *
     * @param bgAlpha 1表示不透明  针对华为手机网上出的方案
     */
    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        activity.getWindow().setAttributes(lp);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int size = listAdapter.getCount() < 5 ? listAdapter.getCount() : 5;
        for (int i = 0; i < size; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static class DialogAdapter extends BaseAdapter {

        private Context context;
        private List<Circle> circles;

        public DialogAdapter(Context context, List<Circle> circles) {
            this.context = context;
            this.circles = circles;
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
                view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.text_item, parent, false);
            }
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.textView = (TextView) view.findViewById(R.id.circle_name_tv);
                holder.duihao_img = (ImageView) view.findViewById(R.id.duihao_img);
                view.setTag(holder);
            }
            if (circles.get(position).isChecked()) {
                holder.duihao_img.setVisibility(View.VISIBLE);
            } else {
                holder.duihao_img.setVisibility(View.GONE);
            }

            holder.textView.setText(circles.get(position).getTitle() + "");
            return view;
        }

        static class ViewHolder {
            private TextView textView;
            private ImageView duihao_img;
        }
    }


    @Override
    public void dismiss() {
//        Log.e("wuweixiang","dismiss");
        super.dismiss();
        hide();
    }
}
