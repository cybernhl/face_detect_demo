package com.guadou.lib_baselib.annotation;


import com.guadou.lib_baselib.utils.NetWorkUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于方法上面的，需要方法的容器注解和解注册
 * 可以直接把网络变换通知到具体的某一个方法
 */
@Target(ElementType.METHOD) //定义在方法上面的注解 ，和EventBus的方式类似
@Retention(RetentionPolicy.RUNTIME) //定义为运行时，在jvm运行的过程中通过反射获取到注解
public @interface NetWork {

    NetWorkUtil.NetworkType netWorkType() default NetWorkUtil.NetworkType.NETWORK_NO;
}
