package com.guadou.lib_baselib.base.vm

import android.text.TextUtils
import com.google.gson.JsonParseException
import com.guadou.testxiecheng.base.BaseBean
import com.guadou.testxiecheng.base.OkResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class BaseRepository {

    //无异常处理 -> 一般不用这个，一旦报错会App崩溃
    suspend inline fun <T : Any> handleApiCall(call: suspend () -> BaseBean<T>): BaseBean<T> {
        return call.invoke()
    }

    //处理Http错误-内部再处理Api错误
    suspend fun <T : Any> handleErrorApiCall(call: suspend () -> OkResult<T>, errorMessage: String = ""): OkResult<T> {
        return try {
            call()
        } catch (e: Exception) {
            if (!TextUtils.isEmpty(errorMessage)) {
                OkResult.Error(IOException(errorMessage))
            } else {
                OkResult.Error(handleExceptionMessage(e))
            }
        }
    }

    //处理Api错误，例如403Token过期 ；把BaseBean的数据转换为自定义的Result数据
    suspend fun <T : Any> handleApiErrorResponse(
        response: BaseBean<T>,
        successBlock: (suspend CoroutineScope.() -> Unit)? = null,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null
    ): OkResult<T> {

        return coroutineScope {
            //执行挂起函数
            if (response.code == 200) {  //这里根据业务逻辑来 200 -1 等
                successBlock?.let { it() }
                OkResult.Success(response.data)
            } else {
                errorBlock?.let { it() }
                OkResult.Error(IOException(response.message))
            }
        }
    }


    //处理自定义错误消息
    fun handleExceptionMessage(e: Exception): IOException {
        return when (e) {
            is UnknownHostException -> IOException("Unable to access domain name, unknown domain name.")
            is JsonParseException -> IOException("Data parsing exception.")
            is HttpException -> IOException("The server is on business. Please try again later.")
            is ConnectException -> IOException("Network connection exception, please check the network.")
            is SocketException -> IOException("Network connection exception, please check the network.")
            is SocketTimeoutException -> IOException("Network connection timeout.")
            is RuntimeException -> IOException("Error running, please try again.")
            else -> IOException("unknown error.")
        }

    }

}