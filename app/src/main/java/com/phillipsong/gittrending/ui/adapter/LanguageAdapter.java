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
package com.phillipsong.gittrending.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.phillipsong.gittrending.R;
import com.phillipsong.gittrending.data.models.Language;
import com.phillipsong.gittrending.ui.misc.OnLanguageClickListener;

import java.util.List;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

    public static class LanguageViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIcon;
        private TextView mName;
        private SwitchCompat mSwitchCompat;

        public LanguageViewHolder(View view) {
            super(view);
            mIcon = (ImageView) view.findViewById(R.id.language_icon);
            mName = (TextView) view.findViewById(R.id.language_name);
            mSwitchCompat = (SwitchCompat) view.findViewById(R.id.switcher);
        }
    }

    private Context mContext;
    private List<Language> mLanguages;
    private OnLanguageClickListener mListener;


    public LanguageAdapter(Context context, List<Language> languages, OnLanguageClickListener listener) {
        this.mContext = context;
        this.mLanguages = languages;
        this.mListener = listener;
    }

    @Override
    public LanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_item,
                parent, false);
        LanguageViewHolder viewHolder = new LanguageViewHolder(view);
        view.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LanguageViewHolder holder, int position) {
        holder.mSwitchCompat.setOnCheckedChangeListener(null);
        Language language = mLanguages.get(position);
        holder.mName.setText(language.getName());
        Glide.with(mContext)
                .load(language.getIcon())
                .error(R.mipmap.ic_lang)
                .fitCenter()
                .into(holder.mIcon);
        holder.mSwitchCompat.setChecked(language.isSelect());
        holder.mSwitchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("Language2", "onBindViewHolder: " + position + language.getName());
            language.setIsSelect(isChecked);
            if (mListener != null) {
                mListener.onItemClick(position, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLanguages == null ? 0 : mLanguages.size();
    }

}