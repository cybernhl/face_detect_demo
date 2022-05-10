package com.hongyegroup.homecleaning.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.dawnyou.functiontest.temperature.OnOpenSerialPortListener
import com.dawnyou.functiontest.temperature.OnSerialPortDataListener
import com.dawnyou.functiontest.temperature.SerialPortManager
import com.guadou.cs_cptservices.base.activity.YYBaseVMActivity
import com.guadou.cs_cptservices.popup.DebugInfoPopup
import com.guadou.lib_baselib.engine.extRequestPermission
import com.guadou.lib_baselib.utils.CommUtils
import com.guadou.lib_baselib.utils.StatusBarUtils
import com.hongyegroup.homecleaning.R
import com.hongyegroup.homecleaning.device.DeviceManager
import com.hongyegroup.homecleaning.mvp.LoginViewModel
import java.io.File


/**
 * 登录页面
 */
class LoginActivity : YYBaseVMActivity<LoginViewModel>() {

    private lateinit var textTemp: TextView

    companion object {
        fun startInstance() {
            val context = CommUtils.getContext()
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun getLayoutIdRes(): Int = R.layout.activity_login


    override fun startObserve() {

    }

    override fun init(savedInstanceState: Bundle?) {
        //是否需要沉浸式状态栏
        StatusBarUtils.immersive(this)

        //设置黑色状态栏文本颜色
        setStatusBarWhiteText()

        textTemp = findViewById(R.id.textTemp)

//        //测试SDK是否能使用
//        DebugInfoPopup.popupText(DeviceManager.getDeviceInfo(), mActivity)

        //Face-SDK
        activeFaceSdk()
    }

    //激活Face-SDK
    private fun activeFaceSdk() {
        mViewModel.activeEngine()
    }

    fun gotoHome(view: View) {
        extRequestPermission(android.Manifest.permission.CAMERA, android.Manifest.permission.READ_PHONE_STATE, block = {
            MainActivity.startInstance()
//            finish()
        })
    }

    fun closeBackLight(view: View) {
        //关闭背光-10秒再开
        DeviceManager.closeBackLight()

        view.postDelayed({
            DeviceManager.openBackLight()

        }, 5000)
    }

    fun openLight(view: View) {
        DeviceManager.openWhiteLight()
    }

    fun openRedLight(view: View) {
        DeviceManager.openRedLight()
    }

    fun openGreenLight(view: View) {
        DeviceManager.openGreenLight()
    }

    fun closeLight(view: View) {
        DeviceManager.closeAllLight()
    }

    private lateinit var mSerialPortManager: SerialPortManager
    var cmd = byteArrayOf(-91, 85, 1, -5)  //开启通信的指令

    var mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(paramAnonymousMessage: Message) {
            if (paramAnonymousMessage.what == 1) {
                val localTextView: TextView = this@LoginActivity.textTemp
                val localStringBuilder = StringBuilder()
                localStringBuilder.append(paramAnonymousMessage.obj)
                localStringBuilder.append("摄氏度")
                localTextView.text = localStringBuilder.toString()
            }
            super.handleMessage(paramAnonymousMessage)
        }
    }

    //获取温度
    fun getTemplete(view: View) {
//        val result = AziotManager.getInstance().getUartPath("1")

        mSerialPortManager = SerialPortManager()
        mSerialPortManager
            .setOnOpenSerialPortListener(object : OnOpenSerialPortListener {
                override fun onFail(paramFile: File?, paramStatus: OnOpenSerialPortListener.Status) {
                    Toast.makeText(mContext,paramStatus.toString(),Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(paramFile: File) {

                }
            })
                //设置串口的数据通信回调
            .setOnSerialPortDataListener(object : OnSerialPortDataListener {
                override fun onDataReceived(paramAnonymousArrayOfByte: ByteArray) {
                    //解析返回的数据转换为摄氏度
                    val i = paramAnonymousArrayOfByte[3]
                    val f = (paramAnonymousArrayOfByte[2] + i * 255 + 20) / 100.0f
                    val message = Message.obtain()
                    message.obj = java.lang.Float.valueOf(f)
                    message.what = 1
                    mHandler.sendMessage(message)
                }

                override fun onDataSent(paramArrayOfByte: ByteArray?) {

                }
            })
            .openSerialPort(File("dev/ttyS3"), 115200)  //打开指定串口,输入端口号和比特率

        mSerialPortManager.beginBytes(cmd)  //开启读取
    }

    override fun onDestroy() {
        super.onDestroy()
        //关闭串口释放资源
        mSerialPortManager.stopBytes()
        mSerialPortManager.closeSerialPort()
    }

    //本地人员人脸注册
    fun registLocal(view: View) {
        mViewModel.tryRegisterUser()
    }

    //清除全部注册的人脸数据
    fun clearRegist(view: View) {
        mViewModel.clearRegistedFaces(mActivity)
    }


}