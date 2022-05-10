package com.guadou.testxiecheng.base

/**
 * 自定义网络返回结果
 * 二次处理之后赋值给ViewModel中展示与判断
 */
sealed class OkResult<out T : Any> {

    data class Success<out T : Any>(val data: T) : OkResult<T>()
    data class Error(val exception: Exception) : OkResult<Nothing>()

    //检测成功与失败
    fun checkResult(success: (T) -> Unit, error: (String?) -> Unit) {
        if (this is Success) {
            success(data)
        } else if (this is Error) {
            error(exception.message)
        }
    }

    //只是检测成功
    fun checkSuccess(success: (T) -> Unit) {
        if (this is Success) {
            success(data)
        }
    }


    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}