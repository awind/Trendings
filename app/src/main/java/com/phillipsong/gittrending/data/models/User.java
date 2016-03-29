/**
 * Copyright (c) 2015-present, MaxLeapMobile.
 * All rights reserved.
 * ----
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.phillipsong.gittrending.data.models;

import com.google.gson.annotations.SerializedName;

public class User {
    private int rank;
    @SerializedName("login_name")
    private String loginName;
    @SerializedName("full_name")
    private String fullName;
    private String avatar;
    private String url;
    @SerializedName("repo_name")
    private String repoName;
    @SerializedName("repo_url")
    private String repoUrl;
    @SerializedName("repo_desc")
    private String repoDesc;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getRepoDesc() {
        return repoDesc;
    }

    public void setRepoDesc(String repoDesc) {
        this.repoDesc = repoDesc;
    }

    @Override
    public String toString() {
        return "User{" +
                "rank=" + rank +
                ", loginName='" + loginName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", url='" + url + '\'' +
                ", repoName='" + repoName + '\'' +
                ", repoUrl='" + repoUrl + '\'' +
                ", repoDesc='" + repoDesc + '\'' +
                '}';
    }
}