package com.phillipsong.gittrending.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fei on 16/5/12.
 */
public class GithubUser {

    private String id;
    private String login;
    @SerializedName("avatar_url")
    private String avatar;
    @SerializedName("html_url")
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "GithubUser{" +
                "id='" + id + '\'' +
                ", login='" + login + '\'' +
                ", avatar='" + avatar + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
