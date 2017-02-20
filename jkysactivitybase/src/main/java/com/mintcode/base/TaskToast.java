package com.mintcode.base;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.mintcode.util.Const;

import cn.dreamplus.wentang.R;

public class TaskToast extends Toast {

	private View mContent;
	private TextView mTvTip;

	public TaskToast(Activity context) {
		super(context.getApplicationContext());
		LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
		mContent = inflater.inflate(R.layout.dialog_task, null);
		mTvTip = (TextView) mContent.findViewById(R.id.tv_tip);
		int widthPixels = Const.getDM(context).widthPixels;
		int heightPixels = dipTopx(context.getApplicationContext(), 50);
		LayoutParams params = new LayoutParams(widthPixels, heightPixels);
		if (mTvTip != null) {
			mTvTip.setLayoutParams(params);
			setGravity(Gravity.TOP, 0, 150);
			setDuration(Toast.LENGTH_LONG);
			setView(mContent);
		}
		
	}

	public  int dipTopx(Context context, float dpValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public void show(String str) {
		mTvTip.setText(str);
		show();
	}

}
