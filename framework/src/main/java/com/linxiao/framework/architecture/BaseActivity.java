package com.linxiao.framework.architecture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;

import com.linxiao.framework.common.DensityHelper;
import com.linxiao.framework.permission.PermissionManager;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import java.util.Map;

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
    private final Map<Integer, ActivityResultListener> activityCallbackMap = new ArrayMap<>();


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
        activityCallbackMap.clear();
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
        ActivityResultListener listener = activityCallbackMap.get(requestCode);
        if (listener != null) {
            listener.onResultCallback(resultCode, data);
            activityCallbackMap.remove(requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handleCallback(this, requestCode, permissions, grantResults);
    }

    public void startActivityForCallback(Intent intent, ActivityResultListener callback) {
        if (callback == null) {
            return;
        }
        long time = System.currentTimeMillis();
        int requestCode = (int) (time - time / 1000 * 1000);
        activityCallbackMap.put(requestCode, callback);
        startActivityForResult(intent, requestCode);
    }

    public void removeActivityResultCallback(int requestCode, ActivityResultListener callback) {
        if (callback == null) {
            return;
        }
        activityCallbackMap.put(requestCode, callback);
    }

    private void addActivityResultCallback(int requestCode) {
        activityCallbackMap.remove(requestCode);
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
        DensityHelper.onActivityGetResources(resources);
        return resources;
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
