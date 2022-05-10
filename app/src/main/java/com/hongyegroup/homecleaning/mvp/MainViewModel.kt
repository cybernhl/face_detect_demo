package com.hongyegroup.homecleaning.mvp

import android.app.Activity
import android.graphics.Point
import android.graphics.Rect
import android.hardware.Camera
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.TextureView
import androidx.lifecycle.MutableLiveData
import com.arcsoft.face.*
import com.arcsoft.face.enums.DetectFaceOrientPriority
import com.arcsoft.face.enums.DetectMode
import com.guadou.lib_baselib.base.vm.BaseViewModel
import com.guadou.lib_baselib.ext.commContext
import com.guadou.lib_baselib.ext.toast
import com.guadou.lib_baselib.utils.CheckUtil
import com.guadou.lib_baselib.utils.CommUtils
import com.guadou.lib_baselib.utils.Log.YYLogUtils
import com.hongyegroup.homecleaning.R
import com.hongyegroup.homecleaning.device.DeviceManager
import com.hongyegroup.homecleaning.face.camera.CameraHelper
import com.hongyegroup.homecleaning.face.camera.CameraListener
import com.hongyegroup.homecleaning.face.face.*
import com.hongyegroup.homecleaning.face.faceserver.FaceServer
import com.hongyegroup.homecleaning.face.model.CompareResult
import com.hongyegroup.homecleaning.face.model.DrawInfo
import com.hongyegroup.homecleaning.face.model.FacePreviewInfo
import com.hongyegroup.homecleaning.face.utils.ConfigUtil
import com.hongyegroup.homecleaning.face.utils.DrawHelper
import com.hongyegroup.homecleaning.face.widget.FaceRectView
import com.hongyegroup.homecleaning.ui.SuccessActivity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit


class MainViewModel : BaseViewModel() {

    // =======================  Camera face begin ↓ =========================
    private val SIMILAR_THRESHOLD = 0.8f                             //人脸对比识别阈值
    private val MAX_DETECT_NUM = 80                                 //最大识别数量
    private val WAIT_LIVENESS_INTERVAL = 100L                       //当FR成功，活体未成功时，FR等待活体的时间
    private val FAIL_RETRY_INTERVAL = 1000L                      //失败重试间隔时间（ms）
    private val MAX_RETRY_TIME = 3                                //出错重试最大次数
    private val rgbCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT   //优先打开的摄像头，默认打开前置
    private val livenessDetect: Boolean = true                       //活体检测的开关

    var cameraHelper: CameraHelper? = null           //摄像头工具类
    private var drawHelper: DrawHelper? = null               //绘制人脸新工具类
    var faceHelper: FaceHelper? = null               //人脸检测工具类
    private var previewSize: Camera.Size? = null

    private lateinit var ftEngine: FaceEngine      //人脸检测引擎，用于预览帧人脸追踪
    private lateinit var frEngine: FaceEngine      //用于特征提取的引擎
    private lateinit var flEngine: FaceEngine      //活体检测引擎，用于预览帧人脸活体检测
    private var ftInitCode = -1                    //引擎状态码记录
    private var frInitCode = -1
    private var flInitCode = -1
    private val compareResultList: ArrayList<CompareResult> = arrayListOf()

    private val requestFeatureStatusMap = ConcurrentHashMap<Int, Int>()  //用于记录人脸识别相关状态
    private val extractErrorRetryMap = ConcurrentHashMap<Int, Int>()    // 用于记录人脸特征提取出错重试次数
    private val livenessMap = ConcurrentHashMap<Int, Int>()   //用于存储活体值
    private val livenessErrorRetryMap = ConcurrentHashMap<Int, Int>()   //用于存储活体检测出错重试次数

    val getFeatureDelayedDisposables = CompositeDisposable()
    val delayFaceTaskCompositeDisposable = CompositeDisposable()

