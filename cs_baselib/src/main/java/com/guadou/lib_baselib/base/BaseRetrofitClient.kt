package com.guadou.lib_baselib.base

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.guadou.lib_baselib.utils.CommUtils
import com.guadou.lib_baselib.utils.NetWorkUtil
import com.guadou.lib_baselib.utils.interceptor.*
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * 基类的Retrofit对象
 */
abstract class BaseRetrofitClient {

    private var gson: Gson? = null
    private val cookieJar by lazy {
        //第三方Cookie管理工具
        PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(CommUtils.getContext()))
    }

    companion object {
        //静态常量
        private const val TIME_OUT = 20
    }

    private val client: OkHttpClient
        get() {
            val builder = OkHttpClient.Builder()

            //可以添加日志拦截和参数拦截
            builder
                .addInterceptor(LoggingInterceptor())
                .connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)

            if (needHttpCache()) {
                handleBuilder(builder)
            }

            return builder.build()
        }

    open protected fun needHttpCache(): Boolean = false

    /**
     * 处理缓存
     */
    private fun handleBuilder(builder: OkHttpClient.Builder) {

        val httpCacheDirectory = File(CommUtils.getContext().cacheDir, "responses") //Http缓存目录
        val cacheSize = 10 * 1024 * 1024L // 10 MiB
        val cache = Cache(httpCacheDirectory, cacheSize)     //Http缓存对象
        builder.cache(cache)
            .cookieJar(cookieJar)
            .addInterceptor { chain ->
                var request = chain.request()

                if (!NetWorkUtil.isAvailable(CommUtils.getContext())) {
                    request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build()
                }

                val response = chain.proceed(request)
                //有网络读取接口，没有网络展示缓存
                if (!NetWorkUtil.isAvailable(CommUtils.getContext())) {
                    val maxAge = 60 * 60
                    response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=$maxAge")
                        .build()
                } else {
                    val maxStale = 60 * 60 * 24 * 28 // tolerate 4-weeks stale
                    response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                        .build()
                }
                response
            }
    }

    fun <S> getService(serviceClass: Class<S>, baseUrl: String): S {
        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(buildGson()))
            .baseUrl(baseUrl)
            .build().create(serviceClass)
    }

    //容错处理
    private fun buildGson(): Gson {
        if (gson == null) {
            gson = GsonBuilder()
                .registerTypeAdapter(Int::class.java, IntDefaut0Adapter())
                .registerTypeAdapter(Double::class.java, DoubleDefault0Adapter())
                .registerTypeAdapter(Long::class.java, LongDefault0Adapter())
                .registerTypeAdapter(List::class.java, ArrayDefailtAdapter())
                .create()
        }
        return gson!!
    }
}
