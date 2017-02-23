package com.jkys.proxy;

import android.content.Context;
import android.os.Handler;

import java.util.concurrent.Executor;

/**
 * Created by ylei on 2017/2/23.
 */

public abstract class UpLoadWxAvaTarTaskProxy {
   public abstract void executeOnExecutor(Executor threadPoolExecutor);

   public UpLoadWxAvaTarTaskProxy(Context context, String url, Handler handler) {

   }
}