    /**
     * 初始化摄像头预览
     */
    fun initCamera(activity: Activity, previewView: TextureView, faceRectView: FaceRectView) {

        /*
         * 人脸处理的监听回调,用于找出人脸，判断活体，对比人脸,共分三个引擎
         *  FT Engine  预览画面中查找出人脸
         *  FL Engine  判断指定的数据是否是活体
         *  FR Engine  人脸比对是否通过
         */
        val faceListener = object : FaceListener {
            override fun onFail(e: Exception?) {
                YYLogUtils.e("faceListener-onFail: " + e?.message)
            }

            //FR Engine -> 人脸比对完成结果回调
            override fun onFaceFeatureInfoGet(
                faceFeature: FaceFeature?, requestId: Int, errorCode: Int?,
                orignData: ByteArray?, faceInfo: FaceInfo?, width: Int, height: Int
            ) {
                //如果提取到了指定的人脸特征
                if (faceFeature != null) {

                    val liveness = livenessMap[requestId]
                    //不做活体检测的情况，直接搜索
                    if (!livenessDetect) {
                        searchFace(faceFeature, requestId, orignData, faceInfo, width, height)
                    } else if (liveness != null && liveness == LivenessInfo.ALIVE) {
                        searchFace(faceFeature, requestId, orignData, faceInfo, width, height)
                    } else {
                        if (requestFeatureStatusMap.containsKey(requestId)) {
                            //延时发射
                            Observable.timer(WAIT_LIVENESS_INTERVAL, TimeUnit.MILLISECONDS)
                                .subscribe(object : Observer<Long?> {
                                    var disposable: Disposable? = null

                                    override fun onSubscribe(d: Disposable) {
                                        disposable = d
                                        getFeatureDelayedDisposables.add(disposable!!)
                                    }

                                    override fun onNext(t: Long) {
                                        onFaceFeatureInfoGet(faceFeature, requestId, errorCode, orignData, faceInfo, width, height)
                                    }

                                    override fun onError(e: Throwable) {
                                    }

                                    override fun onComplete() {
                                        getFeatureDelayedDisposables.remove(disposable!!)
                                    }
                                })
                        }
                    }
                }
                //如果没有提取到特征表示特征提取失败
                else {
                    if (increaseAndGetValue(extractErrorRetryMap, requestId) > MAX_RETRY_TIME) {
                        extractErrorRetryMap[requestId] = 0
                        // 传入的FaceInfo在指定的图像上无法解析人脸，此处使用的是RGB人脸数据，一般是人脸模糊
                        val msg: String = if (errorCode != null && errorCode == ErrorInfo.MERR_FSDK_FACEFEATURE_LOW_CONFIDENCE_LEVEL) {
                            commContext().getString(R.string.low_confidence_level)
                        } else {
                            "ExtractCode:$errorCode"
                        }
                        faceHelper?.setName(requestId, commContext().getString(R.string.recognize_failed_notice, msg))
                        // 在尝试最大次数后，特征提取仍然失败，则认为识别未通过
                        requestFeatureStatusMap[requestId] = RequestFeatureStatus.FAILED
                        retryRecognizeDelayed(requestId)
                    } else {
                        requestFeatureStatusMap[requestId] = RequestFeatureStatus.TO_RETRY
                    }
                }
            }

            //FL Engine -> 是否是活体的回调处理
            override fun onFaceLivenessInfoGet(livenessInfo: LivenessInfo?, requestId: Int, errorCode: Int?) {
                if (livenessInfo != null) {
                    val liveness = livenessInfo.liveness
                    //有结果之后，重新储存这个人脸的活体状态
                    livenessMap[requestId] = liveness

                    // 非活体，重试
                    if (liveness == LivenessInfo.NOT_ALIVE) {
                        faceHelper!!.setName(requestId, commContext().getString(R.string.recognize_failed_notice, "NOT_ALIVE"))
                        // 延迟 FAIL_RETRY_INTERVAL 后，将该人脸状态置为UNKNOWN，帧回调处理时会重新进行活体检测
                        retryLivenessDetectDelayed(requestId)
                    }
                } else {
                    if (increaseAndGetValue(livenessErrorRetryMap, requestId) > MAX_RETRY_TIME) {
                        livenessErrorRetryMap[requestId] = 0
                        // 传入的FaceInfo在指定的图像上无法解析人脸，此处使用的是RGB人脸数据，一般是人脸模糊
                        val msg: String = if (errorCode != null && errorCode == ErrorInfo.MERR_FSDK_FACEFEATURE_LOW_CONFIDENCE_LEVEL) {
                            commContext().getString(R.string.low_confidence_level)
                        } else {
                            "ProcessCode:$errorCode"
                        }
                        faceHelper!!.setName(requestId, commContext().getString(R.string.recognize_failed_notice, msg))
                        retryLivenessDetectDelayed(requestId)
                    } else {
                        livenessMap[requestId] = LivenessInfo.UNKNOWN
                    }
                }
            }
        }

        //自定义相机监听器 - 开启相机监听 -预览数据nv21获取
        val cameraListener = object : CameraListener {
            override fun onCameraOpened(camera: Camera, cameraId: Int, displayOrientation: Int, isMirror: Boolean) {
                val lastPreviewSize = previewSize
                previewSize = camera.parameters.previewSize

                //绘制人脸框与文本的工具类初始化
                drawHelper = DrawHelper(
                    previewSize?.width ?: 0, previewSize?.height ?: 0, previewView.width,
                    previewView.height, displayOrientation, cameraId, isMirror, false, false
                )
                YYLogUtils.d("onCameraOpened: " + drawHelper.toString())

                // 切换相机的时候可能会导致预览尺寸发生变化
                if (faceHelper == null || lastPreviewSize == null || lastPreviewSize.width != previewSize?.width
                    || lastPreviewSize.height != previewSize?.height
                ) {
                    var trackedFaceCount: Int? = null

                    // 记录切换时的人脸序号
                    if (faceHelper != null) {
                        trackedFaceCount = faceHelper!!.trackedFaceCount
                        faceHelper!!.release()
                    }

                    //人脸处理工具类初始化，用于找出人脸，判断活体，对比人脸
                    faceHelper = FaceHelper.Builder()
                        .ftEngine(ftEngine)
                        .frEngine(frEngine)
                        .flEngine(flEngine)
                        .frQueueSize(MAX_DETECT_NUM)
                        .flQueueSize(MAX_DETECT_NUM)
                        .previewSize(previewSize)
                        .faceListener(faceListener)
                        .trackedFaceCount(trackedFaceCount ?: ConfigUtil.getTrackedFaceCount(CommUtils.getContext()))
                        .build()
                }
            }

            //摄像头画面的预览 - 获取到预览页面的nv21数据
            override fun onPreview(nv21: ByteArray, camera: Camera) {
                var startCheck = false

                faceRectView.clearFaceInfo()
                //人脸工具类处理数据流获取到人脸数据
                val facePreviewInfoList: List<FacePreviewInfo>? = faceHelper?.onPreviewFrame(nv21)
                if (!CheckUtil.isEmpty(facePreviewInfoList) && drawHelper != null) {
                    //如果有人脸，开始绘制人脸框与文本
                    val showRect = drawPreviewInfo(facePreviewInfoList!!, faceRectView)
                    showRect?.let {
                        val width = it.width()
                        if (width > 300) startCheck = true
                    }

                    //开启白色补光灯
                    openWhiteLight()
                    showNormalState()
                }

                //删除人脸数据,处理一些Map
                clearLeftFace(facePreviewInfoList)

                //限制人脸距离，比较近的时候开始检测
                if (!startCheck) return
                //开始检测活体与提取特征-内部加入一些状态判断
                if (!CheckUtil.isEmpty(facePreviewInfoList) && previewSize != null) {
                    for (i in facePreviewInfoList!!.indices) {
                        val status = requestFeatureStatusMap[facePreviewInfoList[i].trackId]
                        /**
                         * 在活体检测开启，在人脸识别状态不为成功或人脸活体状态不为处理中（ANALYZING）
                         * 且不为处理完成（ALIVE、NOT_ALIVE）时重新进行活体检测
                         */
                        if (livenessDetect && (status == null || status != RequestFeatureStatus.SUCCEED)) {
                            val liveness = livenessMap[facePreviewInfoList[i].trackId]
                            if (liveness == null || liveness != LivenessInfo.ALIVE && liveness != LivenessInfo.NOT_ALIVE
                                && liveness != RequestLivenessStatus.ANALYZING
                            ) {
                                //开始分析活体，先储存状态为分析中
                                livenessMap[facePreviewInfoList[i].trackId] = RequestLivenessStatus.ANALYZING
                                //人脸工具类调用方法开始分析活体，结果在Face回调中
                                faceHelper!!.requestFaceLiveness(
                                    nv21,
                                    facePreviewInfoList[i].faceInfo,
                                    previewSize!!.width,
                                    previewSize!!.height,
                                    FaceEngine.CP_PAF_NV21,
                                    facePreviewInfoList[i].trackId,
                                    LivenessType.RGB
                                )
                            }
                        }

                        /**
                         * 对于每个人脸，若状态为空或者为失败，则请求特征提取（可根据需要添加其他判断以限制特征提取次数），
                         * 特征提取回传的人脸特征结果在[FaceListener.onFaceFeatureInfoGet]中回传
                         */
                        if (status == null || status == RequestFeatureStatus.TO_RETRY) {
                            //开启分析人脸特征，先存储状态为搜索中
                            requestFeatureStatusMap[facePreviewInfoList[i].trackId] = RequestFeatureStatus.SEARCHING
                            //人脸工具类调用方法开启提前人脸特征
                            faceHelper!!.requestFaceFeature(
                                nv21,
                                facePreviewInfoList[i].faceInfo,
                                previewSize!!.width,
                                previewSize!!.height,
                                FaceEngine.CP_PAF_NV21,
                                facePreviewInfoList[i].trackId
                            )
                        }
                    }
                }
            }

            override fun onCameraClosed() {
                YYLogUtils.w("onCameraClosed: ")
            }

            override fun onCameraError(e: java.lang.Exception?) {
                YYLogUtils.e("onCameraError: " + e?.message)
            }

            override fun onCameraConfigurationChanged(cameraID: Int, displayOrientation: Int) {
                drawHelper?.cameraDisplayOrientation = displayOrientation
                YYLogUtils.w("onCameraConfigurationChanged: $cameraID  $displayOrientation")
            }
        }

        cameraHelper = CameraHelper.Builder()
            .previewViewSize(Point(previewView.measuredWidth, previewView.measuredHeight))  //预览的宽高 最佳相机比例时用到
            .rotation(activity.windowManager.defaultDisplay.rotation)     //指定旋转角度 固定写法
            .specificCameraId(rgbCameraID)   //指定相机ID，这里指定前置
            .isMirror(false)         //是否开启前置镜像
            .previewOn(previewView) //预览容器 推荐TextureView
            .cameraListener(cameraListener) //设置自定义的监听器
            .build()
        cameraHelper?.init()
        cameraHelper?.start()
    }

