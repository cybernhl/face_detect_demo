package com.guadou.lib_baselib.view.gloading;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.guadou.basiclib.R;
import com.guadou.lib_baselib.view.shimmer.ShimmerLayout;

import static com.guadou.lib_baselib.view.gloading.Gloading.STATUS_LOADING;
import static com.guadou.lib_baselib.view.gloading.Gloading.STATUS_LOAD_SUCCESS;
import static com.guadou.lib_baselib.view.gloading.Gloading.STATUS_LOAD_FAILED;
import static com.guadou.lib_baselib.view.gloading.Gloading.STATUS_EMPTY_DATA;
import static com.guadou.lib_baselib.view.gloading.Gloading.STATUS_NORMAL;

/**
 * 占位布局的动画
 */
@SuppressLint("ViewConstructor")
public class GloadingPlaceHolderView extends LinearLayout implements View.OnClickListener {

    public static String NEED_LOADING_STATUS_MAGRIN_TITLE = "loading_status_magrin_title";  //是否需要顶部的margin
    private final TextView mTextView;
    private final ImageView mImageView;
    private final ViewGroup mllErrorBox, mFlPlaceholderBox;
    private final Runnable mRetryTask;
    private final View mTitle;

    public GloadingPlaceHolderView(@NonNull Context context, int layoutRes, Runnable retryTask) {
        super(context);
        this.mRetryTask = retryTask;
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        LayoutInflater.from(context).inflate(R.layout.view_gloading_placeholder_view, this, true);

        mllErrorBox = findViewById(R.id.ll_error_box);
        mFlPlaceholderBox = findViewById(R.id.fl_placeholder_box);
        mImageView = findViewById(R.id.image);
        mTextView = findViewById(R.id.text);
        mTitle = findViewById(R.id.title);

        inflate(context, layoutRes, mFlPlaceholderBox);
    }

    public void setTitleBarVisibility(boolean visible) {
        mTitle.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setStatus(int status, String msg) {
        View.OnClickListener onClickListener = null;
        int image = R.drawable.anim_gloading;
        String str = "Loading...";
        View placeHolderFirstView = mFlPlaceholderBox.getChildAt(0);

        switch (status) {
            case STATUS_LOAD_SUCCESS:
                setVisibility(GONE);
                mFlPlaceholderBox.setVisibility(GONE);
                mllErrorBox.setVisibility(GONE);
                if (placeHolderFirstView instanceof ShimmerLayout) {
                    ((ShimmerLayout) placeHolderFirstView).stopShimmerAnimation();
                }

                break;

            case STATUS_NORMAL:
                setVisibility(VISIBLE);
                mFlPlaceholderBox.setVisibility(VISIBLE);
                mllErrorBox.setVisibility(GONE);
                break;

            case STATUS_LOADING:
                setVisibility(VISIBLE);
                mFlPlaceholderBox.setVisibility(VISIBLE);
                mllErrorBox.setVisibility(GONE);
                if (placeHolderFirstView instanceof ShimmerLayout) {
                    ((ShimmerLayout) placeHolderFirstView).startShimmerAnimation();
                }
                break;

            case STATUS_LOAD_FAILED:
                setVisibility(VISIBLE);
                mFlPlaceholderBox.setVisibility(GONE);
                mllErrorBox.setVisibility(VISIBLE);
                if (placeHolderFirstView instanceof ShimmerLayout) {
                    ((ShimmerLayout) placeHolderFirstView).stopShimmerAnimation();
                }

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
                setVisibility(VISIBLE);
                mFlPlaceholderBox.setVisibility(GONE);
                mllErrorBox.setVisibility(VISIBLE);
                if (placeHolderFirstView instanceof ShimmerLayout) {
                    ((ShimmerLayout) placeHolderFirstView).stopShimmerAnimation();
                }

                str = "No Data";
                image = R.mipmap.loading_error;
                break;

            default:
                break;
        }

        mImageView.setImageResource(image);
        mllErrorBox.setOnClickListener(onClickListener);

        mTextView.setText(str);
    }

    @Override
    public void onClick(View v) {
        if (mRetryTask != null) {

            mRetryTask.run();
        }
    }
}
