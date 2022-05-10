package com.guadou.lib_baselib.engine

import com.guadou.lib_baselib.base.vm.BaseRepository
import com.guadou.testxiecheng.base.BaseBean
import com.guadou.testxiecheng.base.OkResult
import java.io.IOException

/**
 * 引擎类
 * 网络请求的封装
 */

//可用引擎切换 - 默认用于 Retrofit + suspend方式
suspend fun <T : Any> BaseRepository.extRequestHttp(call: suspend () -> BaseBean<T>): OkResult<T> {

    return try {

        val response = call()

        if (response.code == 200) {
            OkResult.Success(response.data)
        } else {
            OkResult.Error(IOException(response.message))
        }

    } catch (e: Exception) {

        e.printStackTrace()
        OkResult.Error(handleExceptionMessage(e))
    }

}
