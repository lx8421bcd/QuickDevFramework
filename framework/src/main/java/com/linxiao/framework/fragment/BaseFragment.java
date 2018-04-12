package com.linxiao.framework.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.SpannedString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxiao.framework.log.Logger;
import com.linxiao.framework.util.ScreenUtil;
import com.linxiao.framework.util.SpanFormatter;
import com.trello.rxlifecycle2.components.support.RxFragment;

import io.reactivex.disposables.CompositeDisposable;

/**
 * base Fragment of entire project
 * <p>template for Fragments in the project, used to define common methods </p>
 * */
public abstract class BaseFragment extends RxFragment {
    protected String TAG;
    
    private View rootView;
    
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
//        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null) {
            onCreateContentView(inflater, container, savedInstanceState);
        }
        
        return rootView;
    }
    
    /**
     * used to add data binding in mvvm.
     * <p>subscribe to the data source provided in ViewModel here </p>
     * */
    protected void onCreateDataBinding() {
        //add data binding
    }
    
    @Override
    public void onStart() {
        super.onStart();
        onCreateDataBinding();
    }
    
    @Override
    public void onStop() {
        super.onStop();
        mCompositeDisposable.clear();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    
    /**
     * set the content view of this fragment by layout resource id
     * <p>if the content view has been set before, this method will not work again</p>
     * @param resId
     * layout resource id of content view
     * @param container
     * parent ViewGroup of content view, get it in
     * {@link #onCreateContentView(LayoutInflater, ViewGroup, Bundle)}
     * */
    protected void setContentView(@LayoutRes int resId, ViewGroup container) {
        if (rootView != null) {
            Logger.w(TAG, "contentView has set already");
            return;
        }
        rootView = LayoutInflater.from(getActivity()).inflate(resId, container, false);
    }
    
    /**
     * set the content view of this fragment by layout resource id
     * <p>if the content view has been set before, this method will not work again</p>
     *
     * @param contentView content view of this fragment
     * */
    protected void setContentView(View contentView) {
        if (rootView != null) {
            Logger.w(TAG, "contentView has set already");
            return;
        }
        rootView = contentView;
    }

    protected View getContentView() {
        return rootView;
    }
    
    /**
     * execute on method onCreateView(), put your code here which you want to do in onCreateView()<br>
     * <strong>execute {@link #setContentView(int, ViewGroup)} or {@link #setContentView(View)} to
     * set the root view of this fragment like activity</strong>
     * */
    protected abstract void onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    
    /**
     * get spanned string from xml resources
     * <p>use this method to get the text which include style labels in strings.xml,
     * support using format args</p>
     * @param resId string resource id
     * @param args format args
     * @return SpannedString
     */
    protected SpannedString getSpannedString(@StringRes int resId, Object... args) {
        return SpanFormatter.format(getText(resId), args);
    }
    
    protected int dp2px(float dpValue) {
        return ScreenUtil.dp2px(dpValue);
    }
    
    public static int px2dp(float pxValue) {
        return ScreenUtil.px2dp(pxValue);
    }
    
    /**
     * use this method instead of findViewById() to simplify view initialization <br>
     * it's not unchecked because of T extends View
     * */
    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(View layoutView, @IdRes int resId) {
        return (T) layoutView.findViewById(resId);
    }

}
