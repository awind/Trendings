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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.ShareEvent;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.phillipsong.gittrending.AppComponent;
import com.phillipsong.gittrending.R;
import com.phillipsong.gittrending.TrendingApplication;
import com.phillipsong.gittrending.data.api.TrendingService;
import com.phillipsong.gittrending.data.models.Repo;
import com.phillipsong.gittrending.data.models.Trending;
import com.phillipsong.gittrending.ui.adapter.RepoAdapter;
import com.phillipsong.gittrending.ui.misc.OnRepoItemClickListener;
import com.phillipsong.gittrending.ui.widget.PSwipeRefreshLayout;
import com.phillipsong.gittrending.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RepoFragment extends BaseFragment implements OnRepoItemClickListener {

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
    @Inject
    Realm mRealm;

    private String mLanguage;
    private String mSince;

    public static RepoFragment newInstance(String language, String since) {
        RepoFragment fragment = new RepoFragment();

        Bundle args = new Bundle();
        args.putString(LANGUAGE, language);
        args.putString(SINCE, since);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repo, container, false);
        mLanguage = getArguments().getString(LANGUAGE);
        mSince = getArguments().getString(SINCE);
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
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mRecyclerView.setNestedScrollingEnabled(true);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mRepoList = new ArrayList<>();
        mRepoAdapter = new RepoAdapter(mContext, mRepoList, this);
        mRecyclerView.setAdapter(mRepoAdapter);

        updateData(mSince);

        RxSwipeRefreshLayout.refreshes(mSwipeRefreshLayout)
                .compose(bindToLifecycle())
                .observeOn(Schedulers.io())
                .flatMap(aVoid -> mTrendingApi.getTrending(mLanguage, mSince))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(aVoid -> mSwipeRefreshLayout.setRefreshing(false))
                .retry()
                .flatMap(trending -> checkFavorite(trending))
                .subscribe(trending -> {
                    mRepoList.clear();
                    mRepoList.addAll(trending.getItems());
                    mRepoAdapter.notifyDataSetChanged();
                });
    }

    public void updateData(String since) {
        mTrendingApi.getTrending(mLanguage, since)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> mSwipeRefreshLayout.setRefreshing(true))
                .doOnCompleted(() -> mSwipeRefreshLayout.setRefreshing(false))
                .doOnError(error -> mSwipeRefreshLayout.setRefreshing(false))
                .flatMap(trending -> checkFavorite(trending))
                .subscribe(trending -> {
                    mRepoList.clear();
                    mRepoList.addAll(trending.getItems());
                    mRepoAdapter.notifyDataSetChanged();
                }, error->{
                    Log.d(TAG, "updateData: " + error.getMessage());
                });
    }

    @Override
    public void onFavoriteClick(int position) {
        if (mRepoList == null || mRepoList.size() == 0) {
            return;
        }

        Repo repo = mRepoList.get(position);
        mRealm.where(Repo.class).equalTo("url", repo.getUrl()).findAllAsync()
                .asObservable()
                .filter(RealmResults::isLoaded)
                .first()
                .subscribe(repos -> {
                    if (repos.size() > 0) {
                        mRealm.beginTransaction();
                        repo.setIsFavorited(false);
                        repos.removeLast();
                        mRealm.commitTransaction();
                    } else if (repos.size() == 0) {
                        mRealm.beginTransaction();
                        repo.setIsFavorited(true);
                        mRealm.copyToRealm(repo);
                        mRealm.commitTransaction();
                    }
                    mRepoAdapter.notifyItemChanged(position);
                });

        Answers.getInstance().logCustom(new CustomEvent("Favorite")
                .putCustomAttribute("name", repo.getUrl())
                .putCustomAttribute("type", mLanguage));
    }

    @Override
    public void onShareClick(int position) {
        if (mRepoList == null || mRepoList.size() == 0) {
            return;
        }
        Repo repo = mRepoList.get(position);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, Constants.GITHUB_BASE_URL + repo.getUrl());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

        Answers.getInstance().logShare(new ShareEvent()
                .putContentType(TAG)
                .putContentId(mLanguage)
                .putContentName(repo.getUrl()));
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

    private Observable<Trending> checkFavorite(Trending trending) {
        if (trending == null) {
            return Observable.just(null);
        }
        for (Repo repo : trending.getItems()) {
            RealmResults<Repo> repos = mRealm.where(Repo.class)
                    .equalTo("url", repo.getUrl()).findAll();
            if (repos.size() > 0) {
                repo.setIsFavorited(true);
            }
        }
        return Observable.just(trending);
    }

}