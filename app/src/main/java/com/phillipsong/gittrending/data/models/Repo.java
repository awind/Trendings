/*
 * Copyright (c) 2016 Phillip Song (http://github.com/awind).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phillipsong.gittrending.data.models;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Repo extends RealmObject {

    private String name;
    private String owner;
    private String star;
    private String url;
    private String language;
    private String description;
    private RealmList<Contributor> contributors;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(RealmList<Contributor> contributors) {
        this.contributors = contributors;
    }

    @Override
    public String toString() {
        return "Repo{" +
                "name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", star='" + star + '\'' +
                ", url='" + url + '\'' +
                ", language='" + language + '\'' +
                ", description='" + description + '\'' +
                ", contributors=" + contributors +
                '}';
    }
}