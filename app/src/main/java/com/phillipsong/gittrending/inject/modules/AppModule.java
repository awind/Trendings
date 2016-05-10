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
package com.phillipsong.gittrending.inject.modules;

import android.content.Context;
import android.content.SharedPreferences;

import com.phillipsong.gittrending.TrendingApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private TrendingApplication mApplication;


    private SharedPreferences mSharedPreferences;

    public AppModule(TrendingApplication application) {
        this.mApplication = application;

        mSharedPreferences = mApplication.getSharedPreferences("app", Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    public TrendingApplication provideTrendingApplication() {
        return mApplication;
    }

    @Singleton
    @Provides
    public SharedPreferences provideSharedPreference() {
        return mSharedPreferences;
    }


}