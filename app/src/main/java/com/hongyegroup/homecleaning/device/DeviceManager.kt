package com.hongyegroup.homecleaning.device

import android.app.smdt.SmdtManager
import android.content.Context
import com.aziot.library.AziotManager

/**
 * @auther Newki
 * @date 2022/2/12
 * @description 控制硬件设备管理类
 */
object DeviceManager {

    private val aziot by lazy { AziotManager.getInstance() }
    var isScreenOff = false

    //SDK初始化
    @JvmStatic
    fun DeviceSDKInit(context: Context) {
        aziot.init(context)
    }

    //打开白色补光灯
    @JvmStatic
    fun openWhiteLight() {
        aziot.setLedLighted(SmdtManager.LED_WHITE, true)
    }

    //打开红色补光灯
    @JvmStatic
    fun openRedLight() {
        aziot.setLedLighted(SmdtManager.LED_RED, true)
    }

    //打开绿色补光灯
    @JvmStatic
    fun openGreenLight() {
        aziot.setLedLighted(SmdtManager.LED_GREEN, true)
    }

    //关闭所有的补光灯
    @JvmStatic
    fun closeAllLight() {
        aziot.apply {
            setLedLighted(SmdtManager.LED_WHITE, false)
            setLedLighted(SmdtManager.LED_RED, false)
            setLedLighted(SmdtManager.LED_GREEN, false)
        }
    }

    //关闭屏幕背光
    @JvmStatic
    fun closeBackLight() {
        aziot.setBrightness(255)  //纯黑
        aziot.aziotSetLcdBackLight(1)
        isScreenOff = true
    }

    //打开屏幕背光
    @JvmStatic
    fun openBackLight() {
        aziot.setBrightness(100)  //默认
        aziot.aziotSetLcdBackLight(0)
        isScreenOff = false
    }

    //打印信息-测试SDK是否能使用
    @JvmStatic
    fun getDeviceInfo(): String {
        val aziot = AziotManager.getInstance()
        val apiVersion = aziot.aziotGetAPIVersion()
        val model = aziot.androidModel
        val version = aziot.androidVersion
        val memory = aziot.runningMemory
        val storageMemory = aziot.internalStorageMemory
        val results0 = aziot.aziotGetCameraVidPid(0)
        val results1 = aziot.aziotGetCameraVidPid(1)


        return "apiVersion :$apiVersion model:$model version:$version memory:$memory storageMemory:$storageMemory" +
                " result0:$results0 result1:$results1 "
    }

}