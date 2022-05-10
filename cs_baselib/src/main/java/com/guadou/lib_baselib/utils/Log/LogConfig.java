package com.guadou.lib_baselib.utils.Log;

import android.text.TextUtils;

import com.guadou.basiclib.BuildConfig;


/**
 * Created by Administrator on 2017/4/12.
 */

public class LogConfig {
    private boolean showThreadInfo = true;
    private boolean debug = BuildConfig.DEBUG;

    private String tag = "Message";
    public LogConfig setTag(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            this.tag = tag;
        }
        return this;
    }
    public LogConfig setShowThreadInfo(boolean showThreadInfo) {
        this.showThreadInfo = showThreadInfo;
        return this;
    }
    public LogConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }
    public String getTag() {
        return tag;
    }
    public boolean isDebug() {
        return debug;
    }
    public boolean isShowThreadInfo() {
        return showThreadInfo;
    }
}
