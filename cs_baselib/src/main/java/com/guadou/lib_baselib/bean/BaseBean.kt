package com.guadou.testxiecheng.base

data class BaseBean<out T>(val code: Int, val message: String, val data: T)