package com.phillipsong.gittrending.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phillipsong.gittrending.R;
import com.phillipsong.gittrending.data.api.GithubService;
import com.phillipsong.gittrending.inject.components.AppComponent;
import com.phillipsong.gittrending.inject.components.DaggerRepoSearchFragmentComponent;
import com.phillipsong.gittrending.inject.modules.RepoSearchFragmentModule;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by fei on 16/5/12.
 */
public class RepoSearchFragment extends BaseFragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    @Inject
    GithubService mGithubApi;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repo_search, container, false);
        initViews(view);
        search();
        return view;
    }

    private void initViews(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresher);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    }

    private void search() {
        mGithubApi.searchRepo("swift")
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> mSwipeRefreshLayout.setRefreshing(true))
                .doOnCompleted(() -> mSwipeRefreshLayout.setRefreshing(false))
                .doOnError(error -> mSwipeRefreshLayout.setRefreshing(false))
                .flatMap(response -> Observable.just(response.getItems()))
                .subscribe(System.out::println);
    }

    @Override
    protected void setupFragmentComponent(AppComponent appComponent) {
        DaggerRepoSearchFragmentComponent.builder()
                .appComponent(appComponent)
                .repoSearchFragmentModule(new RepoSearchFragmentModule(this))
                .build()
                .inject(this);
    }
}
