package com.linxiao.framework.architecture;

import android.os.Bundle;
import androidx.annotation.CheckResult;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import android.text.SpannedString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxiao.framework.common.ScreenUtil;
import com.linxiao.framework.common.SpanFormatter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * base Fragment class of entire project
 * <p>
 * template for Fragments in the project, contains common methods.
 * extends from Android base appcompat component class, manually implemented the implementation
 * of RxLifeCycle, if you have to extends framework base class from some third sdk, just
 * change parent class is ok.
 * </p>
 *
 * @author linxiao
 * @since 2016-12-05
 */
public abstract class BaseFragment extends Fragment implements LifecycleProvider<FragmentEvent> {
    protected String TAG;

    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    private View rootView;
    
    public BaseFragment() {
        TAG = this.getClass().getSimpleName();
    }

    @Override
    @NonNull
    @CheckResult
    public final Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(lifecycleSubject);
    }

    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null) {
            onCreateContentView(inflater, container, savedInstanceState);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(FragmentEvent.START);
    }

    @Override
    public void onResume() {
        super.onResume();
        lifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override
    public void onPause() {
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();
    }

    @Override
    public void onStop() {
        lifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        lifecycleSubject.onNext(FragmentEvent.DETACH);
        super.onDetach();
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
            Log.w(TAG, "contentView has set already");
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
            Log.w(TAG, "contentView has set already");
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
}
