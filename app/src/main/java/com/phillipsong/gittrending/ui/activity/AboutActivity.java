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
import android.widget.TextView;

import com.jakewharton.rxbinding.support.v7.widget.RxToolbar;
import com.phillipsong.gittrending.BuildConfig;
import com.phillipsong.gittrending.R;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

public class AboutActivity extends RxAppCompatActivity {

    private Toolbar mToolbar;
    private WebView mWebView;
    private TextView mVersionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initViews();
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        RxToolbar.navigationClicks(mToolbar)
                .subscribe(aVoid -> finish());
        mVersionTv = (TextView) findViewById(R.id.version);
        mVersionTv.setText(String.format(getString(R.string.version), BuildConfig.VERSION_NAME,
                BuildConfig.BUILD_TYPE));

        mWebView = (WebView) findViewById(R.id.webview);
        final String template = getString(R.string.about_page)
                .replace("{{fork_me_on_github}}", getString(R.string.fork_me_on_github))
                .replace("{{about_trendings}}", getString(R.string.about_title))
                .replace("{{about_trendings_text}}", getString(R.string.about_trendings_text))
                .replace("{{libraries_used}}", getString(R.string.libraries_used));

        mWebView.loadData(template, "text/html; charset=utf-8", null);

    }
}