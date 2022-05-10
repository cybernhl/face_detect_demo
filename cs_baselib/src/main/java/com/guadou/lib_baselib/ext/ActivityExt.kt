package com.guadou.lib_baselib.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * Activity相关
 */

//内联函数+标注泛型 = 泛型实例化
inline fun <reified T> Fragment.gotoActivity(
    flag: Int = -1,
    bundle: Array<out Pair<String, Any?>>? = null
) {
    activity?.gotoActivity<T>(flag, bundle)
}

inline fun <reified T> Fragment.gotoActivity(
    flag: Int = -1,
    bundle: Array<out Pair<String, Any?>>? = null,
    requestCode: Int = -1
) {
    activity?.gotoActivityForResult<T>(flag, bundle, requestCode)
}

inline fun <reified T> Context.gotoActivity(
    flag: Int = -1,
    bundle: Array<out Pair<String, Any?>>? = null
) {
    val intent = Intent(this, T::class.java).apply {
        if (flag != -1) {
            this.addFlags(flag)
        }
        if (this !is Activity) {
            this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (bundle != null) putExtras(bundle.toBundle()!!)
    }
    startActivity(intent)
}

inline fun <reified T> View.gotoActivity(
    flag: Int = -1,
    bundle: Array<out Pair<String, Any?>>? = null
) {
    context.gotoActivity<T>(flag, bundle)
}

inline fun <reified T> View.gotoActivityForResult(
    flag: Int = -1,
    bundle: Array<out Pair<String, Any?>>? = null,
    requestCode: Int = -1
) {
    (context as Activity).gotoActivityForResult<T>(flag, bundle, requestCode)
}

inline fun <reified T> Activity.gotoActivityForResult(
    flag: Int = -1,
    bundle: Array<out Pair<String, Any?>>? = null,
    requestCode: Int = -1
) {
    val intent = Intent(this, T::class.java).apply {
        if (flag != -1) {
            this.addFlags(flag)
        }
        if (bundle != null) putExtras(bundle.toBundle()!!)
    }
    startActivityForResult(intent, requestCode)
}
