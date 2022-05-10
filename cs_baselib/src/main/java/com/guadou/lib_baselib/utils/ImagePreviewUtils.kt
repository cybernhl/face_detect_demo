package com.guadou.lib_baselib.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.guadou.lib_baselib.engine.extLoad
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.ImageViewerPopupView
import com.lxj.xpopup.interfaces.XPopupImageLoader
import java.io.File

/**
 * 图片的预览工具类，基于XPopup实现
 */
object ImagePreviewUtils {

    /**
     * 单图片的预览
     */
    fun singleImagePreview(
        context: Context,
        img: ImageView,
        imageUrl: String,
        placeholderRes: Int = 0,
        placeholderColor: Int = -1,
        isInfinite: Boolean = false,
        placeholderStroke: Int = -1,
        roundRadius: Int = 0,
        isShowSaveBtn: Boolean = false
    ) {
        XPopup.Builder(context)
            .asImageViewer(
                img,
                imageUrl,
                isInfinite,
                placeholderColor,
                placeholderStroke,
                roundRadius,
                isShowSaveBtn,
                ImageLoader(placeholderRes)
            )
            .show()
    }

    /**
     * 多图的选择
     */
    fun multipleImagePreview(
        context: Context,
        img: ImageView,
        list: List<Any>,
        position: Int = 0,
        placeholderRes: Int = 0,
        isInfinite: Boolean = false,
        isShowSaveBtn: Boolean = false,
        block: (popupView: ImageViewerPopupView, position: Int) -> Unit
    ) {
        //Xpopup的弹窗
        XPopup.Builder(context).asImageViewer(
            img,
            position,
            list,
            isInfinite,
            true,
            placeholderRes,
            0,
            0,
            isShowSaveBtn,
            block,
            ImageLoader(placeholderRes)
        ).show()
    }


    class ImageLoader(private val placeholderRes: Int) : XPopupImageLoader {

        override fun loadImage(position: Int, url: Any, imageView: ImageView) {
            //必须指定Target.SIZE_ORIGINAL，否则无法拿到原图，就无法享用天衣无缝的动画
            imageView.extLoad(url, placeholderRes, isForceOriginalSize = true)
        }

        override fun getImageFile(context: Context, uri: Any): File? {
            try {
                return Glide.with(context).downloadOnly().load(uri).submit().get()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

}