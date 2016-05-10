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
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.phillipsong.gittrending.R;
import com.phillipsong.gittrending.TrendingApplication;
import com.phillipsong.gittrending.data.api.TrendingService;
import com.phillipsong.gittrending.data.models.Repo;
import com.phillipsong.gittrending.inject.components.AppComponent;
import com.phillipsong.gittrending.inject.components.DaggerRepoFragmentComponent;
import com.phillipsong.gittrending.inject.modules.RepoFragmentModule;
import com.phillipsong.gittrending.ui.adapter.RepoAdapter;
import com.phillipsong.gittrending.ui.misc.OnItemClickListener;
import com.phillipsong.gittrending.ui.widget.PSwipeRefreshLayout;
import com.phillipsong.gittrending.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class RepoFragment extends BaseFragment implements OnItemClickListener {

    private static final String TAG = "RepoFragment";

    private static final String LANGUAGE = "language";
    private static final String SINCE = "since";

    private PSwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RepoAdapter mRepoAdapter;
    private List<Repo> mRepoList;

    @Inject
    TrendingApplication mContext;
    @Inject
    TrendingService mTrendingApi;

    private String mLanguage;
    private String mSince;


    private Action1<List<Repo>> mUpdateAction = items -> {
        mRepoList.clear();
        mRepoList.addAll(items);
        mRepoAdapter.notifyDataSetChanged();
    };

    private Action1<Throwable> mThrowableAction = throwable ->
            Answers.getInstance().logCustom(new CustomEvent("UpdateException")
            .putCustomAttribute("location", TAG)
            .putCustomAttribute("message", throwable.getMessage()));

    public static RepoFragment newInstance(String language, String since) {
        RepoFragment fragment = new RepoFragment();

        Bundle args = new Bundle();
        args.putString(LANGUAGE, language);
        args.putString(SINCE, since);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLanguage = getArguments().getString(LANGUAGE);
        mSince = getArguments().getString(SINCE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repo, container, false);
        initViews(view);
        return view;
    }

    @Override
    protected void setupFragmentComponent(AppComponent appComponent) {
        DaggerRepoFragmentComponent.builder()
                .appComponent(appComponent)
                .repoFragmentModule(new RepoFragmentModule(this))
                .build()
                .inject(this);
    }

    private void initViews(View view) {
        mSwipeRefreshLayout = (PSwipeRefreshLayout) view.findViewById(R.id.refresher);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.CYAN);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mRecyclerView.setNestedScrollingEnabled(true);
        }
        mRepoList = new ArrayList<>();
        mRepoAdapter = new RepoAdapter(mContext, mRepoList, this);
        mRecyclerView.setAdapter(mRepoAdapter);

        updateData(mLanguage, mSince);

        RxSwipeRefreshLayout.refreshes(mSwipeRefreshLayout)
                .compose(bindToLifecycle())
                .observeOn(Schedulers.io())
                .flatMap(aVoid -> mTrendingApi.getTrending(mLanguage, mSince))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(aVoid -> mSwipeRefreshLayout.setRefreshing(false))
                .retry()
                .flatMap(response -> Observable.just(response.getItems()))
                .subscribe(mUpdateAction, mThrowableAction);
    }

    public void updateData(String language, String since) {
        mLanguage = language;
        mSince = since;
        mTrendingApi.getTrending(mLanguage, mSince)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> mSwipeRefreshLayout.setRefreshing(true))
                .doOnCompleted(() -> mSwipeRefreshLayout.setRefreshing(false))
                .doOnError(error -> mSwipeRefreshLayout.setRefreshing(false))
                .flatMap(response -> Observable.just(response.getItems()))
                .subscribe(mUpdateAction, mThrowableAction);
    }


    @Override
    public void onItemClick(int position) {
        if (mRepoList == null || mRepoList.size() == 0) {
            return;
        }
        Repo repo = mRepoList.get(position);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(Constants.GITHUB_BASE_URL + repo.getUrl()));
        startActivity(i);

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName(repo.getUrl())
                .putContentType(TAG));
    }

}