package com.phillipsong.gittrending.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by fei on 16/5/12.
 */
public class GithubResponse<T> {
    @SerializedName("total_count")
    private int count;
    @SerializedName("incomplete_results")
    private boolean incomplete;
    private List<T> items;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isIncomplete() {
        return incomplete;
    }

    public void setIncomplete(boolean incomplete) {
        this.incomplete = incomplete;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
