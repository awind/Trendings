package com.phillipsong.gittrending.ui.activity;

import android.content.Intent;
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
import com.phillipsong.gittrending.data.models.Language;
import com.phillipsong.gittrending.inject.components.AppComponent;
import com.phillipsong.gittrending.inject.components.DaggerDeveloperActivityComponent;
import com.phillipsong.gittrending.inject.modules.DeveloperActivityModule;
import com.phillipsong.gittrending.ui.adapter.DevelopersViewPagerAdapter;
import com.phillipsong.gittrending.ui.fragment.DeveloperFragment;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

public class DeveloperActivity extends BaseNaviActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_LANGUAGE = 10;

    @Inject
    TrendingApplication mContext;
    @Inject
    TrendingService mTrendingApi;
    @Inject
    Realm mRealm;


    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private DevelopersViewPagerAdapter mPagerAdapter;

    private String mSince = "daily";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(1).setChecked(true);
        initViews();
    }

    private void initViews() {
        mToolbar.setTitle(R.string.title_activity_developer);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mPagerAdapter = new DevelopersViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        setupViewPager();
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager() {
        clearFragment();
        RealmResults<Language> languages = mRealm.where(Language.class).findAll();
        if (languages.size() > 0) {
            for (Language language : languages) {
                mPagerAdapter.addFragment(language.getName().toLowerCase());
            }
        } else {
            mPagerAdapter.addFragment("all");
            mPagerAdapter.addFragment("java");
            mPagerAdapter.addFragment("swift");
        }
        mPagerAdapter.setSince(mSince);
        mPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(0);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void clearFragment() {
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            Fragment fragment = getSupportFragmentManager()
                    .findFragmentByTag(mPagerAdapter.getFragmentTag(R.id.view_pager, i));
            if (fragment instanceof DeveloperFragment) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
        mPagerAdapter.clearFragmentList();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerDeveloperActivityComponent.builder()
                .appComponent(appComponent)
                .developerActivityModule(new DeveloperActivityModule(this))
                .build()
                .inject(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            if (fragment instanceof DeveloperFragment) {
                if (fragment.isAdded()) {
                    ((DeveloperFragment) fragment).updateData(mSince);
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
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.nav_developer) {

        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, LanguagesActivity.class);
            startActivityForResult(intent, REQUEST_LANGUAGE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
