package com.hongyegroup.homecleaning.mvp

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import com.arcsoft.face.ActiveFileInfo
import com.arcsoft.face.ErrorInfo
import com.arcsoft.face.FaceEngine
import com.arcsoft.imageutil.ArcSoftImageFormat
import com.arcsoft.imageutil.ArcSoftImageUtil
import com.arcsoft.imageutil.ArcSoftImageUtilError
import com.guadou.cs_cptservices.YYConstants
import com.guadou.lib_baselib.base.vm.BaseViewModel
import com.guadou.lib_baselib.ext.commContext
import com.guadou.lib_baselib.ext.toast
import com.guadou.lib_baselib.utils.CommUtils
import com.guadou.lib_baselib.utils.Log.YYLogUtils
import com.hongyegroup.homecleaning.R
import com.hongyegroup.homecleaning.bean.UserInfo
import com.hongyegroup.homecleaning.face.faceserver.FaceServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class LoginViewModel : BaseViewModel() {

    /**
     * 激活引擎
     */
    fun activeEngine() {

        launchOnUI {

            val activeCode = withContext(Dispatchers.IO) {

                val start = System.currentTimeMillis()
                val activeCode = FaceEngine.activeOnline(CommUtils.getContext(), YYConstants.APP_ID, YYConstants.SDK_KEY)
                YYLogUtils.w("subscribe cost: " + (System.currentTimeMillis() - start))

                return@withContext activeCode
            }

            when (activeCode) {
                ErrorInfo.MOK -> {
                    toast(R.string.active_success)
                }
                ErrorInfo.MERR_ASF_ALREADY_ACTIVATED -> {
                    toast(R.string.already_activated)
                }
                else -> {
                    toast(CommUtils.getContext().resources.getString(R.string.active_failed, activeCode))
                }
            }

            val activeFileInfo = ActiveFileInfo()
            val res = FaceEngine.getActiveFileInfo(CommUtils.getContext(), activeFileInfo)
            if (res == ErrorInfo.MOK) {
                YYLogUtils.e(activeFileInfo.toString())
            }
        }

    }

    /**
     * 尝试本地注册一些人员进去
     */
    fun tryRegisterUser() {
        FaceServer.getInstance().init(commContext())
        doRegister()
    }

    private fun doRegister() {

        launchOnUI {
//            val failurePath = commContext().filesDir.absolutePath + File.separator + "failed"

            var successCount = 0
            val memberList = listOf(
                UserInfo("1", "chengxiao", R.drawable.a),
                UserInfo("2", "chenlu", R.drawable.b),
                UserInfo("3", "liukai", R.drawable.c),
                UserInfo("4", "leyunying", R.drawable.d),
                UserInfo("5", "fangjun", R.drawable.e),
                UserInfo("6", "huyu", R.drawable.f)
            )
            withContext(Dispatchers.IO) {
                memberList.forEachIndexed { index, bean ->

                    // 获取原始Bitmap
                    var bitmap = BitmapFactory.decodeResource(commContext().resources, bean.userAvatar)
                    if (bitmap == null) {
                        return@forEachIndexed
                    }

                    // 旋转角度创建新的图片
                    val width = bitmap.width
                    val height = bitmap.height
                    if (width > height) {
                        val matrix = Matrix()
                        matrix.postRotate(90F)
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    }

                    // 图像对齐
                    bitmap = ArcSoftImageUtil.getAlignedBitmap(bitmap, true)
                    if (bitmap == null) {
                        //添加到失败文件夹中去
                        return@forEachIndexed
                    }

                    // bitmap转bgr24
                    val bgr24 = ArcSoftImageUtil.createImageData(bitmap.width, bitmap.height, ArcSoftImageFormat.BGR24)
                    val transformCode = ArcSoftImageUtil.bitmapToImageData(bitmap, bgr24, ArcSoftImageFormat.BGR24)
                    if (transformCode != ArcSoftImageUtilError.CODE_SUCCESS) {
                        return@forEachIndexed
                    }

                    //使用bgr24注册人脸信息
                    val success = FaceServer.getInstance().registerBgr24(
                        commContext(), bgr24,
                        bitmap.width, bitmap.height,
                        bean.userId + "-" + bean.userName  //保存到Face文件夹的文件名
                    )

                    if (!success) {
                        //添加到失败文件夹中去
                        bean.isRegistSuccess = false

                    } else {
                        bean.isRegistSuccess = true
                        successCount++
                    }

                    YYLogUtils.w("current register index :$index")
                }

            }

            val failureList = memberList.filter { !it.isRegistSuccess }
            toast("resigter success :$successCount failed:$failureList")

        }

    }

    /**
     * 清除已经注册的人脸信息
     */
    fun clearRegistedFaces(activity: Activity) {
        val faceNum: Int = FaceServer.getInstance().getFaceNumber(commContext())
        if (faceNum == 0) {
            toast(R.string.batch_process_no_face_need_to_delete)
        } else {
            val dialog: AlertDialog = AlertDialog.Builder(activity)
                .setTitle(R.string.batch_process_notification)
                .setMessage((commContext().resources.getString(R.string.batch_process_confirm_delete, faceNum)))
                .setPositiveButton(R.string.ok) { dialog, which ->
                    val deleteCount: Int = FaceServer.getInstance().clearAllFaces(activity)
                    toast("$deleteCount faces cleared!")
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
            dialog.show()
        }
    }
}