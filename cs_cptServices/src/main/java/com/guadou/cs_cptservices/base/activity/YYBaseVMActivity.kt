package com.guadou.cs_cptservices.base.activity

import android.os.Bundle
import com.guadou.cs_cptservices.base.YYLifecycleObserver
import com.guadou.lib_baselib.base.activity.BaseVMActivity
import com.guadou.lib_baselib.base.vm.BaseViewModel

abstract class YYBaseVMActivity<VM : BaseViewModel> : BaseVMActivity<VM>() {

    lateinit var lifecycleObserver: YYLifecycleObserver

    override fun onCreate(savedInstanceState: Bundle?) {

        //注入生命周期回调
        lifecycleObserver = YYLifecycleObserver(this)
        lifecycle.addObserver(lifecycleObserver)

        super.onCreate(savedInstanceState)
    }

}