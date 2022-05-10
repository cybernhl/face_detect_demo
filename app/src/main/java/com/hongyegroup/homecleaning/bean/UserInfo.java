package com.hongyegroup.homecleaning.bean;


public class UserInfo {
    public String userId;
    public String userName;
    public int userAvatar;

    public boolean isRegistSuccess = true;

    public UserInfo() {
    }

    public UserInfo(String userId, String userName, int userAvatar) {
        this.userId = userId;
        this.userName = userName;
        this.userAvatar = userAvatar;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", isRegistSuccess=" + isRegistSuccess +
                '}';
    }
}
