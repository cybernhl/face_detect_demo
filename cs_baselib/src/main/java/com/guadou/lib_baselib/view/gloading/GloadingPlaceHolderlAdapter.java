package com.guadou.lib_baselib.view.gloading;

import android.view.View;

import static com.guadou.lib_baselib.view.gloading.GloadingPlaceHolderView.NEED_LOADING_STATUS_MAGRIN_TITLE;


/**
 * 占位的Loading数据适配器
 */
public class GloadingPlaceHolderlAdapter implements Gloading.Adapter {

    int mPlaceHolderLayoutRes;

    public GloadingPlaceHolderlAdapter(int placeHolderLayoutRes) {
        mPlaceHolderLayoutRes = placeHolderLayoutRes;
    }

    @Override
    public View getView(Gloading.Holder holder, View convertView, int status, String message) {
        //convertView为可重用的布局
        //Holder中缓存了各状态下对应的View
        //	如果status对应的View为null，则convertView为上一个状态的View
        //	如果上一个状态的View也为null，则convertView为null
        GloadingPlaceHolderView loadingView = null;
        if (convertView != null && convertView instanceof GloadingPlaceHolderView) {
            loadingView = (GloadingPlaceHolderView) convertView;
        }
        if (loadingView == null) {
            loadingView = new GloadingPlaceHolderView(holder.getContext(), mPlaceHolderLayoutRes, holder.getRetryTask());
        }

        //show or not show msg view
        Object data = holder.getData();
        //设置Margin-Title的位置
        loadingView.setTitleBarVisibility(NEED_LOADING_STATUS_MAGRIN_TITLE.equals(data));

        loadingView.setStatus(status, message);


        return loadingView;
    }


}
