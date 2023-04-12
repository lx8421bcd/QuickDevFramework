package com.linxiao.quickdevframework.sample.frameworkapi;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linxiao.framework.architecture.BaseBottomDialogFragment;
import com.linxiao.quickdevframework.R;

/**
 *
 * Created by linxiao on 2016-12-12.
 */
public class SampleBottomDialogFragment extends BaseBottomDialogFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_bottom_sample, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().findViewById(R.id.root_view).setOnClickListener(v -> dismiss());
    }
}
