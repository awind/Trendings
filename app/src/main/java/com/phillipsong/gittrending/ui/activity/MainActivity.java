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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.phillipsong.gittrending.R;
import com.phillipsong.gittrending.TrendingApplication;
import com.phillipsong.gittrending.data.api.TrendingService;
import com.phillipsong.gittrending.inject.components.AppComponent;
import com.phillipsong.gittrending.inject.components.DaggerMainActivityComponent;
import com.phillipsong.gittrending.inject.modules.MainActivityModule;
import com.phillipsong.gittrending.ui.fragment.DeveloperFragment;
import com.phillipsong.gittrending.ui.fragment.RepoFragment;
import com.phillipsong.gittrending.ui.fragment.SearchFragment;
import com.phillipsong.gittrending.ui.fragment.SettingsFragment;
import com.phillipsong.gittrending.ui.widget.StringPicker;
import com.phillipsong.gittrending.utils.Constants;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabClickListener;

import java.util.List;

import javax.inject.Inject;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private static final String IS_USED = "used";

    @Inject
    TrendingApplication mContext;
    @Inject
    TrendingService mTrendingApi;
    @Inject
    SharedPreferences mSharedPreferences;

    private BottomBar mBottomBar;
    private RepoFragment mRepoFragment;
    private DeveloperFragment mDevFragment;
    private SearchFragment mSearchFragment;
    private SettingsFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.myCoordinator),
                null, savedInstanceState);

        initViews();

        boolean isUsed = mSharedPreferences.getBoolean(IS_USED, false);
        if (!isUsed) {
            mSharedPreferences.edit().putBoolean(IS_USED, true).apply();
        }
    }

    private void initViews() {
        mBottomBar.setItems(
                new BottomBarTab(R.mipmap.ic_repos, "Repository"),
                new BottomBarTab(R.mipmap.ic_users, "Developer"),
                new BottomBarTab(R.mipmap.ic_search, "Search"),
                new BottomBarTab(R.mipmap.ic_settings, "Settings")
        );
        mBottomBar.mapColorForTab(0, ContextCompat.getColor(mContext, R.color.bottom_bar_tab_0));
        mBottomBar.mapColorForTab(1, ContextCompat.getColor(mContext, R.color.bottom_bar_tab_1));
        mBottomBar.mapColorForTab(2, ContextCompat.getColor(mContext, R.color.bottom_bar_tab_2));
        mBottomBar.mapColorForTab(3, ContextCompat.getColor(mContext, R.color.bottom_bar_tab_3));

        mBottomBar.setOnTabClickListener(new OnTabClickListener() {
            @Override
            public void onTabSelected(int position) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (position) {
                    case 0:
                        hideFragment();
                        if (mRepoFragment == null) {
                            mRepoFragment = new RepoFragment();
                            transaction.add(R.id.main_content, mRepoFragment);
                        } else {
                            transaction.show(mRepoFragment);
                        }
                        break;
                    case 1:
                        hideFragment();
                        if (mDevFragment == null) {
                            mDevFragment = new DeveloperFragment();
                            transaction.add(R.id.main_content, mDevFragment);
                        } else {
                            transaction.show(mDevFragment);
                        }
                        break;
                    case 2:
                        hideFragment();
                        if (mSearchFragment == null) {
                            mSearchFragment = new SearchFragment();
                            transaction.add(R.id.main_content, mSearchFragment);
                        } else {
                            transaction.show(mSearchFragment);
                        }
                        break;
                    case 3:
                        hideFragment();
                        if (mSettingsFragment == null) {
                            mSettingsFragment = new SettingsFragment();
                            transaction.add(R.id.main_content, mSettingsFragment);
                        } else {
                            transaction.show(mSettingsFragment);
                        }
                        break;
                    default:
                        break;
                }
                transaction.commit();
            }

            @Override
            public void onTabReSelected(int position) {

            }
        });
    }

    private void hideFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null) {
            return;
        }
        for (Fragment fragment : fragments) {
            transaction.hide(fragment);
        }
        transaction.commit();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainActivityComponent.builder()
                .appComponent(appComponent)
                .mainActivityModule(new MainActivityModule(this))
                .build()
                .inject(this);
    }
}
