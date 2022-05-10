package com.guadou.lib_baselib.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.guadou.basiclib.R;


public class CommUtils {

    private static Application mApplication;
    private static Handler mHandler;
    private static int mMainTheadId;

    public static void init(Application application, Handler handler, int mainTheadId) {
        mApplication = application;
        mHandler = handler;
        mMainTheadId = mainTheadId;
    }


    //---------------------初始化Application定义的方法-----------------------------------


    public static Context getContext() {
        return mApplication.getApplicationContext();
    }

    public static Handler getHandler() {
        return mHandler;
    }

    public static int getMainThreadId() {
        return mMainTheadId;
    }


    //------------------------获取各种资源----------------------------------------

    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    public static int[] getIntArray(int id) {
        return getContext().getResources().getIntArray(id);
    }

    public static Drawable getDrawable(int id) {
        return ContextCompat.getDrawable(getContext(), id);
    }

    public static int getColor(int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    public static ColorStateList getColorStateList(int id) {
        return ContextCompat.getColorStateList(getContext(), id);
    }

    public static int getDimens(int id) {
        return getContext().getResources().getDimensionPixelSize(id);
    }

    //--------------------px和dip之间的转换-----------------------------------------------

    public static int dip2px(int dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f);
    }

    public static float px2dip(int px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return px / density;
    }

    public static float dip2px(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (dip * density + 0.5f);
    }

    public static float px2dip(float px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return px / density;
    }


    //-------------------加载布局文件-------------------------------------------------

    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

    //-------------------是否运行在主线程 -----------------------------------------------

    public static boolean isRunOnUIThread() {
        int myTid = android.os.Process.myTid();
        if (myTid == getMainThreadId()) {
            return true;
        }
        return false;
    }

    //运行在主线程
    public static void runOnUIThread(Runnable runnable) {
        if (isRunOnUIThread()) {
            runnable.run();
        } else {
            getHandler().post(runnable);
        }
    }

}
