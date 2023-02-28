package com.linxiao.framework.architecture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.linxiao.framework.language.AppLanguageHelper;
import com.linxiao.framework.common.DensityHelper;
import com.linxiao.framework.common.KeyboardUtil;
import com.linxiao.framework.language.LanguageChangedEvent;
import com.linxiao.framework.permission.PermissionManager;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    private boolean hideKeyboardOnTouchOutside = false;

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
        EventBus.getDefault().register(this);
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
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void finish() {
        super.finish();
        finishSubject.onNext(finishSignal);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handleCallback(this, requestCode, permissions, grantResults);
    }

    public void startActivityForCallback(Intent intent, ActivityResultCallback<ActivityResult> callback) {
        ActivityResultHolderFragment.startActivityForCallback(this, intent, callback, null);
    }

    public void setHideKeyboardOnTouchOutside(boolean hideKeyboardOnTouchOutside) {
        this.hideKeyboardOnTouchOutside = hideKeyboardOnTouchOutside;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (hideKeyboardOnTouchOutside) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (shouldHideInput(v, ev)) {
                    KeyboardUtil.hideKeyboard(getWindow().getDecorView());
                    v.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean shouldHideInput(View v, MotionEvent ev) {
        if (v instanceof EditText) {
            int[] l = new int[]{0, 0};
            v.getLocationInWindow(l);
            int left = l[0];
            int top = l[1];
            int right = left + v.getWidth();
            int bottom = top + v.getHeight();
            return !(ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom);
        }
        return false;
    }

    /**
     * set activity to immersive mode without using fullscreen
     * <p>
     * in this mode, the window will extend to the status bar area,
     * but the bottom will not extend to the bottom navigation bar area.
     * </p>
     * @param enabled enable immersive mode
     */
    protected void setImmersiveMode(boolean enabled) {
        int mask = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        int flags = enabled ? mask : 0;
        Window window = getWindow();
        int originStatus = window.getDecorView().getSystemUiVisibility();
        int deStatus = (originStatus & ~mask) | (flags & mask);
        window.getDecorView().setSystemUiVisibility(deStatus);
    }

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        // update density
        DensityHelper.onActivityGetResources(resources);
        // update language config
        AppLanguageHelper.doOnContextGetResources(resources);
        return resources;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LanguageChangedEvent event) {
        this.recreate();
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
