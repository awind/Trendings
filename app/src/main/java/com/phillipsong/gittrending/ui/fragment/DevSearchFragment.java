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
package com.phillipsong.gittrending.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.phillipsong.gittrending.R;
import com.phillipsong.gittrending.TrendingApplication;
import com.phillipsong.gittrending.data.api.GithubService;
import com.phillipsong.gittrending.data.models.GithubUser;
import com.phillipsong.gittrending.inject.components.AppComponent;
import com.phillipsong.gittrending.inject.components.DaggerDevSearchFragmentComponent;
import com.phillipsong.gittrending.inject.modules.DevSearchFragmentModule;
import com.phillipsong.gittrending.ui.adapter.DevSearchAdapter;
import com.phillipsong.gittrending.ui.misc.OnItemClickListener;
import com.phillipsong.gittrending.ui.misc.OnRecScrollListener;
import com.phillipsong.gittrending.ui.widget.PSwipeRefreshLayout;
import com.phillipsong.gittrending.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DevSearchFragment extends BaseFragment implements OnItemClickListener {

    private static final String TAG = "DevSearchFragment";

    @Inject
    TrendingApplication mContext;
    @Inject
    GithubService mGithubApi;

    private PSwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private DevSearchAdapter mAdapter;
    private List<GithubUser> mUserList;
    private String mKeyword;
    private boolean isLoading;
    private int mCurrentPage = 1;
    private int mTotalItems = Constants.PER_ITEMS_COUNT;

    private Action1<List<GithubUser>> mUpdateAction = items -> {
        isLoading = false;
        if (mCurrentPage == 1) {
            mUserList.clear();
        }
        mCurrentPage += 1;
        mUserList.addAll(items);
        mAdapter.notifyDataSetChanged();
    };

    private Action1<Throwable> mThrowableAction = throwable -> {
        isLoading = false;
        Answers.getInstance().logCustom(new CustomEvent("UpdateException")
                .putCustomAttribute("location", TAG)
                .putCustomAttribute("message", throwable.getMessage()));
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dev_search, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mSwipeRefreshLayout = (PSwipeRefreshLayout) view.findViewById(R.id.refresher);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mUserList = new ArrayList<>();
        mAdapter = new DevSearchAdapter(mContext, mUserList);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new OnRecScrollListener() {
            @Override
            public void onBottom() {
                super.onBottom();
                if (mCurrentPage * Constants.PER_ITEMS_COUNT > mTotalItems) {
                    return;
                }
                search(mKeyword);
            }
        });

        RxSwipeRefreshLayout.refreshes(mSwipeRefreshLayout)
                .compose(bindToLifecycle())
                .observeOn(Schedulers.io())
                .flatMap(aVoid -> {
                    mCurrentPage = 1;
                    mTotalItems = Constants.PER_ITEMS_COUNT;
                    return mGithubApi.searchUser(mKeyword, mCurrentPage);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(aVoid -> mSwipeRefreshLayout.setRefreshing(false))
                .retry()
                .flatMap(response -> {
                    mTotalItems = response.getCount();
                    return Observable.just(response.getItems());
                })
                .subscribe(mUpdateAction, mThrowableAction);
    }

    @Override
    protected void setupFragmentComponent(AppComponent appComponent) {
        DaggerDevSearchFragmentComponent.builder()
                .appComponent(appComponent)
                .devSearchFragmentModule(new DevSearchFragmentModule(this))
                .build()
                .inject(this);
    }

    public void search(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return;
        }

        if (isLoading) {
            return;
        }

        if (!keyword.equals(mKeyword)) {
            mCurrentPage = 1;
            mTotalItems = Constants.PER_ITEMS_COUNT;
        }

        mKeyword = keyword;
        isLoading = true;
        mGithubApi.searchUser(keyword, mCurrentPage)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> mSwipeRefreshLayout.setRefreshing(true))
                .doOnCompleted(() -> mSwipeRefreshLayout.setRefreshing(false))
                .doOnError(error -> mSwipeRefreshLayout.setRefreshing(false))
                .flatMap(response -> {
                    mTotalItems = response.getCount();
                    return Observable.just(response.getItems());
                })
                .subscribe(mUpdateAction, mThrowableAction);
    }

    @Override
    public void onItemClick(int position) {
        if (mUserList == null || mUserList.size() == 0)
            return;

        GithubUser user = mUserList.get(position);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(user.getUrl()));
        startActivity(i);

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName(user.getUrl())
                .putContentType(TAG));
    }
}
