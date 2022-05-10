package com.guadou.lib_baselib.receiver;


import com.guadou.lib_baselib.utils.NetWorkUtil;

import java.lang.reflect.Method;

/**
 * 保存符合条件的网络监听注解的对象
 */
public class NetworkMethodManager {

    private Class<?> type; //参数类型

    private NetWorkUtil.NetworkType networkType; //网络类型

    private Method method;  //方法对象

    public NetworkMethodManager() {
    }

    public NetworkMethodManager(Class<?> type, NetWorkUtil.NetworkType networkType, Method method) {
        this.type = type;
        this.networkType = networkType;
        this.method = method;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public NetWorkUtil.NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetWorkUtil.NetworkType networkType) {
        this.networkType = networkType;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
