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
import com.phillipsong.gittrending.data.models.User;
import com.phillipsong.gittrending.ui.misc.OnDevItemClickListener;

import java.util.List;

public class DeveloperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class DeveloperViewHolder extends RecyclerView.ViewHolder {
        private TextView mRank;
        private ImageView mAvatar;
        private TextView mName;
        private TextView mFullName;
        private TextView mRepoName;
        private TextView mRepoDesc;

        public DeveloperViewHolder(View view) {
            super(view);

            mRank = (TextView) view.findViewById(R.id.dev_rank);
            mAvatar = (ImageView) view.findViewById(R.id.dev_avatar);
            mName = (TextView) view.findViewById(R.id.dev_name);
            mFullName = (TextView) view.findViewById(R.id.dev_fullname);
            mRepoName = (TextView) view.findViewById(R.id.dev_repo_name);
            mRepoDesc = (TextView) view.findViewById(R.id.dev_repo_desc);
        }

        public void bindItem(Context context, User user, int position,
                             OnDevItemClickListener listener) {
            mRank.setText(String.valueOf(user.getRank()));
            Glide.with(context)
                    .load(user.getAvatar())
                    .fitCenter()
                    .into(mAvatar);
            mName.setText(user.getLoginName());
            mFullName.setText(user.getFullName());
            mRepoName.setText(user.getRepoName());
            mRepoDesc.setText(user.getRepoDesc());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            });
        }
    }

    public static final int TYPE_CONTENT = 0;
    public static final int TYPE_FOOTER = 1;

    private Context mContext;
    private List<User> mUserList;
    private OnDevItemClickListener mListener;

    public DeveloperAdapter(Context context, List<User> list, OnDevItemClickListener listener) {
        mContext = context;
        mUserList = list;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer,
                    parent, false);
            return new RepoAdapter.FootViewHolder(view);
        }
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_developer,
                parent, false);
        DeveloperViewHolder viewHolder = new DeveloperViewHolder(view);
        view.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_CONTENT) {
            User user = mUserList.get(position);
            ((DeveloperViewHolder) holder).bindItem(mContext, user, position, mListener);
        }
    }

    @Override
    public int getItemCount() {
        return mUserList == null ? 0 : mUserList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == getItemCount() - 1) ? TYPE_FOOTER : TYPE_CONTENT;
    }
}