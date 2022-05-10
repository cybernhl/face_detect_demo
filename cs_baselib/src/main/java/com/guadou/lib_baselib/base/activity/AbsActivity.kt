package com.guadou.lib_baselib.base.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.guadou.lib_baselib.receiver.ConnectivityReceiver
import com.guadou.lib_baselib.utils.ActivityManage
import com.guadou.lib_baselib.utils.StatusBarUtils


/**
 * 最底层的Activity,不带MVP和MVVM,一般不用这个
 */
abstract class AbsActivity() : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    /**
     * 获取Context对象
     */
    protected lateinit var mActivity: Activity
    protected lateinit var mContext: Context

    abstract fun setContentView()

    abstract fun init(savedInstanceState: Bundle?)

    /**
     * 从intent中解析数据，具体子类来实现
     */
    open protected fun getDataFromIntent(intent: Intent) {}

    /**
     * 设置顶部状态栏的颜色（默认为白色背景-黑色文字）
     */
    protected fun setStatusBarColor(): Int {
        //如果状态栏文字能变黑那么背景设置为白色，否则返回背景灰色文本默认为白色
        return if (StatusBarUtils.setStatusBarBlackText(this)) {
            Color.WHITE
        } else {
            Color.parseColor("#B0B0B0")
        }
    }

    /**
     * 动态的设置状态栏颜色
     * 当颜色为白色的时候显示白底黑字
     * 其他颜色为其他颜色底白色字
     * 一般由子类重写
     */
    fun setStatusBarColor(color: Int) {

        if (color == Color.WHITE) {
            //变黑色文字成功
            if (StatusBarUtils.setStatusBarBlackText(this)) {
                StatusBarUtils.setColor(this, Color.WHITE)
            } else {
                StatusBarUtils.setColor(this, Color.parseColor("#B0B0B0"))
            }

        } else {

            //变为白色文字成功
            StatusBarUtils.setStatusBarWhiteText(this)
            StatusBarUtils.setColor(this, color)

        }
    }

    /**
     * 跳转页面
     */
    protected fun gotoActivity(clazz: Class<*>) {
        startActivity(Intent(mActivity, clazz))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()

        mActivity = this
        mContext = this.applicationContext

        //设置当前页面的顶部状态栏背景.
        StatusBarUtils.setColor(this, setStatusBarColor())

        //获取intent传递的数据
        if (intent != null) {
            getDataFromIntent(intent)
        }

        //设置竖屏展示
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        /** 管理Activity的栈  */
        ActivityManage.addActivity(this)

        if (needRegisterNetworkChangeObserver()) {
            ConnectivityReceiver.registerObserver(this)
            ConnectivityReceiver.registerAnnotationObserver(this)
        }

        init(savedInstanceState)
    }

    /**
     * 是否需要注册监听网络变换
     */
    open protected fun needRegisterNetworkChangeObserver(): Boolean {
        return false
    }


    /**
     * 只映射了onDestroy方法
     */
    override fun onDestroy() {
        super.onDestroy()

        ActivityManage.removeActivity(this)

        if (needRegisterNetworkChangeObserver()) {
            ConnectivityReceiver.unregisterObserver(this)
            ConnectivityReceiver.unregisterAnnotationObserver(this)
        }
    }

    /**
     * 设置应用的字体不随系统的字体大小而改变
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.fontScale != 1f)
        //非默认值    //当接收系统的字体大小改变的时候。重置为默认
            resources
        super.onConfigurationChanged(newConfig)
    }

    override fun getResources(): Resources {
        val res = super.getResources()
        if (res.configuration.fontScale != 1f) {//非默认值
            val newConfig = Configuration()
            newConfig.setToDefaults()//设置默认
            res.updateConfiguration(newConfig, res.displayMetrics)
        }
        return res
    }

    /**
     * 设置状态栏的文本颜色
     */
    protected fun setStatusBarWhiteText() {
        StatusBarUtils.setStatusBarWhiteText(this)
    }

    protected fun setStatusBarBlackText() {
        StatusBarUtils.setStatusBarBlackText(this)
    }

}
