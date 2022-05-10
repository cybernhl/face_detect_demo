package com.guadou.lib_baselib.ext

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.guadou.basiclib.R


/**
 * 吐司工具类-还是自己的布局还是自己的icon
 */
 object ToastUtils {

    private const val LENGTH_SHORT = Toast.LENGTH_SHORT

    fun makeText(context: Context?, str: String?) {
        if (null == context || TextUtils.isEmpty(str)) {
            return
        }

        val view = View.inflate(context, R.layout.custom_toast_normal_view, null)
        val toast = Toast(context)
        toast.view = view
        val tvContent = view.findViewById<View>(R.id.tv_content) as TextView
        tvContent.text = str
        toast.duration = LENGTH_SHORT
        toast.show()

    }

    fun makeText(context: Context?, resID: Int = 0) {
        if (null == context) {
            return
        }
        val view = View.inflate(context, R.layout.custom_toast_normal_view, null)
        val toast = Toast(context)
        toast.view = view
        val tvContent = view.findViewById<View>(R.id.tv_content) as TextView
        tvContent.setText(resID)
        toast.duration = LENGTH_SHORT
        toast.show()


    }

    fun showSuccessText(context: Context?, resID: Int = 0) {
        if (null == context) {
            return
        }

        val view = View.inflate(context, R.layout.custom_toast_bg_view, null)
        val toast = Toast(context)
        toast.view = view
        val tvContent = view.findViewById<View>(R.id.tv_content) as TextView
        tvContent.setText(resID)
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    fun showSuccessText(context: Context?, msg: String?) {
        if (null == context) {
            return
        }
        val view = View.inflate(context, R.layout.custom_toast_bg_view, null)
        val toast = Toast(context)
        toast.view = view
        val tvContent = view.findViewById<View>(R.id.tv_content) as TextView
        tvContent.text = msg
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()


    }

    fun showFailText(context: Context?, resID: Int = 0) {
        if (null == context) {
            return
        }

        val view = View.inflate(context, R.layout.custom_toast_bg_view, null)
        val toast = Toast(context)
        toast.view = view
        val tvContent = view.findViewById<View>(R.id.tv_content) as TextView
        val drawable = context.resources.getDrawable(R.mipmap.icon_shibai)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        tvContent.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        tvContent.setText(resID)
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()

    }

    fun showFailText(context: Context?, msg: String?) {
        if (null == context) {
            return
        }

        val view = View.inflate(context, R.layout.custom_toast_bg_view, null)
        val toast = Toast(context)
        toast.view = view
        val tvContent = view.findViewById<View>(R.id.tv_content) as TextView
        val drawable = context.resources.getDrawable(R.mipmap.icon_shibai)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        tvContent.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        tvContent.text = msg
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()


    }

    fun showToastCenter(context: Context?, msg: String?) {
        if (null == context) {
            return
        }

        val view = View.inflate(context, R.layout.custom_toast_bg_view, null)
        val toast = Toast(context)
        toast.view = view
        val tvContent = view.findViewById<View>(R.id.tv_content) as TextView
        tvContent.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        tvContent.text = msg
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()

    }

}
