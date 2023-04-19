package com.lithium.community.android.ui.components.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.lithium.community.android.ui.R;

public class LiProgressFragment extends DialogFragment {

    public static final String EXTRA_TITLE = LiProgressFragment.class.getName() + ".TITLE";
    public static final String EXTRA_MESSAGE = LiProgressFragment.class.getName() + ".MESSAGE";

    private String message;
    private String title;

    private TextView mProgressMessage;

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        this.title = args.getString(EXTRA_TITLE);
        this.message = args.getString(EXTRA_MESSAGE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.li_progress_layout, null);
        mProgressMessage = v.findViewById(R.id.li_progress_message);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        getDialog().setCancelable(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mProgressMessage != null) {
            mProgressMessage.setText(message);
        }
        getDialog().setTitle(title);
    }
}