    //相机预览页面绘制人脸方框
    private fun drawPreviewInfo(facePreviewInfoList: List<FacePreviewInfo>, faceRectView: FaceRectView): Rect? {
        val drawInfoList: MutableList<DrawInfo> = ArrayList()
        var rect: Rect? = null
        for (i in facePreviewInfoList.indices) {
            val name = faceHelper?.getName(facePreviewInfoList[i].trackId)
            val liveness = livenessMap[facePreviewInfoList[i].trackId]
            val recognizeStatus = requestFeatureStatusMap[facePreviewInfoList[i].trackId]

            // 根据识别结果和活体结果设置颜色
            var color: Int = RecognizeColor.COLOR_UNKNOWN
            if (recognizeStatus != null) {
                if (recognizeStatus == RequestFeatureStatus.FAILED) {
                    color = RecognizeColor.COLOR_FAILED
                }
                if (recognizeStatus == RequestFeatureStatus.SUCCEED) {
                    color = RecognizeColor.COLOR_SUCCESS
                }
            }
            if (liveness != null && liveness == LivenessInfo.NOT_ALIVE) {
                color = RecognizeColor.COLOR_FAILED
            }
            rect = drawHelper?.adjustRect(facePreviewInfoList[i].faceInfo.rect)
            //添加需要绘制的人脸信息
            drawInfoList.add(
                DrawInfo(
                    rect, GenderInfo.UNKNOWN, AgeInfo.UNKNOWN_AGE, liveness ?: LivenessInfo.UNKNOWN, color,
                    name ?: (facePreviewInfoList[i].trackId).toString()
                )
            )
        }

        //开启绘制
        drawHelper?.draw(faceRectView, drawInfoList)

        return rect
    }

