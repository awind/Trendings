package com.phillipsong.gittrending.inject.components;

import com.phillipsong.gittrending.inject.modules.DeveloperActivityModule;
import com.phillipsong.gittrending.ui.activity.DeveloperActivity;
import com.phillipsong.gittrending.ui.misc.ActivityScope;

import dagger.Component;

/**
 * Created by songfei on 16/3/29.
 */
@ActivityScope
@Component(modules = DeveloperActivityModule.class, dependencies = AppComponent.class)
public interface DeveloperActivityComponent {

    DeveloperActivity inject(DeveloperActivity developerActivity);

}
