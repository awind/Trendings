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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.phillipsong.gittrending.R;
import com.phillipsong.gittrending.ui.adapter.SectionsPagerAdapter;

public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = "SearchFragment";

    private Toolbar mToolbar;
    private SearchView mSearchView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private RepoSearchFragment mRepoSearchFragment;
    private DevSearchFragment mDevSearchFragment;

    private int mCurrentTabPosition = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mSearchView = (SearchView) view.findViewById(R.id.search);
        mSearchView.onActionViewExpanded();
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getContext().getString(R.string.fragment_search_repos_hint));

        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mViewPager = (ViewPager) view.findViewById(R.id.container);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mRepoSearchFragment = new RepoSearchFragment();
        mDevSearchFragment = new DevSearchFragment();
        mSectionsPagerAdapter.addFragment(mRepoSearchFragment,
                "Repo");
        mSectionsPagerAdapter.addFragment(mDevSearchFragment,
                "Developer");
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentTabPosition = position;
                if (position == 0) {
                    mSearchView.setQueryHint(getContext().getString(R.string.fragment_search_repos_hint));
                } else {
                    mSearchView.setQueryHint(getContext().getString(R.string.fragment_search_users_hint));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        switch (mCurrentTabPosition) {
            case 0:
                mRepoSearchFragment.search(query);
                break;
            case 1:
                mDevSearchFragment.search(query);
                break;
            default:
                break;
        }
        return true;
    }
}