    /**
     * 在已经注册的待检测人脸中搜索指定人脸
     */
    private fun searchFace(
        frFace: FaceFeature, requestId: Int,
        orignData: ByteArray?, faceInfo: FaceInfo?, width: Int, height: Int
    ) {

        launchOnUI {

            val compareResult = withContext(Dispatchers.IO) {
                //直接调用Server方法获取比对之后的人脸,内部实现是SDK方法compareFaceFeature
                YYLogUtils.w("find FaceFeature :$frFace")
                val compareResult: CompareResult? = FaceServer.getInstance().getTopOfFaceLib(frFace)
                YYLogUtils.w("find compare result :$compareResult")
                return@withContext compareResult
            }

            if (compareResult?.userName == null) {
                requestFeatureStatusMap[requestId] = RequestFeatureStatus.FAILED
                faceHelper?.setName(requestId, "VISITOR1-$requestId")

                //开启红灯-代表失败
                openRedLight()

                retryRecognizeDelayed(requestId)
                return@launchOnUI
            }

            if (compareResult.similar > SIMILAR_THRESHOLD) {
                //满足相似度
                var isAdded = false
                if (compareResultList == null) {
                    requestFeatureStatusMap[requestId] = RequestFeatureStatus.FAILED
                    faceHelper?.setName(requestId, "VISITOR2-$requestId")
                    //开启红灯-代表失败
                    openRedLight()
                    return@launchOnUI
                }

                //排查重复数据
                for (compareResult1 in compareResultList) {
                    if (compareResult1.trackId == requestId) {
                        isAdded = true
                        break
                    }
                }

                if (!isAdded) {
                    //对于多人脸搜索，假如最大显示数量为 MAX_DETECT_NUM 且有新的人脸进入，则以队列的形式移除
                    if (compareResultList.size >= MAX_DETECT_NUM) {
                        compareResultList.removeAt(0)
//                      adapter.notifyItemRemoved(0)
                    }
                    //添加显示人员时，保存其trackId
                    compareResult.trackId = requestId
                    compareResultList.add(compareResult)
//                  adapter.notifyItemInserted(compareResultList.size - 1)
                }
                requestFeatureStatusMap[requestId] = RequestFeatureStatus.SUCCEED
                faceHelper?.setName(requestId, commContext().getString(R.string.recognize_success_notice, compareResult.userName))
                //开启绿灯-代表成功
                openGreenLight()

                //成功之后跳转新页面
                jumpSuccessPage(compareResult, orignData, faceInfo, width, height)

            } else {
                //相似度小于0.8不是一个人
                faceHelper?.setName(requestId, commContext().getString(R.string.recognize_failed_notice, "NOT_REGISTERED"))
                //开启红灯-代表失败
                openRedLight()

                retryRecognizeDelayed(requestId)
            }

        }

    }

