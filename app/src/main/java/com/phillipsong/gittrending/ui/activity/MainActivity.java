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
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.phillipsong.gittrending.AppComponent;
import com.phillipsong.gittrending.R;
import com.phillipsong.gittrending.TrendingApplication;
import com.phillipsong.gittrending.data.api.TrendingService;
import com.phillipsong.gittrending.ui.adapter.ViewPagerAdapter;
import com.phillipsong.gittrending.ui.fragment.RepoFragment;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @Inject
    TrendingApplication mContext;
    @Inject
    TrendingService mTrendingApi;

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ViewPagerAdapter mPagerAdapter;

    private String mSince;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainActivityComponent.builder()
                .appComponent(appComponent)
                .mainActivityModule(new MainActivityModule(this))
                .build()
                .inject(this);
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        setupViewPager();
        mTabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        RxView.clicks(fab)
                .subscribe(aVoid -> {
                    Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                    startActivity(intent);
                });

        View spinnerContainer = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner,
                mToolbar, false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mToolbar.addView(spinnerContainer, lp);

        SinceSpinnerAdapter spinnerAdapter = new SinceSpinnerAdapter();

        Spinner spinner = (Spinner) spinnerContainer.findViewById(R.id.toolbar_spinner);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // daily
                        mSince = "daily";
                        break;
                    case 1: // weekly
                        mSince = "weekly";
                        break;
                    case 2: // monthly
                        mSince = "monthly";
                        break;

                }

                for (int i = 0; i < mPagerAdapter.getCount(); i++) {
                    Fragment fragment = getSupportFragmentManager()
                            .findFragmentByTag(mPagerAdapter.getFragmentTag(R.id.view_pager, i));
                    if (fragment instanceof RepoFragment) {
                        if (fragment.isAdded()) {
                            ((RepoFragment) fragment).updateData(mSince);
                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupViewPager() {
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(RepoFragment.newInstance("all", "daily"), "All");
        mPagerAdapter.addFragment(RepoFragment.newInstance("java", "daily"), "Java");
        mPagerAdapter.addFragment(RepoFragment.newInstance("swift", "daily"), "Swift");
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, LanguagesActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SinceSpinnerAdapter extends BaseAdapter {

        final String[] timeSpanTextArray = new String[]{"Today", "This Week", "This Month"};

        @Override
        public int getCount() {
            return timeSpanTextArray.length;
        }

        @Override
        public String getItem(int position) {
            return timeSpanTextArray[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView != null ? convertView :
                    getLayoutInflater().inflate(R.layout.toolbar_spinner_item_actionbar, parent, false);

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText("Trending " + getItem(position));
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            View view = convertView != null ? convertView :
                    getLayoutInflater().inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }
    }
}
