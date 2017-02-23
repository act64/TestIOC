package com.jkys.proxy;


import android.content.Context;
import android.os.Handler;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by ylei on 2017/2/23.
 */

public class ProxyClassFactory {
    public static  <TT > TT getProxyClass(Class<TT> ttClass){
        try {
            return ttClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static  <TT extends UpLoadWxAvaTarTaskProxy > TT getProxyClass(Class<TT> ttClass, Context context, String url, Handler handler){
        try {
          return   ttClass.getConstructor(context.getClass(),Context.class).newInstance(context, url, handler);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;

    }
}
