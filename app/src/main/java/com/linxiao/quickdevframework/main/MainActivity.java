package com.linxiao.quickdevframework.main;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.linxiao.framework.architecture.SimpleViewBindingActivity;
import com.linxiao.framework.common.ToastAlert;
import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.databinding.ActivityMainBinding;
import com.linxiao.quickdevframework.databinding.ContentMainBinding;
import com.linxiao.quickdevframework.sample.adapter.AdapterTestFragment;
import com.linxiao.quickdevframework.sample.frameworkapi.ApplicationApiFragment;
import com.linxiao.quickdevframework.sample.frameworkapi.BiometricApiFragment;
import com.linxiao.quickdevframework.sample.frameworkapi.DialogApiFragment;
import com.linxiao.quickdevframework.sample.frameworkapi.FileApiFragment;
import com.linxiao.quickdevframework.sample.frameworkapi.NotificationApiFragment;
import com.linxiao.quickdevframework.sample.frameworkapi.PermissionApiFragment;
import com.linxiao.quickdevframework.sample.frameworkapi.ToastApiFragment;
import com.linxiao.quickdevframework.sample.netapi.DownloadTestFragment;
import com.linxiao.quickdevframework.sample.netapi.NetTestFragment;
import com.linxiao.quickdevframework.sample.widget.WidgetsGuideFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends SimpleViewBindingActivity<ActivityMainBinding> {

    private static final String KEY_CURRENT_TAG = "CurrentTag";
    private static final String KEY_TAGS = "FragmentTags";
    private static final String KEY_CLASS_NAMES = "FragmentClassNames";

    private ContentMainBinding contentBinding;
    private ArrayList<String> fragmentTags = new ArrayList<>();
    private ArrayList<String> fragmentClassNames = new ArrayList<>();
    private ArrayList<Fragment> fragments = new ArrayList<>();

    private FragmentManager mFragmentManager;
    private String currentTag;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setImmersiveMode(true);
//        StatusBarUtil.setStatusBarColor(this, Color.TRANSPARENT);
//        StatusBarUtil.setStatusBarLightMode(this, true);

        contentBinding = getViewBinding().contentMain;
        setSupportActionBar(getViewBinding().toolbar);
        mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            restoreFragments(savedInstanceState);
        }
        else {
            initFragments();
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, contentBinding.drawerMain, getViewBinding().toolbar, R.string.app_name, R.string.app_name);
        contentBinding.drawerMain.setDrawerListener(toggle);
        toggle.syncState();

        initApiSampleList();

    }

    private void initApiSampleList() {
        List<ApiSampleObject> apiSampleList = new ArrayList<>();
        apiSampleList.add(new ApiSampleObject("Application API", "ApplicationApiFragment"));
        apiSampleList.add(new ApiSampleObject("Dialog API", "DialogApiFragment"));
        apiSampleList.add(new ApiSampleObject("Biometric API", "BiometricApiFragment"));
        apiSampleList.add(new ApiSampleObject("Notification API", "NotificationApiFragment"));
        apiSampleList.add(new ApiSampleObject("Toast API", "ToastApiFragment"));
        apiSampleList.add(new ApiSampleObject("Permission API", "PermissionApiFragment"));
        apiSampleList.add(new ApiSampleObject("File API", "FileApiFragment"));
        apiSampleList.add(new ApiSampleObject("Network API", "NetTestFragment"));
        apiSampleList.add(new ApiSampleObject("Download Test", "DownloadTestFragment"));
        apiSampleList.add(new ApiSampleObject("Adapter API", "AdapterTestFragment"));
        apiSampleList.add(new ApiSampleObject("Widgets", "WidgetsGuideFragment"));


        ApiSampleListAdapter adapter = new ApiSampleListAdapter();
        adapter.setItems(apiSampleList);
        adapter.setOnItemClickListener((baseQuickAdapter, view, position) -> {
            ApiSampleObject object = (ApiSampleObject) adapter.getItem(position);
            switchFragment(object.getTarget());
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(object.getApiName());
            }
            contentBinding.drawerMain.closeDrawer(GravityCompat.START);
        });
        contentBinding.rcvApiSampleList.setLayoutManager(new LinearLayoutManager(this));
        contentBinding.rcvApiSampleList.setItemAnimator(new DefaultItemAnimator());
        contentBinding.rcvApiSampleList.setAdapter(adapter);
    }

    private void initFragments() {
        addFragment(new ApplicationApiFragment(), "ApplicationApiFragment");
        addFragment(new DialogApiFragment(), "DialogApiFragment");
        addFragment(new BiometricApiFragment(), "BiometricApiFragment");
        addFragment(new NotificationApiFragment(), "NotificationApiFragment");
        addFragment(new ToastApiFragment(), "ToastApiFragment");
        addFragment(new PermissionApiFragment(), "PermissionApiFragment");
        addFragment(new FileApiFragment(), "FileApiFragment");
        addFragment(new NetTestFragment(), "NetTestFragment");
        addFragment(new DownloadTestFragment(), "DownloadTestFragment");
        addFragment(new AdapterTestFragment(), "AdapterTestFragment");
        addFragment(new WidgetsGuideFragment(), "WidgetsGuideFragment");

        currentTag = "DialogApiFragment";
        switchFragment(currentTag);
    }

    private void restoreFragments(@NonNull Bundle savedInstanceState) {
        currentTag = savedInstanceState.getString(KEY_CURRENT_TAG, "");
        fragmentTags.clear();
        fragmentTags.addAll(savedInstanceState.getStringArrayList(KEY_TAGS));
        fragmentClassNames.clear();
        fragmentClassNames.addAll(savedInstanceState.getStringArrayList(KEY_CLASS_NAMES));
        Log.d(TAG, fragmentTags.toString());
        Log.d(TAG, fragmentClassNames.toString());
        Log.d(TAG, "CurrentTag = " + currentTag);
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
                ToastAlert.showToast(this, getString(R.string.press_again_exit), 2000);
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
