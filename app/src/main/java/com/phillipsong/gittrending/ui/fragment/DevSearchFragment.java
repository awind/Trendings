package com.phillipsong.gittrending.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phillipsong.gittrending.R;
import com.phillipsong.gittrending.inject.components.AppComponent;

/**
 * Created by fei on 16/5/12.
 */
public class DevSearchFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dev_search, container, false);
        return view;
    }

    @Override
    protected void setupFragmentComponent(AppComponent appComponent) {

    }
}
