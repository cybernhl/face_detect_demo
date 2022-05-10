package com.guadou.lib_baselib.base.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.guadou.lib_baselib.receiver.ConnectivityReceiver
import com.guadou.lib_baselib.utils.StatusBarUtils


/**
 * 普通的Fragment，基类Fragment
 */

abstract class AbsFragment : Fragment(), ConnectivityReceiver.ConnectivityReceiverListener {

    /**
     * 获取Context对象
     */
    protected lateinit var mActivity: Activity
    protected lateinit var mContext: Context


    abstract fun setContentView(container: ViewGroup?): View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = requireActivity()
        mContext = requireActivity().applicationContext

        if (needRegisterNetworkChangeObserver()) {
            ConnectivityReceiver.registerObserver(this)
            ConnectivityReceiver.registerAnnotationObserver(this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return transformRootView(setContentView(container))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    //用于转换根数图View(可以对其做一些别的操作,例如加入GLoading)
    protected open fun transformRootView(view: View): View {
        return view
    }

    protected open fun initViews(view: View) {
    }

    /**
     * 只映射了onDestroy方法 取消任务
     */
    override fun onDestroy() {
        super.onDestroy()

        if (needRegisterNetworkChangeObserver()) {
            ConnectivityReceiver.unregisterObserver(this)
            ConnectivityReceiver.unregisterAnnotationObserver(this)
        }
    }

    /**
     * 是否需要注册监听网络变换
     */
    open protected fun needRegisterNetworkChangeObserver(): Boolean {
        return false
    }

    /**
     * 设置状态栏的文本颜色
     */
    protected fun setStatusBarWhiteText() {
        StatusBarUtils.setStatusBarWhiteText(requireActivity())
    }

    protected fun setStatusBarBlackText() {
        StatusBarUtils.setStatusBarBlackText(requireActivity())
    }

}
