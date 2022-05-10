package com.hongyegroup.homecleaning.face.model;

import com.arcsoft.face.FaceInfo;

/**
 * 预览人脸的信息
 */
public class FacePreviewInfo {
    public FaceInfo faceInfo;
    public int trackId;

    public FacePreviewInfo(FaceInfo faceInfo, int trackId) {
        this.faceInfo = faceInfo;
        this.trackId = trackId;
    }

}
