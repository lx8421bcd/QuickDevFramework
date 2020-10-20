package com.linxiao.framework.architecture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.SpannedString;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.linxiao.framework.common.ScreenUtil;
import com.linxiao.framework.common.SpanFormatter;
import com.linxiao.framework.permission.PermissionManager;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import java.lang.reflect.InvocationTargetException;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;


/**
 * base activity class of entire project
 * <p>
 * template for activities in the project, used to define common methods of activity,
 * extends from Android base appcompat component class, manually implemented the implementation
 * of RxLifeCycle, if you have to extends framework base class from some third sdk, just
 * change parent class is ok.
 * </p>
 *
 * @author linxiao
 * @since 2016-12-05
 */
public abstract class BaseActivity extends AppCompatActivity implements LifecycleProvider<ActivityEvent> {

    protected String TAG;
    public static final String ACTION_EXIT_APPLICATION = "exit_application";

    private boolean printLifeCycle = false;
    private ActivityBaseReceiver mReceiver;
    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();
    private final BehaviorSubject<Object> finishSubject = BehaviorSubject.create();
    private static final Object finishSignal = new Object();

    @Override
    @NonNull
    @CheckResult
    public final Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    /**
     * notify subscriber once {@link #finish()} has been called
     */
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilFinish() {
        return RxLifecycle.bindUntilEvent(finishSubject, finishSignal);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);
        if (printLifeCycle) {
            Log.d(TAG, "onCreate");
        }
        TAG = this.getClass().getSimpleName();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_EXIT_APPLICATION);
        mReceiver = new ActivityBaseReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
        if (printLifeCycle) {
            Log.d(TAG, "onStart");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(ActivityEvent.RESUME);
        if (printLifeCycle) {
            Log.d(TAG, "onResume");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
        if (printLifeCycle) {
            Log.d(TAG, "onPause");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        lifecycleSubject.onNext(ActivityEvent.STOP);
        if (printLifeCycle) {
            Log.d(TAG, "onStop");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
       if (printLifeCycle) {
           Log.d(TAG, "onRestart");
       }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        if (printLifeCycle) {
            Log.d(TAG, "onDestroy");
        }
        unregisterReceiver(mReceiver);
    }

    @Override
    public void finish() {
        super.finish();
        finishSubject.onNext(finishSignal);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionManager.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handleCallback(this, requestCode, permissions, grantResults);
    }
    
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
     * set is print activity lifecycle
     * <p>if true, activity will log out life cycle</p>
     * */
    public void printLifecycle(boolean printLifeCycle) {
        this.printLifeCycle = printLifeCycle;
    }

    /**
     * 基础类Activity的BroadcastReceiver
     */
    protected class ActivityBaseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_EXIT_APPLICATION)) {
                finish();
            }
        }
    }
}
