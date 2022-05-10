package com.hongyegroup.homecleaning.app

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Process
import android.webkit.WebView
import com.guadou.lib_baselib.base.BaseApplication
import com.hongyegroup.homecleaning.device.DeviceManager
import com.hongyegroup.homecleaning.face.faceserver.FaceServer

/**
 * 项目初始化Application
 */
class MyApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()

//        initBugly()

        //Device-SDK初始化
//        DeviceManager.DeviceSDKInit(this)

        //Face库初始化 本地人脸库初始化 - 此服务用于人脸的注册与搜索
        FaceServer.getInstance().init(this)
    }

//    /**
//     * 初始化Bugly，捕获到异常信息上传到Bugly平台
//     */
//    private fun initBugly() {
//
//        CrashReport.initCrashReport(applicationContext, "afcd34c05f", true)
//        val debug: Boolean = BuildConfig.DEBUG
//        //能捕获自己的异常，上报给bugly
//        Cockroach.install(this, object : ExceptionHandler() {
//            override fun onUncaughtExceptionHappened(thread: Thread, throwable: Throwable) {
//                YYLogUtils.e("--->onUncaughtExceptionHappened:" + thread + "<--->" + throwable.message)
//                Handler(Looper.getMainLooper()).post {
//                    if (debug) {
//                        //如果是测试环境那么吐司错误信息
//                        toast(throwable.toString())
//                    }
//                    //上报错误信息
//                    CrashReport.postCatchedException(throwable)
//                }
//            }
//
//            override fun onBandageExceptionHappened(throwable: Throwable) {
//                throwable.printStackTrace() //打印警告级别log，该throwable可能是最开始的bug导致的，无需关心
//                YYLogUtils.e("Cockroach Worked:" + throwable.message)
//                if (debug) {
//                    //如果是测试环境那么吐司错误信息
//                    toast(throwable.toString())
//                }
//                //上报错误信息
//                CrashReport.postCatchedException(throwable)
//            }
//
//            override fun onEnterSafeMode() {
//                YYLogUtils.e("Cockroach onEnterSafeMode")
//            }
//
//            override fun onMayBeBlackScreen(e: Throwable) {
//                val thread = Looper.getMainLooper().thread
//                Log.e("AndroidRuntime", "--->onUncaughtExceptionHappened:$thread<---", e)
//                //黑屏时建议直接杀死app
//                ActivityManage.finishAllActivity()
//                exitProcess(0)
//            }
//        })
//    }

}