package com.miniproject.forumapp;

public class ModelFriendReq {

    private String name,userId;
    private int avatar;

    public ModelFriendReq(String name, String userId, int avatar) {
        this.name = name;
        this.userId = userId;
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }
}
