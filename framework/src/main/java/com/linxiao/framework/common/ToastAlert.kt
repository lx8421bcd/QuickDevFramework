package com.linxiao.framework.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.linxiao.framework.R;

import java.util.HashMap;
import java.util.Map;

/**
 * A powerful util of {@link Toast}. Must init it in your Application.
 * Typicaly use cases look something
 * like this:
 * <pre>
 * <code>
 *     // show a simple toast immediately that only display a text
 *     ToastAlert.show("this is a text only toast");
 *
 *     // show a simple toast immediately that with a text and a icon
 *     ToastAlert.show("this is toast with a icon", R.drawable.icon);
 *
 *     // put a simple toast in a queue that only display text
 *     ToastAlert.show("this is a text only toast");
 *
 *     // put a simple toast in a queue that with a text and a icon
 *     ToastAlert.enqueue("this is toast with a icon", R.drawable.icon);
 * </code>
 * </pre>
 * <p>
 * more powerful usage
 * like this:
 * <pre>
 * <code>
 *     ToastAlert.create("this is the toast text")
 *         .iconResId(R.drawable.your_icon)
 *         .duration(4000)              // we use millis as time unit
 *         .gravity(Gravity.TOP, 200)   // set Toast to be shown at top, the top offset is 200
 *         .show();                     // show the Toast immediately, or use 'enqueue()' to put it in a queue.
 * </code>
 * </pre>
 * <p>
 * more and more powerful usage - use custom layout(please see wiki)
 *
 * @author linxiao, wangxin
 * @since 2016/11/26
 */
public class ToastAlert {

    private ToastAlert() {
    }

    /**
     * @param context
     * @param message
     * @param timeMills
     * @see #create(CharSequence)
     * @see #show(CharSequence)
     * @see ToastInfo#duration()
     */
    public static void showToast(Context context, CharSequence message, int timeMills) {
        create(message).duration(timeMills).show();
    }

    /**
     * @param context
     * @param message
     * @see #show(CharSequence)
     * @see #create(CharSequence)
     */
    public static void showToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    //------------------------------------以下为新功能核心代码区---------------------------------------

    @SuppressWarnings("WeakerAccess")
    public static final Factory<ToastInfo> DEFAULT_FACTORY = new Factory<ToastInfo>() {
        @Override
        public ToastInfo newToastInfo() {
            return new ToastInfo(this);
        }

        @TargetApi(Build.VERSION_CODES.CUPCAKE)
        @Override
        public View onCreateView(ToastInfo toastInfo) {
            final CharSequence text = toastInfo.text();
            if (TextUtils.isEmpty(text) && toastInfo.iconResId() == 0) return null;

            final TextView textView = new TextView(ContextProviderKt.getGlobalContext());
            textView.setText(text);
            textView.setTextColor(Color.WHITE);

            if (toastInfo.iconResId() == 0) {
                final int padding = ToastInfo.dpToPx(15);
                textView.setPadding(padding, padding, padding, padding);
            } else {
                final int padding = ToastInfo.dpToPx(20);
                textView.setPadding(padding, padding, padding, padding);
                textView.setCompoundDrawablePadding(ToastAlert.ToastInfo.dpToPx(15));
                textView.setCompoundDrawablesWithIntrinsicBounds(0, toastInfo.iconResId(), 0, 0);
            }

            if (toastInfo.backgroundResId() == 0) {
                textView.setBackgroundResource(R.drawable.toast);
            } else {
                textView.setBackgroundResource(toastInfo.backgroundResId());
            }

            // if duration is not set manually, auto adjust duration depending on the text length
            if (toastInfo.duration() <= 0) {
                if (text.length() > 50) {
                    toastInfo.duration(5000);
                } else if (text.length() > 25) {
                    toastInfo.duration(3500);
                } else {
                    toastInfo.duration(2000);
                }
            }

            // if icon is set, let the Toast shown at the center of screen
            final int gravity = toastInfo.gravity();
            if (toastInfo.iconResId() != 0) {
                toastInfo.gravity(gravity == Gravity.NO_GRAVITY ? Gravity.CENTER : gravity, toastInfo.offsetY());
            } else {
                toastInfo.gravity(gravity == Gravity.NO_GRAVITY ? Gravity.BOTTOM : gravity, toastInfo.offsetY());
            }
            return textView;
        }
    };
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    @SuppressWarnings("WeakerAccess")
    public interface Factory<T extends ToastInfo> {
        T newToastInfo();

        View onCreateView(T toastInfo);
    }

    public interface OnStageListener {
        void onShow(ToastDelegate toastDelegate);

        void onDismiss(ToastDelegate toastDelegate);
    }

    private static Factory sFactory = DEFAULT_FACTORY;


    public static void setDefaultFactory(Factory factory) {
        sFactory = factory != null ? factory : DEFAULT_FACTORY;
    }

    public static <T extends ToastInfo> T create(CharSequence cs) {
        //noinspection unchecked
        return (T) sFactory.newToastInfo().text(cs);
    }

    public static void show(int stringResId) {
        show(ContextProviderKt.getGlobalContext().getString(stringResId));
    }

    public static void show(int stringResId, int iconResId) {
        show(ContextProviderKt.getGlobalContext().getString(stringResId), iconResId);
    }

    public static void enqueue(int stringResId) {
        enqueue(ContextProviderKt.getGlobalContext().getString(stringResId));
    }

    public static void enqueue(int stringResId, int iconResId) {
        enqueue(ContextProviderKt.getGlobalContext().getString(stringResId), iconResId);
    }


