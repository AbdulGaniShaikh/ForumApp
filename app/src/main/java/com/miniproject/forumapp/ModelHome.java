package com.miniproject.forumapp;

public class ModelHome {

    String topicname,desp,id,category;
    int followers;


    public ModelHome(String topicname, String desp, String id, String category, int followers) {
        this.topicname = topicname;
        this.desp = desp;
        this.id = id;
        this.category = category;
        this.followers = followers;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopicname() {
        return topicname;
    }

    public void setTopicname(String topicname) {
        this.topicname = topicname;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }
}
