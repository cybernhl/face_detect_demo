package com.guadou.lib_baselib.view.gloading;

import android.view.View;

import static com.guadou.lib_baselib.view.gloading.GloadingGlobalStatusView.HIDE_LOADING_STATUS_MSG;
import static com.guadou.lib_baselib.view.gloading.GloadingGlobalStatusView.NEED_LOADING_STATUS_MAGRIN_TITLE;

/**
 * 默认的是上下跳动的加载动画
 */
public class GloadingGlobalAdapter implements Gloading.Adapter {

    @Override
    public View getView(Gloading.Holder holder, View convertView, int status, String message) {
        //convertView为可重用的布局
        //Holder中缓存了各状态下对应的View
        //	如果status对应的View为null，则convertView为上一个状态的View
        //	如果上一个状态的View也为null，则convertView为null
        GloadingGlobalStatusView loadingView = null;
        if (convertView != null && convertView instanceof GloadingGlobalStatusView) {
            loadingView = (GloadingGlobalStatusView) convertView;
        }
        if (loadingView == null) {
            loadingView = new GloadingGlobalStatusView(holder.getContext(), holder.getRetryTask());
        }

        //show or not show msg view
        Object data = holder.getData();

        //设置Margin-Title的位置
        loadingView.setTitleBarVisibility(NEED_LOADING_STATUS_MAGRIN_TITLE.equals(data));


        loadingView.setStatus(status, message);

        //Loading文本先不需要隐藏
        loadingView.setMsgViewVisibility(!HIDE_LOADING_STATUS_MSG.equals(data));


        return loadingView;
    }


}
