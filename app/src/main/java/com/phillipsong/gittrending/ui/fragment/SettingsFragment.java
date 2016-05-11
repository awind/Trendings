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


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.phillipsong.gittrending.R;
import com.phillipsong.gittrending.utils.Constants;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView mShare;
    private TextView mRate;
    private TextView mAbout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_fragment_settings);

        mShare = (TextView) view.findViewById(R.id.share_app);
        mRate = (TextView) view.findViewById(R.id.rate_app);
        mAbout = (TextView) view.findViewById(R.id.about_author);
        mShare.setOnClickListener(this);
        mRate.setOnClickListener(this);
        mAbout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.share_app:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.fragment_settings_share_app_content));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.rate_app:
                final Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
                final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);
                if (getContext().getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0) {
                    startActivity(rateAppIntent);
                } else {
                    Toast.makeText(getContext(), "Can not open Market App.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.about_author:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Constants.GITHUB_PROFILE_URL));
                startActivity(i);
                break;
        }
    }
}
