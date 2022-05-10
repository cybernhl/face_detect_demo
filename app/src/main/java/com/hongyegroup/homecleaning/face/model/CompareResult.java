package com.hongyegroup.homecleaning.face.model;


import java.io.Serializable;

/**
 * 比对成功的返回对象
 */
public class CompareResult implements Serializable {
    public String userId;   //用户Id
    public String userName;  //用户姓名

    public float similar;   //匹配分数
    public int trackId;

    public CompareResult(String userId, String userName, float similar) {
        this.userId = userId;
        this.userName = userName;
        this.similar = similar;
    }

    public CompareResult() {
    }


}
