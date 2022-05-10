package com.guadou.lib_baselib.utils.interceptor;


import android.util.Log;

import com.guadou.basiclib.BuildConfig;
import com.guadou.lib_baselib.utils.Log.YYLogUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 自定义OKhttp3的拦截器，不用官方的也可以拦截打印日志
 */

public class LoggingInterceptor implements Interceptor {

    private boolean debug = BuildConfig.DEBUG;

    @Override
    public Response intercept(Chain chain) throws IOException {
        //这个chain里面包含了request和response，所以你要什么都可以从这里拿
        Request request = chain.request();
        long t1 = System.nanoTime();//请求发起的时间

        if (debug)
            Log.w("Request,", String.format("发送请求 %s on %s%n%s", request.url(), request, request.headers()));

        Response response = chain.proceed(request);
        long t2 = System.nanoTime();//收到响应的时间
        ResponseBody responseBody = response.peekBody(1024 * 1024);


        if (debug)
            Log.w("Response,", String.format("接收响应: [%s]  %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        /*  自己封装的Log打印，长度无限制，可以专门输出json或xml  **/
        YYLogUtils.json(responseBody.string());

        return response;
    }
}
