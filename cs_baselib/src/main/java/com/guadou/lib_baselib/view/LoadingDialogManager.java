package com.guadou.lib_baselib.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.guadou.basiclib.R;


/**
 * 对话框 Loading
 */
public class LoadingDialogManager {

    private static LoadingDialogManager ourInstance = new LoadingDialogManager();
    private Dialog mDialog;
    private Dialog mProgressDialog;

    public static LoadingDialogManager get() {
        return ourInstance;
    }

    private LoadingDialogManager() {
    }

    public boolean isShowing() {
        if (mDialog == null)
            return false;
        return mDialog.isShowing();
    }

    /**
     * 展示dialog
     */
    public void showLoading(Activity activity) {
        if (mDialog != null && mDialog.isShowing()) {
            return;
        }

        mDialog = new Dialog(activity, R.style.Theme_LoadingDialog);
        //自定义布局
        View view = LayoutInflater.from(activity).inflate(R.layout.base_layout_loading, null);
        mDialog.setContentView(view);
        //一定要先show出来再设置dialog的参数，不然就不会改变dialog的大小了
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);

        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mListener != null)
                    mListener.onLoadingCancel();
            }
        });

        mDialog.show();
    }


    /**
     * 展示长条的大Dialog
     */
    public void showBigLoading(Activity activity) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog = new Dialog(activity, R.style.Theme_LoadingDialog);
        //自定义布局
        View view = LayoutInflater.from(activity).inflate(R.layout.base_layout_uploading_progress, null);
        mProgressDialog.setContentView(view);
        //一定要先show出来再设置dialog的参数，不然就不会改变dialog的大小了
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mListener != null)
                    mListener.onLoadingCancel();
            }
        });

        mProgressDialog.show();
    }

    /**
     * 隐藏dialog
     */
    public void dismissLoading() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog.cancel();
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog.cancel();
        }
    }

    private OnLoadingCancelListener mListener;

    public void SetOnLoadingCancelListener(OnLoadingCancelListener listener) {
        this.mListener = listener;
    }

    public interface OnLoadingCancelListener {

        void onLoadingCancel();
    }

}
