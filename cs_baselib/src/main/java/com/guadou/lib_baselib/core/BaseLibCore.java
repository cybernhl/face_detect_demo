package com.guadou.lib_baselib.core;

import android.app.Application;
import android.graphics.Color;
import android.os.Handler;

import androidx.core.content.ContextCompat;

import com.guadou.basiclib.R;
import com.guadou.lib_baselib.receiver.ConnectivityReceiver;
import com.guadou.lib_baselib.utils.CommUtils;
import com.guadou.lib_baselib.utils.ThreadPoolUtils;
import com.guadou.lib_baselib.view.gloading.Gloading;
import com.guadou.lib_baselib.view.gloading.GloadingGlobalAdapter;
import com.guadou.lib_baselib.view.titlebar.EasyTitleBar;


/**
 * 底层的模组依赖库都在这里初始化
 * 当前类在Application中初始化
 */
public class BaseLibCore {

    //static 代码段可以防止内存泄露
//    static {
//        //设置全局的Header构建器
//        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
//            @Override
//            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
//                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
//                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
//            }
//        });
//    }

    /**
     * 初始化全局工具类和图片加载引擎
     */
    public static void init(Application application, Handler handler, int mainThread) {

        //CommUtil初始化
        CommUtils.init(application, handler, mainThread);

        //初始化全局通用的线程池
        ThreadPoolUtils.init();

        //EaseTitleBar的初始化
        EasyTitleBar.init()
                .backIconRes(R.mipmap.back_black)
                .backgroud(Color.WHITE)
                .titleSize(18)
                .showLine(false)
                .lineHeight(1)
                .menuImgSize(23)
                .menuTextSize(16)
                .lineColor(Color.parseColor("#DFE4EE"))
                .titleColor(ContextCompat.getColor(application.getApplicationContext(), R.color.black))
                .viewPadding(10)
                .titleBarHeight(46);

        //全局的Loading状态默认配置
        Gloading.initDefault(new GloadingGlobalAdapter());

    }

    /**
     * 注册网络监听
     */
    public static void registerNetworkObserver(Application application) {
        ConnectivityReceiver.registerReceiver(application);
    }

    public static void unregisterNetworkObserver(Application application) {
        ConnectivityReceiver.unregisterReceiver(application);
    }


}
