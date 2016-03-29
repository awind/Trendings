package com.phillipsong.gittrending.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import com.jakewharton.rxbinding.view.RxView;
import com.phillipsong.gittrending.R;
import com.phillipsong.gittrending.inject.components.AppComponent;

/**
 * Created by songfei on 16/3/29.
 */
public class BaseNaviActivity extends BaseActivity {

    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mActionBarDrawerToggle;
    protected NavigationView mNavigationView;
    protected Toolbar mToolbar;


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        onCreateDrawer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void onCreateDrawer() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        RxView.clicks(fab)
                .subscribe(aVoid -> {
                    Intent intent = new Intent(this, FavoriteActivity.class);
                    startActivity(intent);
                });
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

}
