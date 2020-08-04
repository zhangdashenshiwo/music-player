package com.example.zhang.myapplication.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zhang.myapplication.R;

public class MessageFragment extends Fragment {

    private View layout;
    private Activity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = View.inflate(getActivity().getApplicationContext(), R.layout.fragment_message, null);
        activity = getActivity();
        return layout;
    }


}
