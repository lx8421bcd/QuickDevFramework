package com.linxiao.quickdevframework.sample.widget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.linxiao.framework.activity.BaseActivity;
import com.linxiao.framework.widget.CusViewPager;
import com.linxiao.quickdevframework.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewPagerActivity extends BaseActivity {

    private List<Fragment> childPagerFragments = new ArrayList<>();

    @BindView(R.id.vpRoot)
    CusViewPager vpRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        ButterKnife.bind(this);

        vpRoot.TAG = "ViewPager Root";

        PageSampleFragment sampleFragment = new PageSampleFragment();
        sampleFragment.setPageDesc("page 0");
        childPagerFragments.add(sampleFragment);

        ChildPagerFragment pagerFragment = new ChildPagerFragment();
        pagerFragment.setPagerIndex(1);
        childPagerFragments.add(pagerFragment);

        PageSampleFragment sampleFragment2 = new PageSampleFragment();
        sampleFragment2.setPageDesc("page 2");
        childPagerFragments.add(sampleFragment2);

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return childPagerFragments.get(position);
            }

            @Override
            public int getCount() {
                return childPagerFragments.size();
            }
        };
        vpRoot.setAdapter(adapter);
    }
}
