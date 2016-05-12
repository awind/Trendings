package com.phillipsong.gittrending.inject.modules;

import com.phillipsong.gittrending.ui.fragment.RepoSearchFragment;
import com.phillipsong.gittrending.ui.misc.PerFragment;

import dagger.Module;
import dagger.Provides;

/**
 * Created by fei on 16/5/12.
 */

@Module
public class RepoSearchFragmentModule {

    RepoSearchFragment mRepoSearchFragment;

    public RepoSearchFragmentModule(RepoSearchFragment fragment) {
        this.mRepoSearchFragment = fragment;
    }

    @Provides
    @PerFragment
    RepoSearchFragment provideRepoFragment() {
        return mRepoSearchFragment;
    }
}
