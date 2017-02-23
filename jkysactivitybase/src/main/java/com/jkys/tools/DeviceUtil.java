package com.jkys.tools;

import android.content.Context;
import android.util.DisplayMetrics;

import com.jkys.proxy.AppImpl;

public class DeviceUtil {

	static int d = 0;

	static DisplayMetrics displayMetric;
	
	static Context context = AppImpl.getAppRroxy().getApplicationContext();

	public static DisplayMetrics getDisplay() {
		if (displayMetric == null)
			displayMetric = context.getResources()
					.getDisplayMetrics();
		return displayMetric;
	}

	public static int getDensity() {
		if (d <= 0)
			d = (int) context.getResources().getDisplayMetrics().density;
		return d;
	}

}
