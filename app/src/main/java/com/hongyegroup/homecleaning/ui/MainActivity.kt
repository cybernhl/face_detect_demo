package com.hongyegroup.homecleaning.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.TextureView
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.guadou.cs_cptservices.base.activity.YYBaseVMActivity
import com.guadou.lib_baselib.ext.commContext
import com.guadou.lib_baselib.utils.CommUtils
import com.guadou.lib_baselib.utils.Log.YYLogUtils
import com.hongyegroup.homecleaning.R
import com.hongyegroup.homecleaning.device.DeviceManager
import com.hongyegroup.homecleaning.face.faceserver.FaceServer
import com.hongyegroup.homecleaning.face.utils.ConfigUtil
import com.hongyegroup.homecleaning.face.widget.FaceRectView
import com.hongyegroup.homecleaning.mvp.MainViewModel

/**
 * 首页
 */
class MainActivity : YYBaseVMActivity<MainViewModel>(), ViewTreeObserver.OnGlobalLayoutListener {

    private lateinit var previewView: TextureView
    private lateinit var faceRectView: FaceRectView
    private lateinit var flLoadingWaitView: FrameLayout
    private lateinit var progressLoading: ProgressBar

    companion object {
        fun startInstance() {
            val context = CommUtils.getContext()
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun getLayoutIdRes(): Int = R.layout.activity_main

    override fun startObserve() {
        //当前屏幕的状态切换
        mViewModel.curStateLD.observe(this) {
            when (it) {
                1 -> {
                    flLoadingWaitView.visibility = View.GONE
                }
                2 -> {
                    flLoadingWaitView.visibility = View.VISIBLE
                    progressLoading.visibility = View.VISIBLE
                }
                3 -> {
                    flLoadingWaitView.visibility = View.VISIBLE
                    progressLoading.visibility = View.GONE
                }
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        //保持亮屏
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // Activity启动后就锁定为启动时的方向
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        //本地人脸库初始化 - 此服务用于人脸的注册与搜索
        FaceServer.getInstance().init(this)

        initViews()
    }


    private fun initViews() {
        previewView = findViewById(R.id.single_camera_texture_preview)
        faceRectView = findViewById(R.id.single_camera_face_rect_view)
        flLoadingWaitView = findViewById(R.id.fl_loading_wait)
        progressLoading = findViewById(R.id.progress_load)

        //在布局结束后才做初始化操作
        previewView.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        previewView.viewTreeObserver.removeOnGlobalLayoutListener(this)

        //初始化Camera资源和三个任务池
        mViewModel.initEngine()
        mViewModel.initCamera(mActivity, previewView, faceRectView)
    }

    //销毁资源
    override fun onDestroy() {
        YYLogUtils.w("MainActivity-onDestroy")
        FaceServer.getInstance().unInit()

        if (mViewModel.cameraHelper != null) {
            mViewModel.cameraHelper?.release()
            mViewModel.cameraHelper = null
        }

        mViewModel.unInitEngine()

        mViewModel.getFeatureDelayedDisposables.clear()
        mViewModel.delayFaceTaskCompositeDisposable.clear()

        if (mViewModel.faceHelper != null) {
            ConfigUtil.setTrackedFaceCount(commContext(), mViewModel.faceHelper?.trackedFaceCount ?: 0)
            mViewModel.faceHelper?.release()
            mViewModel.faceHelper = null
        }

        //设备控制状态重置
        DeviceManager.closeAllLight()
        mViewModel.releseAllHandler()

        super.onDestroy()
    }

    //重启页面处理Camera
    override fun onRestart() {
        super.onRestart()

        if (mViewModel.cameraHelper != null) {
            if (mViewModel.cameraHelper!!.isStopped) {
                mViewModel.openWhiteLight()
                mViewModel.cameraHelper?.start()
            }
        } else {
            YYLogUtils.w("cameraHelper都是空了!!")
        }
    }

}