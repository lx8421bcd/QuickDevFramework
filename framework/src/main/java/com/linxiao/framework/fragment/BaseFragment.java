package com.linxiao.framework.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * base Fragment of entire project
 * <p>template for Fragments in the project, used to define common methods </p>
 * */
public abstract class BaseFragment extends Fragment {

    public static String TAG;

    private View contentView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
//        this.setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contentView == null) {
            contentView = inflater.inflate(getInflateLayoutRes(), container, false);
            onInitContentView(contentView, inflater, container, savedInstanceState);
        }
        return contentView;
    }

    /**
     * return fragment content view id
     * */
    @LayoutRes
    protected abstract int getInflateLayoutRes();

    /**
     * execute on method onCreateView(), put your code here which you want to do in onCreateView()<br>
     * <strong>do not override onCreateView() or this method and getInflateLayoutRes() will be invalidated</strong>
     * */
    protected abstract void onInitContentView(View contentView, LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    /**
     * use this method instead of findViewById() to simplify view initialization <br>
     * it's not unchecked because T extends View
     * */
    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(View layoutView, @IdRes int resId) {
        return (T) layoutView.findViewById(resId);
    }

    public void showToast(CharSequence message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void showToast(CharSequence message, int time) {
        Toast.makeText(getActivity(), message, time).show();
    }

}
