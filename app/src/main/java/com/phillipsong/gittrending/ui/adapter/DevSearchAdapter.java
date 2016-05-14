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
import com.phillipsong.gittrending.data.models.GithubUser;
import com.phillipsong.gittrending.ui.misc.OnItemClickListener;

import java.util.List;

public class DevSearchAdapter extends RecyclerView.Adapter<DevSearchAdapter.DevSearchViewHolder> {

    private Context mContext;
    private List<GithubUser> mUserList;
    private OnItemClickListener mListener;

    public DevSearchAdapter(Context context, List<GithubUser> list) {
        mContext = context;
        mUserList = list;
    }

    @Override
    public DevSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_dev, parent, false);
        DevSearchViewHolder viewHolder = new DevSearchViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DevSearchViewHolder holder, int position) {
        GithubUser user = mUserList.get(position);
        holder.bindItem(mContext, user);

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
        return mUserList.size();
    }

    public static class DevSearchViewHolder extends RecyclerView.ViewHolder {
        private ImageView mAvatar;
        private TextView mFullName;

        public DevSearchViewHolder(View view) {
            super(view);
            mAvatar = (ImageView) view.findViewById(R.id.dev_avatar);
            mFullName = (TextView) view.findViewById(R.id.dev_fullname);
        }

        public void bindItem(Context context, GithubUser user) {
            Glide.with(context)
                    .load(user.getAvatar())
                    .placeholder(R.mipmap.ic_lang)
                    .error(R.mipmap.ic_lang)
                    .fitCenter()
                    .into(mAvatar);
            mFullName.setText(user.getLogin());
        }
    }
}
