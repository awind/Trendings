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
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.phillipsong.gittrending.R;
import com.phillipsong.gittrending.data.models.Repo;
import com.phillipsong.gittrending.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.realm.Realm;

public class RepoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }

    public static class RepoViewHolder extends RecyclerView.ViewHolder {
        private TextView mOwner;
        private TextView mTitle;
        private ImageView mLanguage;
        private TextView mDescription;
        private ImageView mAvatar;
        private TextView mStar;
        private TextView mShare;
        private TextView mFavorite;

        public RepoViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.title);
            mLanguage = (ImageView) view.findViewById(R.id.language);
            mDescription = (TextView) view.findViewById(R.id.description);
            mAvatar = (ImageView) view.findViewById(R.id.avatar);
            mStar = (TextView) view.findViewById(R.id.star);
            mShare = (TextView) view.findViewById(R.id.share);
            mFavorite = (TextView) view.findViewById(R.id.favorite);
        }

        public void bind(Repo repo, Context context, Realm realm) {
            SpannableString name = new SpannableString(repo.getName());
            name.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, name.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTitle.setText(String.format(context.getString(R.string.repo_title),
                    repo.getOwner()));
            mTitle.append(name);
            mDescription.setText(repo.getDescription());
            Picasso.with(context)
                    .load(repo.getContributors().get(0).getAvatar())
                    .centerCrop()
                    .fit()
                    .into(mAvatar);
            mStar.setText(repo.getStar());

            mFavorite.setOnClickListener(v -> {
                realm.beginTransaction();
                realm.copyToRealm(repo);
                realm.commitTransaction();
                Toast.makeText(context, "Ok", Toast.LENGTH_LONG).show();
            });

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Constants.GITHUB_BASE_URL + repo.getUrl()));
                context.startActivity(intent);
            });
        }
    }

    private static final int TYPE_FOOTER = 0;
    private static final int TYPE_CONTENT = 1;

    private Context mContext;
    private List<Repo> mRepos;
    private Realm mRealm;

    public RepoAdapter(List<Repo> list, Context context, Realm realm) {
        this.mContext = context;
        this.mRepos = list;
        this.mRealm = realm;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trending_item_footer,
                    parent, false);
            return new FootViewHolder(view);
        }
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trending_item_repo,
                parent, false);
        RepoViewHolder viewHolder = new RepoViewHolder(view);
        view.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_CONTENT) {
            Repo repo = mRepos.get(position);
            ((RepoViewHolder) holder).bind(repo, mContext, mRealm);
        }
    }

    @Override
    public int getItemCount() {
        return mRepos == null ? 0 : mRepos.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == getItemCount() - 1) ? TYPE_FOOTER : TYPE_CONTENT;
    }
}