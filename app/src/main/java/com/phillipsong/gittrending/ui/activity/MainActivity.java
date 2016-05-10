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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.phillipsong.gittrending.R;
import com.phillipsong.gittrending.TrendingApplication;
import com.phillipsong.gittrending.data.api.TrendingService;
import com.phillipsong.gittrending.inject.components.AppComponent;
import com.phillipsong.gittrending.inject.components.DaggerMainActivityComponent;
import com.phillipsong.gittrending.inject.modules.MainActivityModule;
import com.phillipsong.gittrending.ui.adapter.RepoViewPagerAdapter;
import com.phillipsong.gittrending.ui.fragment.RepoFragment;

import javax.inject.Inject;


public class MainActivity extends BaseNaviActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private static final String IS_USED = "used";
    private static final int REQUEST_LANGUAGE = 10;

    @Inject
    TrendingApplication mContext;
    @Inject
    TrendingService mTrendingApi;
    @Inject
    SharedPreferences mSharedPreferences;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private RepoViewPagerAdapter mPagerAdapter;

    private String mSince = "daily";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.onCreateDrawer();
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);
        boolean isUsed = mSharedPreferences.getBoolean(IS_USED, false);
        if (!isUsed) {
            mSharedPreferences.edit().putBoolean(IS_USED, true).apply();
        }
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

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initViews() {
        mToolbar.setTitle(R.string.title_activity_repo);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mPagerAdapter = new RepoViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        setupViewPager();
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager() {
        clearFragment();

        mPagerAdapter.addFragment("all");
        mPagerAdapter.addFragment("java");
        mPagerAdapter.addFragment("swift");

        mPagerAdapter.setSince(mSince);
        mPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(0);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void clearFragment() {
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            Fragment fragment = getSupportFragmentManager()
                    .findFragmentByTag(mPagerAdapter.getFragmentTag(R.id.view_pager, i));
            if (fragment instanceof RepoFragment) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
        mPagerAdapter.clearFragmentList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void updateSince() {
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            Fragment fragment = getSupportFragmentManager()
                    .findFragmentByTag(mPagerAdapter.getFragmentTag(R.id.view_pager, i));
            if (fragment instanceof RepoFragment) {
                if (fragment.isAdded()) {
                    ((RepoFragment) fragment).updateData(mSince);
                }
            }
        }
        mPagerAdapter.setSince(mSince);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mSince = item.getTitle().toString();
        updateSince();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LANGUAGE && resultCode == RESULT_OK) {
            setupViewPager();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_trending) {
        } else if (id == R.id.nav_developer) {
            Intent intent = new Intent(this, DeveloperActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
