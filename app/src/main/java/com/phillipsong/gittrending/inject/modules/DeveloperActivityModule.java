package com.phillipsong.gittrending.inject.modules;

import com.phillipsong.gittrending.ui.activity.DeveloperActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by songfei on 16/3/29.
 */
@Module
public class DeveloperActivityModule {

    private DeveloperActivity mDeveloperActivity;

    public DeveloperActivityModule(DeveloperActivity developerActivity) {
        this.mDeveloperActivity = developerActivity;
    }

    @Provides
    @Singleton
    DeveloperActivity provideDeveloperActivity() {
        return mDeveloperActivity;
    }
}
