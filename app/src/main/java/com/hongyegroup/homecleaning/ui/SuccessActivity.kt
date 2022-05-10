package com.hongyegroup.homecleaning.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.arcsoft.face.FaceInfo
import com.guadou.lib_baselib.base.activity.BaseVMActivity
import com.guadou.lib_baselib.engine.extLoad
import com.guadou.lib_baselib.ext.commContext
import com.guadou.lib_baselib.ext.countDown
import com.guadou.lib_baselib.utils.BitmapUtils
import com.guadou.lib_baselib.utils.CheckUtil
import com.guadou.lib_baselib.utils.CommUtils
import com.guadou.lib_baselib.utils.Log.YYLogUtils
import com.hongyegroup.homecleaning.R
import com.hongyegroup.homecleaning.face.model.CompareResult
import com.hongyegroup.homecleaning.mvp.SuccessViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * 识别成功的页面
 */
class SuccessActivity : BaseVMActivity<SuccessViewModel>() {

    private var mResult: CompareResult? = null
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private lateinit var mTvUserId: TextView
    private lateinit var mTvUserName: TextView
    private lateinit var mIvUserAvatar: ImageView

    companion object {
        var mFaceInfo: FaceInfo? = null
        var mNv21: ByteArray? = null

        fun startInstance(result: CompareResult, nv21: ByteArray?, faceInfo: FaceInfo?, width: Int, height: Int) {
            mFaceInfo = faceInfo
            mNv21 = nv21

            val context = CommUtils.getContext()
            val intent = Intent(context, SuccessActivity::class.java)
            intent.putExtra("result", result)
            intent.putExtra("width", width)
            intent.putExtra("height", height)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun getDataFromIntent(intent: Intent) {
        mResult = intent.getSerializableExtra("result") as CompareResult?
        mWidth = intent.getIntExtra("width", 0)
        mHeight = intent.getIntExtra("height", 0)
    }

    override fun getLayoutIdRes(): Int = R.layout.activity_success


    @ExperimentalCoroutinesApi
    override fun init(savedInstanceState: Bundle?) {
        initViews()
        popupResult()
        startCountDown()
    }


    @ExperimentalCoroutinesApi
    private fun startCountDown() {
        countDown(3, end = {
            BitmapUtils.getInstance(commContext()).deleteBitmapCacheDir()
            finish()
        })
    }

    private fun popupResult() {
        if (mResult != null && mFaceInfo != null) {
            mTvUserId.text = mResult!!.userId
            mTvUserName.text = mResult!!.userName

            mViewModel.handleBitmap(mNv21, mFaceInfo, mWidth, mHeight, mResult!!.userName).observe(this) {
                if (!CheckUtil.isEmpty(it)) {
                    YYLogUtils.w("保存成功的图片为：$it")
                    mIvUserAvatar.extLoad(it)
                }
            }
        }

    }

    private fun initViews() {
        mTvUserId = findViewById(R.id.tv_user_id)
        mTvUserName = findViewById(R.id.tv_user_name)
        mIvUserAvatar = findViewById(R.id.iv_user_avatar)
    }

    override fun startObserve() {

    }

}