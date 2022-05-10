package com.hongyegroup.homecleaning.mvp

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arcsoft.face.FaceInfo
import com.arcsoft.imageutil.ArcSoftImageFormat
import com.guadou.lib_baselib.base.vm.BaseViewModel
import com.guadou.lib_baselib.ext.commContext
import com.guadou.lib_baselib.utils.BitmapUtils
import com.guadou.lib_baselib.utils.Log.YYLogUtils
import com.hongyegroup.homecleaning.face.faceserver.FaceServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class SuccessViewModel : BaseViewModel() {

    /**
     * 转换为BitMap对象
     */
    fun handleBitmap(nv21: ByteArray?, faceInfo: FaceInfo?, width: Int, height: Int, userName: String): LiveData<String> {
        val liveData = MutableLiveData<String>()
        YYLogUtils.w("nv21:$nv21 faceInfo$faceInfo")

        if (nv21 != null && faceInfo != null) {

            launchOnUI {

                withContext(Dispatchers.IO) {

                    try {

                        // 保存注册结果（注册图、特征数据）
                        // 为了美观，扩大rect截取注册图
                        val cropRect = FaceServer.getBestRect(width, height, faceInfo.rect)
                        if (cropRect == null) {
                            YYLogUtils.e("转换为BitMap对象: cropRect is null!")
                            return@withContext
                        }

                        cropRect.left = cropRect.left and 3.inv()
                        cropRect.top = cropRect.top and 3.inv()
                        cropRect.right = cropRect.right and 3.inv()
                        cropRect.bottom = cropRect.bottom and 3.inv()

                        val imgDir = BitmapUtils.getInstance(commContext()).sdpath
                        val file = File(imgDir + File.separator + userName + System.currentTimeMillis().toString() + FaceServer.IMG_SUFFIX)

                        // 创建一个头像的Bitmap，存放旋转结果图
                        val headBmp: Bitmap = FaceServer.getInstance().getHeadImage(
                            nv21, width, height, faceInfo.orient, cropRect, ArcSoftImageFormat.NV21
                        )

                        //保存到本地文件
                        val fosImage = FileOutputStream(file)
                        headBmp.compress(Bitmap.CompressFormat.JPEG, 100, fosImage)
                        fosImage.close()

                        liveData.postValue(file.absolutePath)
                    } catch (e: IOException) {

                        liveData.postValue(null)
                        e.printStackTrace()
                    }

                }

            }

        }

        return liveData
    }

}