    //识别成功跳转到成功页面
    private fun jumpSuccessPage(compareResult: CompareResult, orignData: ByteArray?, faceInfo: FaceInfo?, width: Int, height: Int) {
        //为了显示框框好看的画面
        CommUtils.getHandler().postDelayed({
            cameraHelper?.stop()
            clearLeftFace(null)
            SuccessActivity.startInstance(compareResult, orignData, faceInfo, width, height)
        }, 250)

    }

    /**
     * 删除已经离开的人脸
     *
     * @param facePreviewInfoList 人脸和trackId列表
     */
    fun clearLeftFace(facePreviewInfoList: List<FacePreviewInfo>?) {
        if (compareResultList.isNotEmpty()) {
            for (i in compareResultList.indices.reversed()) {
                if (!requestFeatureStatusMap.containsKey(compareResultList[i].trackId)) {
                    compareResultList.removeAt(i)
//                    adapter.notifyItemRemoved(i)
                }
            }
        }
        if (facePreviewInfoList == null || facePreviewInfoList.isEmpty()) {
            requestFeatureStatusMap.clear()
            livenessMap.clear()
            livenessErrorRetryMap.clear()
            extractErrorRetryMap.clear()

            getFeatureDelayedDisposables.clear()

            return
        }
        val keys = requestFeatureStatusMap.keys()
        while (keys.hasMoreElements()) {
            val key = keys.nextElement()
            var contained = false
            for (facePreviewInfo in facePreviewInfoList) {
                if (facePreviewInfo.trackId == key) {
                    contained = true
                    break
                }
            }
            if (!contained) {
                requestFeatureStatusMap.remove(key)
                livenessMap.remove(key)
                livenessErrorRetryMap.remove(key)
                extractErrorRetryMap.remove(key)
            }
        }
    }

