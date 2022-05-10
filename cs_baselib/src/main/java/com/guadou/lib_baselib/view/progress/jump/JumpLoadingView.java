package com.guadou.lib_baselib.view.progress.jump;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.guadou.basiclib.R;

/**
 * 上下跳动的LoadingView，控制形状的改版
 * 原形-矩形-三角形变换
 */
public class JumpLoadingView extends LinearLayout {

    private static final int ANIMATION_DURATION = 500;

    private static final float FACTOR = 1.2f;

    private static float mDistance = 200;

    private ShapeLoadingView mShapeLoadingView;

    private ImageView mIndicationIm;

    private String mLoadText;
    private AnimatorSet mUpAnimatorSet;
    private AnimatorSet mDownAnimatorSet;

    private boolean mStopped = false;

    private int mDelay;

    public JumpLoadingView(Context context) {
        super(context);
        init(context, null);
    }

    public JumpLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public JumpLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public JumpLoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        setOrientation(VERTICAL);
        mDistance = dip2px(context, 54f);
        LayoutInflater.from(context).inflate(R.layout.load_view, this, true);
        mShapeLoadingView = findViewById(R.id.shapeLoadingView);
        mIndicationIm = findViewById(R.id.indication);

        mIndicationIm.setScaleX(0.2f);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JumpLoadingView);
        mDelay = typedArray.getInteger(R.styleable.JumpLoadingView_delay, 80);
        typedArray.recycle();

//        setLoadingText(loadText);
    }

    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getVisibility() == VISIBLE) {
            startLoading(mDelay);
        }
    }

    private Runnable mFreeFallRunnable = new Runnable() {
        @Override
        public void run() {
            mShapeLoadingView.setTranslationY(0f);
            mShapeLoadingView.setRotation(180f);
            mIndicationIm.setScaleX(0.2f);
            mStopped = false;
            freeFall();
        }
    };

    public void startLoading(long delay) {
        if (mDownAnimatorSet != null && mDownAnimatorSet.isRunning()) {
            return;
        }
        this.removeCallbacks(mFreeFallRunnable);
        if (delay > 0) {
            this.postDelayed(mFreeFallRunnable, delay);
        } else {
            this.post(mFreeFallRunnable);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLoading();
    }

    public void stopLoading() {
        mStopped = true;
        if (mUpAnimatorSet != null) {
            if (mUpAnimatorSet.isRunning()) {
                mUpAnimatorSet.cancel();
            }
            mUpAnimatorSet.removeAllListeners();
            for (Animator animator : mUpAnimatorSet.getChildAnimations()) {
                animator.removeAllListeners();
            }
            mUpAnimatorSet = null;
        }
        if (mDownAnimatorSet != null) {
            if (mDownAnimatorSet.isRunning()) {
                mDownAnimatorSet.cancel();
            }
            mDownAnimatorSet.removeAllListeners();
            for (Animator animator : mDownAnimatorSet.getChildAnimations()) {
                animator.removeAllListeners();
            }
            mDownAnimatorSet = null;
        }
        this.removeCallbacks(mFreeFallRunnable);
    }

    @Override
    public void setVisibility(int visibility) {
        this.setVisibility(visibility, mDelay);
    }

    public void setVisibility(int visibility, int delay) {
        super.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            startLoading(delay);
        } else {
            stopLoading();
        }
    }

    public void setDelay(int delay) {
        mDelay = delay;
    }

    public int getDelay() {
        return mDelay;
    }

    /**
     * 上抛
     */
    public void upThrow() {

        if (mUpAnimatorSet == null) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mShapeLoadingView, "translationY", mDistance, 0);
            ObjectAnimator scaleIndication = ObjectAnimator.ofFloat(mIndicationIm, "scaleX", 1f, 0.2f);

            ObjectAnimator objectAnimator1 = null;
            switch (mShapeLoadingView.getShape()) {
                case SHAPE_RECT:

                    objectAnimator1 = ObjectAnimator.ofFloat(mShapeLoadingView, "rotation", 0, 180);

                    break;
                case SHAPE_CIRCLE:
                    objectAnimator1 = ObjectAnimator.ofFloat(mShapeLoadingView, "rotation", 0, 180);

                    break;
                case SHAPE_TRIANGLE:

                    objectAnimator1 = ObjectAnimator.ofFloat(mShapeLoadingView, "rotation", 0, 180);

                    break;
            }

            mUpAnimatorSet = new AnimatorSet();
            mUpAnimatorSet.playTogether(objectAnimator, objectAnimator1, scaleIndication);

            mUpAnimatorSet.setDuration(ANIMATION_DURATION);
            mUpAnimatorSet.setInterpolator(new DecelerateInterpolator(FACTOR));

            mUpAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!mStopped) {
                        freeFall();
                    }

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        mUpAnimatorSet.start();


    }

    /**
     * 下落
     */
    public void freeFall() {

        if (mDownAnimatorSet == null) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mShapeLoadingView, "translationY", 0, mDistance);
            ObjectAnimator scaleIndication = ObjectAnimator.ofFloat(mIndicationIm, "scaleX", 0.2f, 1f);

            mDownAnimatorSet = new AnimatorSet();
            mDownAnimatorSet.playTogether(objectAnimator, scaleIndication);
            mDownAnimatorSet.setDuration(ANIMATION_DURATION);
            mDownAnimatorSet.setInterpolator(new AccelerateInterpolator(FACTOR));
            mDownAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!mStopped) {
                        mShapeLoadingView.changeShape();
                        upThrow();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        mDownAnimatorSet.start();


    }

}
