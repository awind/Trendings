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
import com.phillipsong.gittrending.ui.widget.StringPickerDialog;
import com.phillipsong.gittrending.utils.Constants;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabClickListener;

import javax.inject.Inject;


public class MainActivity extends BaseActivity implements StringPickerDialog.OnClickListener {

    private static final String TAG = "MainActivity";

    private static final String IS_USED = "used";

    @Inject
    TrendingApplication mContext;
    @Inject
    TrendingService mTrendingApi;
    @Inject
    SharedPreferences mSharedPreferences;

    private Toolbar mToolbar;
    private TextView mTitleTv;
    private ImageButton mLangBtn;
    private ImageButton mSinceBtn;
    private BottomBar mBottomBar;
    private RepoFragment mRepoFragment;
    private DeveloperFragment mDevFragment;

    private String mLanguage = "All";
    private String mSince = "Daily";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
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
                        if (mRepoFragment == null) {
                            mRepoFragment = RepoFragment.newInstance(mLanguage.toLowerCase(), mSince.toLowerCase());
                        }
                        transaction.replace(R.id.main_content, mRepoFragment);
                        break;
                    case 1:
                        if (mDevFragment == null) {
                            mDevFragment = DeveloperFragment.newInstance(mLanguage.toLowerCase(), mSince.toLowerCase());
                        }
                        transaction.replace(R.id.main_content, mDevFragment);
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

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleTv = (TextView) findViewById(R.id.title);
        mTitleTv.setText(mLanguage);
        setSupportActionBar(mToolbar);

        mLangBtn = (ImageButton) findViewById(R.id.language_btn);
        mSinceBtn = (ImageButton) findViewById(R.id.since_btn);
        mLangBtn.setOnClickListener(v -> {
            StringPickerDialog dialog = new StringPickerDialog();
            Bundle bundle = new Bundle();
            bundle.putStringArray(getString(R.string.string_picker_dialog_values), Constants.LANGUAGE_LIST);
            bundle.putInt(getString(R.string.string_picker_dialog_current_index), 2);
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), TAG);
        });
        mSinceBtn.setOnClickListener(v -> {

        });
    }

    @Override
    public void onClick(String value) {
        mLanguage = value;
        mTitleTv.setText(mLanguage);
        mRepoFragment.updateData(mLanguage.toLowerCase(), mSince.toLowerCase());
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