    public static void show(CharSequence cs) {
        sFactory.newToastInfo().text(cs).show();
    }

    public static void show(CharSequence cs, int iconResId) {
        sFactory.newToastInfo().text(cs).iconResId(iconResId).show();
    }

    public static void enqueue(CharSequence cs) {
        sFactory.newToastInfo().text(cs).enqueue();
    }

    public static void enqueue(CharSequence cs, int iconResId) {
        sFactory.newToastInfo().text(cs).iconResId(iconResId).enqueue();
    }


    @SuppressWarnings("WeakerAccess")
    public static class ToastInfo {

        public static int dpToPx(float dp) {
            final float scale = ContextProviderKt.getGlobalContext().getResources().getDisplayMetrics().density;
            return (int) (dp * scale + 0.5f);
        }

        protected final Factory mFactory;
        protected CharSequence mText;
        protected int mBackgroundResId = 0;
        protected int mIconResId = 0;
        protected Map<String, Object> mExtra = null;

        public ToastInfo(Factory factory) {
            mFactory = factory;
        }

        public ToastInfo text(CharSequence cs) {
            mText = cs;
            return this;
        }

        public CharSequence text() {
            return mText;
        }

        public ToastInfo backgroundResId(int resId) {
            mBackgroundResId = resId;
            return this;
        }

        public int backgroundResId() {
            return mBackgroundResId;
        }

        public ToastInfo iconResId(int resId) {
            mIconResId = resId;
            return this;
        }

        public int iconResId() {
            return mIconResId;
        }

        public ToastInfo extra(String key, Object value) {
            if (mExtra == null) mExtra = new HashMap<String, Object>();
            mExtra.put(key, value);
            return this;
        }

        public Object extra(String key) {
            return mExtra == null ? null : mExtra.get(key);
        }


        protected int mDuration = 0;
        protected int mGravity = Gravity.NO_GRAVITY;
        protected int mOffsetY = 200;

        public ToastInfo duration(int duration) {
            mDuration = duration;
            return this;
        }

        public int duration() {
            return mDuration;
        }

        public ToastInfo gravity(int gravity, int offsetY) {
            mGravity = gravity;
            mOffsetY = offsetY;
            return this;
        }

        public int gravity() {
            return mGravity;
        }

        public int offsetY() {
            return mOffsetY;
        }


        protected OnStageListener mOnStageListener;

        public ToastInfo onStageListener(OnStageListener onStageListener) {
            mOnStageListener = onStageListener;
            return this;
        }

        public OnStageListener onStageListener() {
            return mOnStageListener;
        }

        public final ToastDelegate build() {
            //noinspection unchecked
            final View view = mFactory.onCreateView(this);
            return view == null ? null : new ToastDelegate(view, this);
        }

        public final void show() {
            final ToastDelegate toastDelegate = build();
            if (toastDelegate != null) toastDelegate.show();
        }

        public final void enqueue() {
            final ToastDelegate toastDelegate = build();
            if (toastDelegate != null) toastDelegate.enqueue();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static final class ToastDelegate {

        public static int sCheckInterval = 335;

        private static ToastDelegate sLastToastDelegate = null;// memory leak 28 bytes, forget it!!

        private static long currentTimestamp() {
            return SystemClock.uptimeMillis();
        }

        private final Runnable mUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                update();
            }
        };
        private final Runnable mEnqueueRunnable = new Runnable() {
            @Override
            public void run() {
                enqueue();
            }
        };

        private final int mDuration;
        private final ToastInfo mToastInfo;
        private Toast mToast;
        private long mStartTimestamp;

        public ToastDelegate(View view, ToastInfo toastInfo) {
            final Toast toast = new Toast(ContextProviderKt.getGlobalContext());
            toast.setView(view);
            toast.setGravity(toastInfo.gravity(), 0, toastInfo.offsetY());
            toast.setDuration(Toast.LENGTH_LONG);

            mToast = toast;
            mDuration = toastInfo.duration() <= 0 ? 2000 : toastInfo.duration();
            mToastInfo = toastInfo;
        }

        public ToastInfo getToastInfo() {
            return mToastInfo;
        }

        public Toast getToast() {
            return mToast;
        }

        public boolean isShowing() {
            return mToast != null && mStartTimestamp > 0;
        }

        private void update() {
            if (mStartTimestamp == 0) mStartTimestamp = currentTimestamp();

            if (currentTimestamp() - mStartTimestamp < mDuration) {
                mToast.show();
                HANDLER.postDelayed(mUpdateRunnable, sCheckInterval);
            } else {
                cancel();
            }
        }

        public void show() {
            if (sLastToastDelegate != null) sLastToastDelegate.cancel();

            sLastToastDelegate = this;
            update();
            if (mToastInfo.mOnStageListener != null) {
                mToastInfo.mOnStageListener.onShow(this);
            }
        }

        public void enqueue() {
            if (sLastToastDelegate == null) {
                show();
                return;
            }

            final long timeRemaining = sLastToastDelegate.mStartTimestamp
                    + sLastToastDelegate.mDuration - currentTimestamp();
            if (timeRemaining <= 0) {
                sLastToastDelegate.cancel();
                show();
                return;
            }

            HANDLER.postDelayed(mEnqueueRunnable, timeRemaining);
        }

        public void cancel() {
            if (!isShowing()) return;

            mToast.cancel();
            mToast = null;
            HANDLER.removeCallbacks(mUpdateRunnable);
            HANDLER.removeCallbacks(mEnqueueRunnable);
            if (mToastInfo.mOnStageListener != null) {
                mToastInfo.mOnStageListener.onDismiss(this);
            }
        }
    }
}
