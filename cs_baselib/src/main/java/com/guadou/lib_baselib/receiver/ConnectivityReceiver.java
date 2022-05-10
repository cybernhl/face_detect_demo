package com.guadou.lib_baselib.receiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import androidx.annotation.NonNull;

import com.guadou.lib_baselib.annotation.NetWork;
import com.guadou.lib_baselib.base.BaseApplication;
import com.guadou.lib_baselib.utils.NetWorkUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 监听网络变换的广播，集成在BaseActivity
 * 需要在Application中注册，ConnectivityReceiver.registerReceiver(this);
 */

public class ConnectivityReceiver extends BroadcastReceiver {

    //Observer的通知集合对象，本质上是接口回调
    private List<ConnectivityReceiverListener> mObservers = new ArrayList<>();
    //保存以注解方式定义的网络监听方法的Map集合
    private static Map<Object, List<NetworkMethodManager>> mAnnotationNetWorkObservers = new HashMap<>();

    private long lastTimeMilles = 0;
    private NetWorkUtil.NetworkType LAST_TYPE = NetWorkUtil.NetworkType.NETWORK_UNKNOWN;


    private static class InstanceHolder {
        private static final ConnectivityReceiver INSTANCE = new ConnectivityReceiver();
    }

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {

            NetWorkUtil.NetworkType networkType = NetWorkUtil.getNetworkType(context);
            long currentTimeMillis = System.currentTimeMillis();

            //如果这次和上次的相同，那么5秒之内 只能触发一次
            if (LAST_TYPE == networkType) {
                if (lastTimeMilles == 0) {
                    doNotifyObserver(networkType);
                } else {
                    if (currentTimeMillis - lastTimeMilles > 5000) {
                        doNotifyObserver(networkType);
                    }
                }
            } else {
                doNotifyObserver(networkType);
            }

            //重新赋值最后一次的网络类型和时间戳
            lastTimeMilles = currentTimeMillis;
            LAST_TYPE = networkType;
        }
    }

    //具体去执行通知
    private void doNotifyObserver(NetWorkUtil.NetworkType networkType) {
        //收到变换网络的通知就通过遍历集合去循环回调接口
        notifyObservers(networkType);

        //通知注解类型
        notifyByAnnotation(networkType);

        //赋值Application全局的类型
        BaseApplication.networkType = networkType;
    }

    /**
     * 注册网络监听
     */
    public static void registerReceiver(@NonNull Application application) {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        application.getApplicationContext().registerReceiver(InstanceHolder.INSTANCE, intentFilter);
    }

    /**
     * 取消网络监听
     */
    public static void unregisterReceiver(@NonNull Application application) {
        application.getApplicationContext().unregisterReceiver(InstanceHolder.INSTANCE);
    }


    /**
     * 注册网络变化Observer
     */
    public static void registerObserver(ConnectivityReceiverListener observer) {
        if (observer == null)
            return;
        if (!InstanceHolder.INSTANCE.mObservers.contains(observer)) {
            InstanceHolder.INSTANCE.mObservers.add(observer);
        }
    }

    /**
     * 取消网络变化Observer的注册
     */
    public static void unregisterObserver(ConnectivityReceiverListener observer) {
        if (observer == null)
            return;
        if (InstanceHolder.INSTANCE.mObservers == null)
            return;
        InstanceHolder.INSTANCE.mObservers.remove(observer);
    }

    /**
     * 注册网络变化的annotation的方法对象
     */
    public static void registerAnnotationObserver(Object object) {
        List<NetworkMethodManager> networkMethodManagers = mAnnotationNetWorkObservers.get(object);

        if (networkMethodManagers == null || networkMethodManagers.isEmpty()) {
            //以前没有注册过，开始添加
            networkMethodManagers = findAnnotationMethod(object);
            mAnnotationNetWorkObservers.put(object, networkMethodManagers);
        }

    }

    /**
     * 找到类中的全部注解@NetWork的方法
     */
    private static List<NetworkMethodManager> findAnnotationMethod(Object object) {
        List<NetworkMethodManager> networkMethodManagers = new ArrayList<>();

        Class<?> clazz = object.getClass();  //获取当前对象的class对象
        Method[] methods = clazz.getMethods();  //获取当前class内部全部的method方法
        for (Method method : methods) {
            NetWork netWork = method.getAnnotation(NetWork.class);  //循环判断取出内部的@NetWork注解
            if (netWork == null) continue;

            //开始添加到集合
            NetworkMethodManager manager = new NetworkMethodManager(null, netWork.netWorkType(), method);
            networkMethodManagers.add(manager);
        }

        return networkMethodManagers;
    }

    /**
     * 解绑网络变化的annotation的方法对象
     */
    public static void unregisterAnnotationObserver(Object object) {
        if (!mAnnotationNetWorkObservers.isEmpty()) {
            //如果不为空，直接移除
            mAnnotationNetWorkObservers.remove(object);
        }
    }

    /**
     * 通知所有的Observer网络状态变化
     */
    private void notifyObservers(NetWorkUtil.NetworkType networkType) {
        if (networkType == NetWorkUtil.NetworkType.NETWORK_NO || networkType == NetWorkUtil.NetworkType.NETWORK_UNKNOWN) {
            for (ConnectivityReceiverListener observer : mObservers) {
                observer.onNetworkConnectionChanged(false, networkType);
            }
        } else {
            for (ConnectivityReceiverListener observer : mObservers) {
                observer.onNetworkConnectionChanged(true, networkType);
            }
        }
    }

    /**
     * 通知注解的方法类型去调用方法
     */
    private void notifyByAnnotation(NetWorkUtil.NetworkType networkType) {
        Set<Object> keySet = mAnnotationNetWorkObservers.keySet();
        for (final Object getter : keySet) {
            //获取当前类的全部@NetWork方法
            List<NetworkMethodManager> networkMethodManagers = mAnnotationNetWorkObservers.get(getter);
            assert networkMethodManagers != null;
            if (!networkMethodManagers.isEmpty()) {

                for (final NetworkMethodManager manager : networkMethodManagers) {

                    //逐一匹配对应的
                    if (manager.getNetworkType() == NetWorkUtil.NetworkType.NETWORK_2G) {
                        if (networkType == NetWorkUtil.NetworkType.NETWORK_2G || networkType == NetWorkUtil.NetworkType.NETWORK_NO) {
                            invoke(manager, getter);
                        }
                    } else if (manager.getNetworkType() == NetWorkUtil.NetworkType.NETWORK_3G) {
                        if (networkType == NetWorkUtil.NetworkType.NETWORK_3G || networkType == NetWorkUtil.NetworkType.NETWORK_NO) {
                            invoke(manager, getter);
                        }
                    } else if (manager.getNetworkType() == NetWorkUtil.NetworkType.NETWORK_4G) {
                        if (networkType == NetWorkUtil.NetworkType.NETWORK_4G || networkType == NetWorkUtil.NetworkType.NETWORK_NO) {
                            invoke(manager, getter);
                        }
                    } else if (manager.getNetworkType() == NetWorkUtil.NetworkType.NETWORK_WIFI) {
                        if (networkType == NetWorkUtil.NetworkType.NETWORK_WIFI || networkType == NetWorkUtil.NetworkType.NETWORK_NO) {
                            invoke(manager, getter);
                        }
                    } else if (manager.getNetworkType() == NetWorkUtil.NetworkType.NETWORK_UNKNOWN) {
                        invoke(manager, getter);

                    } else if (manager.getNetworkType() == NetWorkUtil.NetworkType.NETWORK_NO) {
                        if (networkType == NetWorkUtil.NetworkType.NETWORK_NO) {
                            invoke(manager, getter);
                        }
                    }
                }
            }
        }
    }

    /**
     * 反射执行的具体注解方法
     */
    private void invoke(NetworkMethodManager manager, Object getter) {
        Method method = manager.getMethod();
        try {
            method.invoke(getter);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    //通过这个接口回调出去
    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected, NetWorkUtil.NetworkType networkType);
    }
}
