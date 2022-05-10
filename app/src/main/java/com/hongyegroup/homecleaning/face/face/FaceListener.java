package com.hongyegroup.homecleaning.face.face;

import androidx.annotation.Nullable;

import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.LivenessInfo;

/**
 * 人脸处理回调
 */
public interface FaceListener {
    /**
     * 当出现异常时执行
     *
     * @param e 异常信息
     */
    void onFail(Exception e);


    /**
     * 请求人脸特征后的回调
     *
     * @param faceFeature 人脸特征数据
     * @param requestId   请求码
     * @param errorCode   错误码
     * @param orignData   原始预览帧数据 用于转换Bitmap
     * @param width       原始预览帧宽度 用于转换Bitmap
     * @param height      原始预览帧高度 用于转换Bitmap
     */
    void onFaceFeatureInfoGet(@Nullable FaceFeature faceFeature, Integer requestId, Integer errorCode,
                              byte[] orignData, FaceInfo faceInfo, int width, int height);

    /**
     * 请求活体检测后的回调
     *
     * @param livenessInfo 活体检测结果
     * @param requestId    请求码
     * @param errorCode    错误码
     */
    void onFaceLivenessInfoGet(@Nullable LivenessInfo livenessInfo, Integer requestId, Integer errorCode);
}
