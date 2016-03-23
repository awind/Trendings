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

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.support.v7.widget.RxToolbar;
import com.phillipsong.gittrending.R;
import com.phillipsong.gittrending.TrendingApplication;
import com.phillipsong.gittrending.data.api.TrendingService;
import com.phillipsong.gittrending.data.models.Language;
import com.phillipsong.gittrending.data.models.Support;
import com.phillipsong.gittrending.inject.components.AppComponent;
import com.phillipsong.gittrending.inject.components.DaggerLanguagesActivityComponent;
import com.phillipsong.gittrending.inject.modules.LanguagesActivityModule;
import com.phillipsong.gittrending.ui.adapter.LanguageAdapter;
import com.phillipsong.gittrending.ui.misc.OnLanguageClickListener;
import com.phillipsong.gittrending.ui.widget.PSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LanguagesActivity extends BaseActivity implements OnLanguageClickListener {

    @Inject
    TrendingApplication mContext;
    @Inject
    TrendingService mTrendingApi;
    @Inject
    Realm mRealm;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private PSwipeRefreshLayout mSwipeRefreshLayout;
    private LanguageAdapter mLanguageAdapter;

    private List<Language> mLanguageList = new ArrayList<>();
    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages);
        initViews();
        updateData();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerLanguagesActivityComponent.builder()
                .appComponent(appComponent)
                .languagesActivityModule(new LanguagesActivityModule(this))
                .build()
                .inject(this);
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.languages_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RxToolbar.navigationClicks(mToolbar)
                .subscribe(aVoid -> onBackPressed());

        mSwipeRefreshLayout = (PSwipeRefreshLayout) findViewById(R.id.refresher);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        RxSwipeRefreshLayout.refreshes(mSwipeRefreshLayout)
                .compose(bindToLifecycle())
                .observeOn(Schedulers.io())
                .flatMap(aVoid -> mTrendingApi.getSupport())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(aVoid -> mSwipeRefreshLayout.setRefreshing(false))
                .retry()
                .flatMap(support -> checkLanguage(support))
                .subscribe(support -> {
                    mLanguageList.clear();
                    mLanguageList.addAll(support.getItems());
                    mLanguageAdapter.notifyDataSetChanged();
                });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mLanguageAdapter = new LanguageAdapter(mContext, mLanguageList, this);
        mRecyclerView.setAdapter(mLanguageAdapter);
    }

    private void updateData() {
        mTrendingApi.getSupport()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> mSwipeRefreshLayout.setRefreshing(true))
                .doOnCompleted(() -> mSwipeRefreshLayout.setRefreshing(false))
                .doOnError(error -> mSwipeRefreshLayout.setRefreshing(false))
                .flatMap(support -> checkLanguage(support))
                .subscribe(support -> {
                    mLanguageList.clear();
                    mLanguageList.addAll(support.getItems());
                    mLanguageAdapter.notifyDataSetChanged();
                }, error -> {
                });
    }

    private Observable<Support> checkLanguage(Support support) {
        for (Language language : support.getItems()) {
            RealmResults<Language> languages = mRealm.where(Language.class)
                    .equalTo("name", language.getName()).findAll();
            if (languages.size() > 0) {
                language.setIsSelect(true);
            }
        }
        return Observable.just(support);
    }

    @Override
    public void onItemClick(int position) {
        if (mLanguageList == null || mLanguageList.size() == 0) {
            return;
        }

        Language language = mLanguageList.get(position);
        RealmResults<Language> languages = mRealm.where(Language.class)
                .equalTo("name", language.getName()).findAll();
        mRealm.beginTransaction();
        if (languages.size() > 0) {
            language.setIsSelect(false);
            languages.removeLast();
        } else {
            language.setIsSelect(true);
            mRealm.copyToRealm(language);
        }
        mRealm.commitTransaction();
        isChanged = true;
        mLanguageAdapter.notifyItemChanged(position);
    }

    @Override
    public void onBackPressed() {
        if (isChanged) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }
}