package com.guadou.cs_cptservices.base

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.guadou.lib_baselib.view.LoadingDialogManager


/**
 * 全局的生命周期的监听
 */
class YYLifecycleObserver(private val activity: Activity) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun connectOnCreate() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun connectOnResume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun connectOnPause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun disConnectOnDestroy() {
        LoadingDialogManager.get().dismissLoading()
    }

}