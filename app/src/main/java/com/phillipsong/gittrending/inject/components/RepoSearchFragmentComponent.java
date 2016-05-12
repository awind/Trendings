package com.phillipsong.gittrending.inject.components;

import com.phillipsong.gittrending.inject.modules.RepoSearchFragmentModule;
import com.phillipsong.gittrending.ui.fragment.RepoSearchFragment;
import com.phillipsong.gittrending.ui.misc.PerFragment;

import dagger.Component;

/**
 * Created by fei on 16/5/12.
 */
@PerFragment
@Component(modules = {RepoSearchFragmentModule.class}, dependencies = {AppComponent.class})
public interface RepoSearchFragmentComponent extends MainActivityComponent {

    void inject(RepoSearchFragment fragment);

}