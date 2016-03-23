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
package com.phillipsong.gittrending;

import android.app.Application;
import android.content.Context;

import com.phillipsong.gittrending.data.api.ApiModule;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class TrendingApplication extends Application {

    private AppComponent mAppComponent;

    public static TrendingApplication get(Context context){
        return (TrendingApplication)context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(false)
                .build();
        Fabric.with(fabric);

        // Dagger
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .apiModule(new ApiModule())
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

}