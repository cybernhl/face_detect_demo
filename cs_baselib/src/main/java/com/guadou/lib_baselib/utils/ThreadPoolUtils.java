package com.guadou.lib_baselib.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 全局线程池工具类
 */
public class ThreadPoolUtils {

    private static ExecutorService mCachedThreadPool;

    public static void init(){
        mCachedThreadPool = Executors.newCachedThreadPool();
    }

    /**
     * 初始化线程池，全局通用缓存线程池
     */
    public static ExecutorService getCachedThreadPool() {
        return mCachedThreadPool;
    }


}
