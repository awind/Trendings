package com.phillipsong.gittrending.data.models;

import com.google.gson.annotations.SerializedName;


/**
 * Created by fei on 16/5/12.
 */
public class GithubRepo {
    private String id;
    private String name;
    @SerializedName("full_name")
    private String fullName;
    private String description;
    @SerializedName("html_url")
    private String url;
    private String language;
    @SerializedName("stargazers_count")
    private int stars;
    private GithubUser owner;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public GithubUser getOwner() {
        return owner;
    }

    public void setOwner(GithubUser owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "GithubRepo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", url='" + url + '\'' +
                ", language='" + language + '\'' +
                ", stars=" + stars +
                ", owner=" + owner +
                '}';
    }
}
