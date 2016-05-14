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
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.phillipsong.gittrending.data.models.GithubRepo;
import com.phillipsong.gittrending.data.models.User;
import com.phillipsong.gittrending.inject.components.AppComponent;
import com.phillipsong.gittrending.inject.components.DaggerRepoSearchFragmentComponent;
import com.phillipsong.gittrending.inject.modules.RepoSearchFragmentModule;
import com.phillipsong.gittrending.ui.adapter.RepoSearchAdapter;
import com.phillipsong.gittrending.ui.misc.OnItemClickListener;
import com.phillipsong.gittrending.ui.misc.OnRecScrollListener;
import com.phillipsong.gittrending.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class RepoSearchFragment extends BaseFragment implements OnItemClickListener {

    private static final String TAG = "RepoSearchFragment";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RepoSearchAdapter mAdapter;

    private List<GithubRepo> mRepoList = new ArrayList<>();
    private String mKeyword;
    private int mCurrentPage = 1;
    private int mTotalItems = Constants.PER_ITEMS_COUNT;
    private boolean isLoading;

    @Inject
    TrendingApplication mContext;
    @Inject
    GithubService mGithubApi;

    private Action1<List<GithubRepo>> mUpdateAction = items -> {
        isLoading = false;
        if (mCurrentPage == 1) {
            mRepoList.clear();
        }
        Log.d(TAG, "action1: " + mCurrentPage);
        mCurrentPage += 1;
        mRepoList.addAll(items);
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
        View view = inflater.inflate(R.layout.fragment_repo_search, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresher);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mAdapter = new RepoSearchAdapter(mContext, mRepoList);
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
                    return mGithubApi.searchRepo(mKeyword, mCurrentPage);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(aVoid -> mSwipeRefreshLayout.setRefreshing(false))
                .retry()
                .flatMap(repos -> {
                    mTotalItems = repos.getCount();
                    return Observable.just(repos.getItems());
                })
                .subscribe(mUpdateAction, mThrowableAction);
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
        mGithubApi.searchRepo(keyword, mCurrentPage)
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
    protected void setupFragmentComponent(AppComponent appComponent) {
        DaggerRepoSearchFragmentComponent.builder()
                .appComponent(appComponent)
                .repoSearchFragmentModule(new RepoSearchFragmentModule(this))
                .build()
                .inject(this);
    }

    @Override
    public void onItemClick(int position) {
        if (mRepoList == null || mRepoList.size() == 0)
            return;

        GithubRepo repo = mRepoList.get(position);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(repo.getUrl()));
        startActivity(i);

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName(repo.getFullName())
                .putContentType(TAG));
    }
}