    /**
     * 将map中key对应的value增1回传
     *
     * @param countMap map
     * @param key      key
     * @return 增1后的value
     */
    fun increaseAndGetValue(countMap: MutableMap<Int, Int>?, key: Int): Int {
        if (countMap == null) {
            return 0
        }
        var value = countMap[key]
        if (value == null) {
            value = 0
        }
        countMap[key] = ++value
        return value
    }

    /**
     * 延迟 FAIL_RETRY_INTERVAL 重新进行活体检测
     *
     * @param requestId 人脸ID
     */
    private fun retryLivenessDetectDelayed(requestId: Int) {
        Observable.timer(FAIL_RETRY_INTERVAL, TimeUnit.MILLISECONDS)
            .subscribe(object : Observer<Long?> {
                var disposable: Disposable? = null
                override fun onSubscribe(d: Disposable) {
                    disposable = d
                    delayFaceTaskCompositeDisposable.add(disposable!!)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onComplete() {
                    // 将该人脸状态置为UNKNOWN，帧回调处理时会重新进行活体检测
                    if (livenessDetect) {
                        faceHelper!!.setName(requestId, Integer.toString(requestId))
                    }
                    livenessMap[requestId] = LivenessInfo.UNKNOWN
                    delayFaceTaskCompositeDisposable.remove(disposable!!)
                }

                override fun onNext(t: Long) {
                }
            })
    }

    /**
     * 延迟 FAIL_RETRY_INTERVAL 重新进行人脸识别
     *
     * @param requestId 人脸ID
     */
    private fun retryRecognizeDelayed(requestId: Int) {
        requestFeatureStatusMap[requestId] = RequestFeatureStatus.FAILED
        Observable.timer(FAIL_RETRY_INTERVAL, TimeUnit.MILLISECONDS)
            .subscribe(object : Observer<Long?> {
                var disposable: Disposable? = null
                override fun onSubscribe(d: Disposable) {
                    disposable = d
                    delayFaceTaskCompositeDisposable.add(disposable!!)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onComplete() {
                    // 将该人脸特征提取状态置为FAILED，帧回调处理时会重新进行活体检测
                    faceHelper?.setName(requestId, requestId.toString())
                    requestFeatureStatusMap[requestId] = RequestFeatureStatus.TO_RETRY
                    delayFaceTaskCompositeDisposable.remove(disposable!!)
                }

                override fun onNext(t: Long) {
                }
            })
    }

    /**
     * 引擎工具类初始化
     */
    fun initEngine() {
        //检测人脸
        ftEngine = FaceEngine()
        ftInitCode = ftEngine.init(
            //这个比较特殊需要270度角才能识别
            commContext(), DetectMode.ASF_DETECT_MODE_VIDEO, ConfigUtil.getFtOrient(commContext()), 16,
            MAX_DETECT_NUM, FaceEngine.ASF_FACE_DETECT
        )

        //提取特征
        frEngine = FaceEngine()
        frInitCode = frEngine.init(
            commContext(), DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_0_ONLY, 16,
            MAX_DETECT_NUM, FaceEngine.ASF_FACE_RECOGNITION
        )

        //活体识别
        flEngine = FaceEngine()
        flInitCode = flEngine.init(
            commContext(), DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_0_ONLY, 16,
            MAX_DETECT_NUM, FaceEngine.ASF_LIVENESS
        )

        if (ftInitCode != ErrorInfo.MOK) {
            val error = commContext().getString(R.string.specific_engine_init_failed, "ftEngine", ftInitCode)
            toast(error)
            return
        }
        if (frInitCode != ErrorInfo.MOK) {
            val error = commContext().getString(R.string.specific_engine_init_failed, "frEngine", frInitCode)
            toast(error)
            return
        }
        if (flInitCode != ErrorInfo.MOK) {
            val error = commContext().getString(R.string.specific_engine_init_failed, "flEngine", flInitCode)
            toast(error)
            return
        }
    }

    /**
     * 销毁引擎，faceHelper中可能会有特征提取耗时操作仍在执行，加锁防止crash
     */
    fun unInitEngine() {
        if (ftInitCode == ErrorInfo.MOK) {
            synchronized(ftEngine) {
                val ftUnInitCode = ftEngine.unInit()
                YYLogUtils.d("unInitEngine: $ftUnInitCode")
            }
        }
        if (frInitCode == ErrorInfo.MOK) {
            synchronized(frEngine) {
                val frUnInitCode = frEngine.unInit()
                YYLogUtils.d("unInitEngine: $frUnInitCode")
            }
        }
        if (flInitCode == ErrorInfo.MOK) {
            synchronized(flEngine) {
                val flUnInitCode = flEngine.unInit()
                YYLogUtils.d("unInitEngine: $flUnInitCode")
            }
        }
    }

    // =======================  Camera face end ↑ =========================

    // =======================  Device Control begin ↓ =========================
    private var isRedGreenLightOpened = false
    private var isWhiteLightOpened = false
    private val STATE_NORMAL = 1
    private val STATE_LOADING = 2
    private val STATE_SCREEN_OFF = 3
    val curStateLD = MutableLiveData<Int>()

    private val mDeviceHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0x01) {
                //延时关闭灯光
                DeviceManager.closeAllLight()

                isRedGreenLightOpened = false
                isWhiteLightOpened = false
            } else if (msg.what == 0x03) {

                showLoadingState()
            } else if (msg.what == 0x04) {

                showScreenOffState()
            }
        }
    }

    /**
     * 打开白色灯光,默认5秒之后关闭
     */
    fun openWhiteLight() {
        if (!isRedGreenLightOpened) {
            if (!isWhiteLightOpened) {
                DeviceManager.apply {
                    closeAllLight()
                    openWhiteLight()
                }
            }

            mDeviceHandler.removeMessages(0x01)
            mDeviceHandler.sendEmptyMessageDelayed(0x01, 5000)

            isWhiteLightOpened = true
        }
    }

    /**
     * 打开红色灯光,默认2秒关闭
     */
    private fun openRedLight() {
        isRedGreenLightOpened = true

        DeviceManager.apply {
            closeAllLight()
            openRedLight()
        }

        mDeviceHandler.removeMessages(0x01)
        mDeviceHandler.sendEmptyMessageDelayed(0x01, 2000)
    }

    /**
     * 打开绿色灯光,默认2秒关闭
     */
    private fun openGreenLight() {
        isRedGreenLightOpened = true

        DeviceManager.apply {
            closeAllLight()
            openGreenLight()
        }

        mDeviceHandler.removeMessages(0x01)
        mDeviceHandler.sendEmptyMessageDelayed(0x01, 2000)
    }

    //展示默认运行页面
    fun showNormalState() {
        curStateLD.value = 1

        mDeviceHandler.removeMessages(0x03)
        mDeviceHandler.removeMessages(0x04)
        mDeviceHandler.sendEmptyMessageDelayed(0x03, 10000)
    }

    //展示Loading页面
    fun showLoadingState() {
        curStateLD.value = 2

        mDeviceHandler.removeMessages(0x03)
        mDeviceHandler.removeMessages(0x04)
        mDeviceHandler.sendEmptyMessageDelayed(0x04, 20000)
    }

    //展示全黑页面
    fun showScreenOffState() {
        curStateLD.value = 3

        mDeviceHandler.removeMessages(0x03)
        mDeviceHandler.removeMessages(0x04)
    }

    //页面destory的时候释放全部的消息
    fun releseAllHandler() {
        mDeviceHandler.removeCallbacksAndMessages(null)
    }

    // =======================  Device Control end ↑ =========================

}