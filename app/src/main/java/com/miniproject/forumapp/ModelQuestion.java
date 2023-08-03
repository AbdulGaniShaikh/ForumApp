package com.miniproject.forumapp;

public class ModelQuestion {

    private String name,question,mail,time,id;
    private int avatar;

    public ModelQuestion(String name, String question, String mail, String time, int avatar, String id) {
        this.name = name;
        this.question = question;
        this.mail = mail;
        this.time = time;
        this.id = id;
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
