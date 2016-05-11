package com.phillipsong.gittrending.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.phillipsong.gittrending.R;

/**
 * Created by fei on 16/5/10.
 */
public class StringPickerDialog extends DialogFragment {

    private OnClickListener mListener;

    private Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.string_picker_dialog, null, false);

        final StringPicker stringPicker = (StringPicker) view.findViewById(R.id.string_picker);
        final Bundle params = getArguments();
        if (params == null) {
            throw new RuntimeException("params is null!");
        }

        final String[] values = params.getStringArray(getValue(R.string.string_picker_dialog_values));
        stringPicker.setValues(values);
        final int index = params.getInt(getValue(R.string.string_picker_dialog_current_index));
        if (index < values.length) {
            stringPicker.setCurrent(index);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(getValue(R.string.string_picker_dialog_title));
        builder.setPositiveButton(getValue(R.string.string_picker_dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onClick(stringPicker.getCurrentValue());
            }
        });
        builder.setNegativeButton(getValue(R.string.string_picker_dialog_cancel), null);
        builder.setView(view);
        return builder.create();
    }

    public void setListener(OnClickListener listener) {
        if (listener == null) {
            throw new RuntimeException("OnClickListener is null!");
        }
        this.mListener = listener;
    }
    private String getValue(final int resId) {
        return mActivity.getString(resId);
    }

    public interface OnClickListener {
        void onClick(final String value);
    }

}