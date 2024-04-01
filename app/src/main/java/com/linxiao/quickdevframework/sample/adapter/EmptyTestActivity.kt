package com.linxiao.quickdevframework.sample.adapter;

import android.os.Bundle;
import android.os.Handler;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.linxiao.framework.common.DensityHelper;
import com.linxiao.framework.list.EquidistantDecoration;
import com.linxiao.quickdevframework.databinding.ActivityEmptyTestBinding;
import com.linxiao.framework.architecture.SimpleViewBindingActivity;
import com.linxiao.quickdevframework.databinding.LayoutEmptyViewBinding;

import java.util.ArrayList;
import java.util.Arrays;

public class EmptyTestActivity extends SimpleViewBindingActivity<ActivityEmptyTestBinding> {

    private SampleAdapter mAdapter;

    private boolean showEmpty = true;
    private boolean showData = false;
    private boolean showError = false;

    private LayoutEmptyViewBinding emptyViewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SampleAdapter();
        getViewBinding().rcvEmptySimple.setAdapter(mAdapter);
        getViewBinding().rcvEmptySimple.setItemAnimator(new DefaultItemAnimator());
        //TODO bug QuickGridLayoutManager not work
//        getViewBinding().rcvEmptySimple.setLayoutManager(new QuickGridLayoutManager(this, 3));
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        // fix method
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0 && mAdapter.getItems().size() == 0) {
                    return 3;
                }
                return 1;
            }
        });
        getViewBinding().rcvEmptySimple.setLayoutManager(layoutManager);
        getViewBinding().rcvEmptySimple.addItemDecoration(new EquidistantDecoration(3, DensityHelper.dp2px(12)));
        // 这种初始化empty view必须先设置LayoutManager
        emptyViewBinding = LayoutEmptyViewBinding.inflate(getLayoutInflater(), getViewBinding().rcvEmptySimple, false);
        mAdapter.setEmptyViewEnable(true);
        mAdapter.setEmptyView(emptyViewBinding.getRoot());


        getViewBinding().btnRefresh.setOnClickListener(v -> {
            mAdapter.submitList(new ArrayList<>());
            refreshData();
        });
        mAdapter.submitList(Arrays.asList("1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1"));

    }

    private void refreshData() {
        emptyViewBinding.tvText.setText("loading");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (showEmpty) {
                    showEmpty = false;
                    showError = true;
                    emptyViewBinding.tvText.setText("empty");
                }
                else if (showError) {
                    showError = false;
                    showData = true;
                    emptyViewBinding.tvText.setText("error");
                }
                else if (showData) {
                    showData = false;
                    showEmpty = true;
                    mAdapter.submitList(Arrays.asList("1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1"));
                }
            }
        }, 1000);
    }
}
