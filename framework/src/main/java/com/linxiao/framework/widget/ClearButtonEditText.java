package com.linxiao.framework.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.linxiao.framework.R;

/**
 * 带清空按钮的EditText
 * <p>注意此不要使用DrawableRight属性,drawableRight已被清空按钮占用,可以用自己的清空按钮覆盖</p>
 *
 * @author linxiao
 * @version 1.0
 */
public class ClearButtonEditText extends EditText {

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length() > 0 && hasFocus() && isEnabled() && isFocusable()) {
                showClearButton(true);
            }
            else {
                showClearButton(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private Drawable clearButtonDrawable;
    private Drawable cachedDrawable;

    public ClearButtonEditText(Context context) {
        super(context);
        initView(context);
        this.addTextChangedListener(watcher);
    }

    public ClearButtonEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        this.addTextChangedListener(watcher);

    }

    public ClearButtonEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        this.addTextChangedListener(watcher);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClearButtonEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
        this.addTextChangedListener(watcher);

    }

    private void initView(Context context) {
        clearButtonDrawable = ContextCompat.getDrawable(context, R.drawable.ic_btn_clear);
        cachedDrawable = clearButtonDrawable;
        int length = (int) this.getTextSize();
        clearButtonDrawable.setBounds(0, 0, length, length);
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && getText().length() > 0) {
                    showClearButton(true);
                }
                else {
                    showClearButton(false);
                }
            }
        });
    }

    private void showClearButton(boolean isShow) {
        if(isShow) {
            this.setCompoundDrawables(null, null, clearButtonDrawable, null);
        }
        else {
            this.setCompoundDrawables(null, null, null, null);
        }
    }

    public void setClearButtonVisible(boolean visible) {
        clearButtonDrawable = visible ? cachedDrawable : null;
    }

    /**
     * 添加触摸事件 点击之后 出现 清空editText的效果
     */
    @Override
    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        if ((this.getCompoundDrawables()[2] != null) && (paramMotionEvent.getAction() == 1)) {
            Rect rBounds = this.clearButtonDrawable.getBounds();
            int i = (int) paramMotionEvent.getRawX();// 距离屏幕的距离
            // int i = (int) paramMotionEvent.getX();//距离边框的距离
            if (i > getRight() - 3 * rBounds.width()) {
                setText("");
                paramMotionEvent.setAction(MotionEvent.ACTION_CANCEL);
            }
        }
        return super.onTouchEvent(paramMotionEvent);
    }

}
