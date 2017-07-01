package com.linxiao.quickdevframework.sample.widget;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.framework.widget.CusViewPager;
import com.linxiao.quickdevframework.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChildPagerFragment extends BaseFragment {

    private List<PageSampleFragment> fragments = new ArrayList<>();

    @BindView(R.id.vpChild)
    CusViewPager vpChild;

    private int pagerIndex;

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setContentView(R.layout.fragment_child_pager, container);
        ButterKnife.bind(this, getContentView());

        vpChild.TAG = "ViewPager Child";

        fragments.add(new PageSampleFragment());
        fragments.add(new PageSampleFragment());
        fragments.add(new PageSampleFragment());
        fragments.add(new PageSampleFragment());

        for (PageSampleFragment fragment : fragments) {
            fragment.setPageDesc("this is page from child page " + pagerIndex);
        }

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        vpChild.setAdapter(adapter);
    }

    public void setPagerIndex(int index) {
        pagerIndex = index;
    }
}
