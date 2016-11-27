package com.linxiao.framework.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.linxiao.framework.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by LinXiao on 2016-10-05.
 */

public abstract class BaseFragmentSwitcherActivity extends BaseActivity {

    private static final String SAVED_CURRENT_ID = "currentId";

    public static final List<String> PAGE_TAGS = new ArrayList<>();
    private List<Class<? extends BaseFragment>> fragmentClasses = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();

    private FragmentManager mFragmentManager;

    private int currentId = 0;

    /**
     * return the activity layout res id
     * */
    @LayoutRes
    protected abstract int getContentViewRes();

    /**
     * return the fragment container layout id
     * */
    @IdRes
    protected abstract int getFragmentContainerLayoutRes();
    
    /**
     * execute on method onCreate(), put your code here which you want to do in onCreate()<br>
     * <strong>do not override onCreate() or this method and getContentViewRes() will be invalidated</strong>
     * */
    protected abstract void doOnOnCreate(Bundle savedInstanceState, PersistableBundle persistentState);

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(getContentViewRes());
        
    }

    private void initFragments(Bundle savedInstanceState) {
        mFragmentManager = getSupportFragmentManager();

        for(int i = 0; i < fragments.size(); i++) {
            fragments.set(i, mFragmentManager.findFragmentByTag(PAGE_TAGS.get(i)));
            if(fragments.get(i) == null) {
                try {
                    fragments.set(i,fragmentClasses.get(i).newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //先重置所有fragment的状态为隐藏，彻底解决重叠问题
            if(fragments.get(i).isAdded()) {
                mFragmentManager.beginTransaction()
                        .hide(fragments.get(i))
                        .commitAllowingStateLoss();
            }
        }
        if(savedInstanceState != null) {
            int cachedId = savedInstanceState.getInt(SAVED_CURRENT_ID, 0);
            if(cachedId >= 0 && cachedId <= 4) {
                currentId = cachedId;
            }
        }
        switchFragment(currentId, false);
    }

    public void addFragment(Fragment fragment, String tag) {

    }

    private void switchFragment(int index) {
        switchFragment(index, true);
    }

    private void switchFragment(int index, boolean anim) {
        /* Fragment 切换 */
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
//        if(anim) {
//            if(index > currentId) {
//                transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
//            }
//            else {
//                transaction.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out);
//            }
//        }
        if(fragments.get(index).isAdded()) {
            transaction.hide(fragments.get(currentId));
            transaction.show(fragments.get(index));
        }
        else {
            transaction.hide(fragments.get(currentId));
            transaction.add(getFragmentContainerLayoutRes(), fragments.get(index), PAGE_TAGS.get(index));
            transaction.show(fragments.get(index));
        }
        transaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commitAllowingStateLoss();
        currentId = index;
    }
    
}
