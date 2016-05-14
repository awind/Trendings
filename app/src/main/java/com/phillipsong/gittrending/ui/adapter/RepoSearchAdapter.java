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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.phillipsong.gittrending.R;
import com.phillipsong.gittrending.data.models.GithubRepo;
import com.phillipsong.gittrending.ui.misc.OnItemClickListener;

import java.util.List;

public class RepoSearchAdapter extends RecyclerView.Adapter<RepoSearchAdapter.RepoSearchViewHolder> {


    private Context mContext;
    private List<GithubRepo> mRepoList;
    private OnItemClickListener mListener;

    public RepoSearchAdapter(Context context, List<GithubRepo> list) {
        mContext = context;
        mRepoList = list;
    }

    @Override
    public RepoSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_repo, parent, false);
        RepoSearchViewHolder viewHolder = new RepoSearchViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RepoSearchViewHolder holder, int position) {
        GithubRepo repo = mRepoList.get(position);
        holder.bindItem(mContext, repo);

        holder.itemView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onItemClick(position);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mRepoList.size();
    }

    public static class RepoSearchViewHolder extends RecyclerView.ViewHolder {
        private ImageView mRepoIcon;
        private TextView mRepoTitle;
        private TextView mRepoDesc;
        private TextView mRepoLang;
        private TextView mRepoStars;

        public RepoSearchViewHolder(View view) {
            super(view);
            mRepoIcon = (ImageView) view.findViewById(R.id.repo_icon);
            mRepoTitle = (TextView) view.findViewById(R.id.repo_title);
            mRepoDesc = (TextView) view.findViewById(R.id.repo_desc);
            mRepoLang = (TextView) view.findViewById(R.id.repo_lang);
            mRepoStars = (TextView) view.findViewById(R.id.repo_stars);
        }

        public void bindItem(Context context, GithubRepo repo) {
            Glide.with(context)
                    .load(repo.getOwner().getAvatar())
                    .placeholder(R.mipmap.ic_lang)
                    .error(R.mipmap.ic_lang)
                    .fitCenter()
                    .into(mRepoIcon);
            mRepoTitle.setText(repo.getFullName());
            mRepoDesc.setText(repo.getDescription());
            mRepoLang.setText(repo.getLanguage());
            mRepoStars.setText(String.valueOf(repo.getStars()));
        }
    }
}
