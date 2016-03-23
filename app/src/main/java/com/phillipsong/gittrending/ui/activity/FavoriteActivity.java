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
package com.phillipsong.gittrending.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.ShareEvent;
import com.jakewharton.rxbinding.support.v7.widget.RxToolbar;
import com.phillipsong.gittrending.AppComponent;
import com.phillipsong.gittrending.R;
import com.phillipsong.gittrending.TrendingApplication;
import com.phillipsong.gittrending.data.models.Repo;
import com.phillipsong.gittrending.ui.adapter.RepoAdapter;
import com.phillipsong.gittrending.ui.misc.OnRepoItemClickListener;
import com.phillipsong.gittrending.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

public class FavoriteActivity extends BaseActivity implements OnRepoItemClickListener {

    private static final String TAG = "FavoriteActivity";
    @Inject
    TrendingApplication mContext;
    @Inject
    Realm mRealm;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private RepoAdapter mRepoAdapter;
    private List<Repo> mRepoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked);

        initViews();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerFavoriteActivityComponent.builder()
                .appComponent(appComponent)
                .favoriteActivityModule(new FavoriteActivityModule(this))
                .build()
                .inject(this);
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.activity_favorite_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RxToolbar.navigationClicks(mToolbar)
                .subscribe(i -> finish());

        mRecyclerView = (RecyclerView) findViewById(R.id.favorite_list);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(manager);
        mRepoList = mRealm.where(Repo.class).findAll();
        mRepoAdapter = new RepoAdapter(mContext, mRepoList, this);
        mRecyclerView.setAdapter(mRepoAdapter);
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
                .putContentName(repo.getUrl()));
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
                        repos.removeLast();
                        mRealm.commitTransaction();
                    } else if (repos.size() == 0) {
                        mRealm.beginTransaction();
                        repo.setIsFavorited(true);
                        mRealm.copyToRealm(repo);
                        mRealm.commitTransaction();
                    }
                    mRepoAdapter.notifyDataSetChanged();
                });

        Answers.getInstance().logCustom(new CustomEvent("Favorite")
                .putCustomAttribute("name", repo.getUrl()));
    }
}