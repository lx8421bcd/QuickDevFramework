package com.linxiao.quickdevframework.sample.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linxiao.framework.architecture.BaseFragment;
import com.linxiao.quickdevframework.databinding.FragmentAdapterTestBinding;

public class AdapterTestFragment extends BaseFragment {

    private FragmentAdapterTestBinding viewBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentAdapterTestBinding.inflate(inflater);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewBinding.btnTestEmptyView.setOnClickListener(v -> startActivity(new Intent(getActivity(), EmptyTestActivity.class)));
        viewBinding.btnHeaderFooter.setOnClickListener(v -> startActivity(new Intent(getActivity(), HeaderFooterActivity.class)));
    }

}
