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
package com.phillipsong.gittrending.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.phillipsong.gittrending.data.models.Contributor;
import com.phillipsong.gittrending.utils.UnitUtil;

import java.util.List;

public class AvatarContainer extends LinearLayout {

    private Context mContext;
    private List<Contributor> mContributors;

    public AvatarContainer(Context context) {
        super(context);
        mContext = context;
    }

    public AvatarContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setImageUrls(List<Contributor> contributorses) {
        this.mContributors = contributorses;
        addImageView();
    }

    private void addImageView() {
        removeAllViews();
        for (Contributor contributor : mContributors) {
            ImageView imageView = new ImageView(mContext);
            int square = UnitUtil.dpToPx(mContext, 24);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(square, square);
            param.setMargins(8, 0, 0, 0);
            imageView.setLayoutParams(param);
            Glide.with(mContext)
                    .load(contributor.getAvatar())
                    .centerCrop()
                    .centerCrop()
                    .into(imageView);
            addView(imageView);
        }
        invalidate();
    }

}