package com.guadou.lib_baselib.utils.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 真正请求网络之前 拦截请求对象 判断并添加全局的请求头
 */
public class HeadInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();  //原始request对象

        Request.Builder builder = chain.request().newBuilder();  //request新的build对象，可以操作添加参数

        if (originalRequest.header("content-type") == null) {
            builder.addHeader("content-type", "application/x-www-form-urlencoded");
        }

        Request request = builder.build();

        return chain.proceed(request);
    }
}
