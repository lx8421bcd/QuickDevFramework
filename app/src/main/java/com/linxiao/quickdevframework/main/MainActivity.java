package com.linxiao.quickdevframework.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import com.linxiao.framework.activity.BaseActivity;
import com.linxiao.framework.adapter.BaseRecyclerViewAdapter;
import com.linxiao.framework.toast.ToastWrapper;
import com.linxiao.framework.log.Logger;
import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.sample.adapter.AdapterTestFragment;
import com.linxiao.quickdevframework.sample.frameworkapi.ApplicationApiFragment;
import com.linxiao.quickdevframework.sample.frameworkapi.DialogApiFragment;
import com.linxiao.quickdevframework.sample.frameworkapi.FileApiFragment;
import com.linxiao.quickdevframework.sample.frameworkapi.FileBrowserFragment;
import com.linxiao.quickdevframework.sample.frameworkapi.NotificationApiFragment;
import com.linxiao.quickdevframework.sample.frameworkapi.PermissionApiFragment;
import com.linxiao.quickdevframework.sample.frameworkapi.ToastApiFragment;
import com.linxiao.quickdevframework.sample.netapi.NetTestFragment;
import com.linxiao.quickdevframework.sample.widget.WidgetsGuideFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    private static final String KEY_CURRENT_TAG = "CurrentTag";
    private static final String KEY_TAGS = "FragmentTags";
    private static final String KEY_CLASS_NAMES = "FragmentClassNames";

    @BindView(R.id.drawer_main)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.rcvApiSampleList)
    RecyclerView rcvApiSampleList;

    private ArrayList<String> fragmentTags = new ArrayList<>();
    private ArrayList<String> fragmentClassNames = new ArrayList<>();
    private ArrayList<Fragment> fragments = new ArrayList<>();

    private FragmentManager mFragmentManager;

    private String currentTag;

    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            restoreFragments(savedInstanceState);
        }
        else {
            initFragments();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        initApiSampleList();
    }

    private void initApiSampleList() {
        List<ApiSampleObject> apiSampleList = new ArrayList<>();
        apiSampleList.add(new ApiSampleObject("Application API", "ApplicationApiFragment"));
        apiSampleList.add(new ApiSampleObject("Dialog API", "DialogApiFragment"));
        apiSampleList.add(new ApiSampleObject("Notification API", "NotificationApiFragment"));
        apiSampleList.add(new ApiSampleObject("Toast API", "ToastApiFragment"));
        apiSampleList.add(new ApiSampleObject("Permission API", "PermissionApiFragment"));
        apiSampleList.add(new ApiSampleObject("File API", "FileApiFragment"));
        apiSampleList.add(new ApiSampleObject("File Browser", "FileBrowserFragment"));
        apiSampleList.add(new ApiSampleObject("Network API", "NetTestFragment"));
        apiSampleList.add(new ApiSampleObject("Adapter API", "AdapterTestFragment"));
        apiSampleList.add(new ApiSampleObject("Widgets", "WidgetsGuideFragment"));

        ApiSampleListAdapter adapter = new ApiSampleListAdapter(this);
        adapter.setDataSource(apiSampleList);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter adapter, View itemView, int position) {
                ApiSampleObject object = (ApiSampleObject) adapter.getFromDataSource(position);
                switchFragment(object.getTarget());
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(object.getApiName());
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        rcvApiSampleList.setLayoutManager(new LinearLayoutManager(this));
        rcvApiSampleList.setItemAnimator(new DefaultItemAnimator());
        rcvApiSampleList.setAdapter(adapter);
    }

    private void initFragments() {
        addFragment(new ApplicationApiFragment(), "ApplicationApiFragment");
        addFragment(new DialogApiFragment(), "DialogApiFragment");
        addFragment(new NotificationApiFragment(), "NotificationApiFragment");
        addFragment(new ToastApiFragment(), "ToastApiFragment");
        addFragment(new PermissionApiFragment(), "PermissionApiFragment");
        addFragment(new FileApiFragment(), "FileApiFragment");
        addFragment(new FileBrowserFragment(), "FileBrowserFragment");
        addFragment(new NetTestFragment(), "NetTestFragment");
        addFragment(new AdapterTestFragment(), "AdapterTestFragment");
        addFragment(new WidgetsGuideFragment(), "WidgetsGuideFragment");

        currentTag = "DialogApiFragment";
        switchFragment("ApplicationApiFragment");
    }

    private void restoreFragments(@NonNull Bundle savedInstanceState) {
        currentTag = savedInstanceState.getString(KEY_CURRENT_TAG, "");
        fragmentTags.clear();
        fragmentTags.addAll(savedInstanceState.getStringArrayList(KEY_TAGS));
        fragmentClassNames.clear();
        fragmentClassNames.addAll(savedInstanceState.getStringArrayList(KEY_CLASS_NAMES));
        Logger.d(TAG, fragmentTags.toString());
        Logger.d(TAG, fragmentClassNames.toString());
        Logger.d(TAG, "CurrentTag = " + currentTag);
        for(int i = 0; i < fragmentTags.size(); i++) {
            Fragment fragment = mFragmentManager.findFragmentByTag(fragmentTags.get(i));
            if (fragment == null) {
                try {
                    Class<?> fragmentClass = Class.forName(fragmentClassNames.get(i));
                    Object obj = fragmentClass.newInstance();
                    if (obj instanceof Fragment) {
                        fragment = (Fragment) obj;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fragment != null) {
                fragments.add(fragment);
                if(fragment.isAdded()) {
                    mFragmentManager.beginTransaction()
                    .hide(fragment)
                    .commitAllowingStateLoss();
                }
            }
        }
        switchFragment(currentTag);
    }

    private void switchFragment(String tag) {
        /* Fragment 切换 */
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (fragmentTags.indexOf(tag) < 0) {
            return;
        }
        Fragment showFragment = fragments.get(fragmentTags.indexOf(tag));
        Fragment currentFragment = mFragmentManager.findFragmentByTag(currentTag);
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }
        if (showFragment.isAdded()) {
            transaction.show(showFragment);
        }
        else {
            transaction.add(R.id.content_frame, showFragment, tag);
            transaction.show(showFragment);
        }
        transaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commitAllowingStateLoss();
        currentTag = tag;
    }

    protected void addFragment(@NonNull Fragment fragment, @NonNull String tag) {
        fragments.add(fragment);
        fragmentTags.add(tag);
        fragmentClassNames.add(fragment.getClass().getName());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CURRENT_TAG, currentTag);
        outState.putStringArrayList(KEY_TAGS, fragmentTags);
        outState.putStringArrayList(KEY_CLASS_NAMES, fragmentClassNames);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //2秒内按两次退出
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastWrapper.showToast(this, getString(R.string.press_again_exit), 2000);
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
