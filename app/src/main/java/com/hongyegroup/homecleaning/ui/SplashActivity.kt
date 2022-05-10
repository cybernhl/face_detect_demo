package com.hongyegroup.homecleaning.ui

import android.os.Bundle
import com.guadou.cs_cptservices.YYConstants
import com.guadou.cs_cptservices.base.activity.YYBaseVMActivity
import com.guadou.lib_baselib.base.vm.EmptyViewModel
import com.guadou.lib_baselib.ext.SP
import com.guadou.lib_baselib.utils.BitmapUtils
import com.guadou.lib_baselib.utils.CheckUtil
import com.guadou.lib_baselib.utils.CommUtils
import com.hongyegroup.homecleaning.R
import com.hongyegroup.homecleaning.device.DeviceManager


class SplashActivity : YYBaseVMActivity<EmptyViewModel>() {

    companion object {
        //起始页面延迟的时间
        const val SPLASH_DELAY_MILLISEC: Long = 500
    }

    override fun getLayoutIdRes(): Int = R.layout.activity_splash

    override fun startObserve() {

    }

    override fun init(savedInstanceState: Bundle?) {

        //清除BitmapUtil的缓存图片
        BitmapUtils.getInstance(mContext).deleteBitmapCacheDir()

        //设备控制状态重置
        DeviceManager.closeAllLight()

        //开启倒计时，还是直接启动
        navLoginOrMain()

    }

    private fun navLoginOrMain() {

        CommUtils.getHandler().postDelayed({

            if (CheckUtil.isEmpty(SP().getString(YYConstants.SP_KEY_TOKEN, ""))) {
                //登录
                LoginActivity.startInstance()
            } else {
                //首页
                MainActivity.startInstance()
            }

            finish()

        }, SPLASH_DELAY_MILLISEC)

    }


}