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
package com.phillipsong.gittrending.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import com.jakewharton.rxbinding.support.v7.widget.RxToolbar;
import com.phillipsong.gittrending.R;
import com.phillipsong.gittrending.inject.components.AppComponent;
import com.phillipsong.gittrending.inject.components.DaggerAboutActivityComponent;
import com.phillipsong.gittrending.inject.modules.AboutActivityModule;

public class AboutActivity extends BaseActivity {

    private Toolbar mToolbar;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initViews();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerAboutActivityComponent.builder()
                .appComponent(appComponent)
                .aboutActivityModule(new AboutActivityModule(this))
                .build()
                .inject(this);
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        RxToolbar.navigationClicks(mToolbar)
                .subscribe(aVoid -> finish());

        mWebView = (WebView) findViewById(R.id.webview);
        final String template = getString(R.string.about_page)
                .replace("{{fork_me_on_github}}", getString(R.string.fork_me_on_github))
                .replace("{{about_trendings}}", getString(R.string.about_title))
                .replace("{{about_trendings_text}}", getString(R.string.about_trendings_text))
                .replace("{{libraries_used}}", getString(R.string.libraries_used));

        mWebView.loadData(template, "text/html; charset=utf-8", null);

    }
}