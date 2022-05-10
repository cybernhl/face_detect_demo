package com.guadou.lib_baselib.view.gloading;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guadou.basiclib.R;

import static com.guadou.lib_baselib.view.gloading.Gloading.STATUS_EMPTY_DATA;
import static com.guadou.lib_baselib.view.gloading.Gloading.STATUS_LOADING;
import static com.guadou.lib_baselib.view.gloading.Gloading.STATUS_LOAD_FAILED;
import static com.guadou.lib_baselib.view.gloading.Gloading.STATUS_LOAD_SUCCESS;
import static com.guadou.lib_baselib.view.gloading.Gloading.STATUS_NORMAL;

/**
 * 这是菊花Loading
 */
@SuppressLint("ViewConstructor")
public class GloadingLoadingStatusView extends LinearLayout implements View.OnClickListener {

    public static String HIDE_LOADING_STATUS_MSG = "hide_loading_status_msg";  //是否需要展示message文本
    public static String NEED_LOADING_STATUS_MAGRIN_TITLE = "loading_status_magrin_title";  //是否需要顶部的margin

    private final TextView mTextView;
    private final Runnable mRetryTask;
    private final ImageView mImageView;
    private final View mTitle;

    public GloadingLoadingStatusView(Context context, Runnable retryTask) {
        super(context);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.view_gloading_loading_status, this, true);
        mImageView = findViewById(R.id.image);
        mTextView = findViewById(R.id.text);
        mTitle = findViewById(R.id.title);

        this.mRetryTask = retryTask;
    }

    //是否显示文本
    public void setMsgViewVisibility(boolean visible) {
        mTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    //是否显示Title
    public void setTitleBarVisibility(boolean visible) {
        mTitle.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setStatus(int status, String msg) {
        boolean show = true;  //是否展示这个布局

        OnClickListener onClickListener = null;
        int image = R.drawable.anim_gloading;
        mImageView.setVisibility(VISIBLE);
        String str = "Loading...";

        switch (status) {
            case STATUS_LOAD_SUCCESS:
                mImageView.setVisibility(GONE);
                show = false;
                break;

            case STATUS_LOADING:
            case STATUS_NORMAL:
                mImageView.setVisibility(VISIBLE);
                str = "Loading...";
                break;

            case STATUS_LOAD_FAILED:
                mImageView.setVisibility(VISIBLE);
                //是否需要加网络状态判断
//                boolean networkConn = NetWorkUtil.isConnected(getContext());
//                if (!networkConn) {
//                    str = "NetWork Error";
//                    image = R.mipmap.page_icon_network;
//                } else {
                str = TextUtils.isEmpty(msg) ? "Load Error" : msg;
                image = R.mipmap.loading_error;
//                }

                onClickListener = this;
                break;

            case STATUS_EMPTY_DATA:
                mImageView.setVisibility(VISIBLE);
                str = "No Data";
                image = R.mipmap.loading_error;
                break;

            default:
                break;
        }

        mImageView.setImageResource(image);
        setOnClickListener(onClickListener);

        mTextView.setText(str);
        setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (mRetryTask != null) {

            mRetryTask.run();
        }
    }

}
