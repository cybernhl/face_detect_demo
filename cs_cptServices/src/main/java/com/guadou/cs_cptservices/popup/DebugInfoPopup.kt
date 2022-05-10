package com.guadou.cs_cptservices.popup

import android.app.Activity
import com.lxj.xpopup.XPopup


/**
 * @auther Newki
 * @date 2022/2/12
 * @description 测试打印信息弹窗
 */
object DebugInfoPopup {

    fun popupText(content: String, activity: Activity) {

        XPopup.Builder(activity)
            .asConfirm(
                "", content, "", "Ok", null, null, true
            ).show()
    }
}