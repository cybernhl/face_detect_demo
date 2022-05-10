package com.guadou.lib_baselib.ext

import android.content.Context
import android.content.SharedPreferences
import com.guadou.lib_baselib.utils.CommUtils


/**
 * SharedPreferences相关
 *
 * sp().getString("a", "default")
 * sp().getBoolean("b", false)
 *
sp().putString("abc","test demo")

sp().edit {
putString("a", "1")
putBoolean("b", true)
}

 * sp().clear()
}
 */

fun Any.SP(name: String = "app_config") =
    CommUtils.getContext().getSharedPreferences(name, Context.MODE_PRIVATE)

/**
 * 批处理
 */
fun SharedPreferences.edit(action: SharedPreferences.Editor.() -> Unit) {
    edit().apply { action() }.apply()
}

/**
 * 对象操作
 */
fun SharedPreferences.putObject(key: String, obj: Any?) {
    putString(key, obj?.toJson() ?: "")
}

inline fun <reified T> SharedPreferences.getObject(key: String): T? {
    val string = getString(key, null)
    if (string == null || string.isEmpty()) return null
    return getString(key, null)?.toBean<T>()
}


/**
 *  put系列
 */
fun SharedPreferences.putString(key: String, value: String) {
    edit { putString(key, value) }
}

fun SharedPreferences.putInt(key: String, value: Int) {
    edit { putInt(key, value) }
}

fun SharedPreferences.putBoolean(key: String, value: Boolean) {
    edit { putBoolean(key, value) }
}

fun SharedPreferences.putFloat(key: String, value: Float) {
    edit { putFloat(key, value) }
}

fun SharedPreferences.putLong(key: String, value: Long) {
    edit { putLong(key, value) }
}

fun SharedPreferences.putStringSet(key: String, value: Set<String>) {
    edit { putStringSet(key, value) }
}

fun SharedPreferences.remove(key: String) {
    edit { remove(key) }
}

fun SharedPreferences.clear() {
    edit { clear() }
}
