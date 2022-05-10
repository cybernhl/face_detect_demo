package com.hongyegroup.homecleaning.face.model;

/**
 * 注册人脸的信息
 */
public class FaceRegisterInfo {
    public byte[] featureData;
    public String userId;
    public String userName;

    public FaceRegisterInfo(byte[] faceFeature, String userId, String userName) {
        this.featureData = faceFeature;
        this.userName = userName;
        this.userId = userId;
    }

}
