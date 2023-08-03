package com.miniproject.forumapp;

public class ModelPeople {

    final private String name,userid,mail;
    final private int avatar;

    public ModelPeople(String name, String userid, String mail, int avatar) {
        this.name = name;
        this.userid = userid;
        this.mail = mail;
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public String getUserid() {
        return userid;
    }

    public int getAvatar() {
        return avatar;
    }

    public String getMail() {
        return mail;
    }
}
