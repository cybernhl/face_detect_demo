package com.guadou.lib_baselib.bean


/**
 * 加载状态的bean
 */
data class LoadAction(var action: Int, var message: String? = "") {

    companion object {
        const val STATE_NORMAL = 141
        const val STATE_LOADING = 142
        const val STATE_SUCCESS = 143
        const val STATE_ERROR = 144
        const val STATE_NO_DATA = 145
        const val STATE_PROGRESS = 146
        const val STATE_HIDE_PROGRESS = 147
    }
}
