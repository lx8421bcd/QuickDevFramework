package com.linxiao.framework.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.linxiao.framework.R;

/**
 * 水平方向数字选择器>
 *
 * @author linxiao
 * @version 1.0.0
 * Create on 2015-11-15
 */
public class HorizontalNumberPicker extends RelativeLayout {

    private static final String TAG = HorizontalNumberPicker.class.getSimpleName();

    private InputMethodManager imm;

    public interface OnNumberChangeListener {

        void onNumberChange(int number);

        void onReachMinimum(int min);

        void onReachMaximum(int max);
    }

    public interface OnBtnClickListener {
        void OnAddClick();

        void OnSubtractClick();
    }

    public void setOnBtnClickListener(OnBtnClickListener listener) {
        onBtnClickListener = listener;
    }

    public void setOnNumberChangeListener(OnNumberChangeListener listener) {
        onNumberChangeListener = listener;
    }

    private TextView etNumber;

    private ImageView btnSubtract;

    private ImageView btnAdd;

    private int number = 0;
    private int min = 0;
    private int max = 999;
    private OnNumberChangeListener onNumberChangeListener;
    private OnBtnClickListener onBtnClickListener;


    private boolean isAutoChangeNumber = true;

    public HorizontalNumberPicker(Context context) {
        super(context);
        initView(context);
        initAttrs(context, null);
    }

    public HorizontalNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initAttrs(context, attrs);
    }

    public HorizontalNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initAttrs(context, attrs);
    }

    public HorizontalNumberPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
        initAttrs(context, attrs);
    }

    public void allowInput(boolean isAllow) {
        if (!isAllow) {
            etNumber.setKeyListener(null);
        }

    }

    public boolean isAutoChangeNumber() {
        return isAutoChangeNumber;
    }

    public void setAutoChangeNumber(boolean autoChangeNumber) {
        isAutoChangeNumber = autoChangeNumber;
    }

    private void initView(Context context) {
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        LayoutInflater.from(context).inflate(R.layout.layout_number_picker, this, true);
        //初始化软件盘监听
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        btnSubtract = findViewById(R.id.btn_subtract);
        btnAdd = findViewById(R.id.btn_add);
        etNumber = findViewById(R.id.et_number);

        initListeners();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        Drawable numberBackground = null;
        Drawable addImage = null;
        Drawable subtractImage = null;
        float numberTextSize = 12f;
        int contentMargin = 0;
        //设置属性
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HorizontalNumberPicker);
            int buttonWidth = typedArray.getDimensionPixelOffset(R.styleable.HorizontalNumberPicker_hnp_buttonWidth, 0);
            int buttonHeight = typedArray.getDimensionPixelOffset(R.styleable.HorizontalNumberPicker_hnp_buttonHeight, 0);
            if (buttonWidth > 0) {
                btnSubtract.getLayoutParams().width = buttonWidth;
                btnAdd.getLayoutParams().width = buttonWidth;
            }
            if (buttonHeight > 0) {
                btnSubtract.getLayoutParams().height = buttonHeight;
                btnAdd.getLayoutParams().height = buttonHeight;
            }

            int numberWidth = typedArray.getDimensionPixelOffset(R.styleable.HorizontalNumberPicker_hnp_numberWidth, 0);
            int numberHeight = typedArray.getDimensionPixelOffset(R.styleable.HorizontalNumberPicker_hnp_numberHeight, 0);
            if (numberWidth > 0) {
                etNumber.setWidth(numberWidth);
            }
            if (numberHeight > 0) {
                etNumber.setHeight(numberHeight);
            }
            addImage = typedArray.getDrawable(R.styleable.HorizontalNumberPicker_hnp_addImage);
            subtractImage = typedArray.getDrawable(R.styleable.HorizontalNumberPicker_hnp_subtractImage);

            numberBackground = typedArray.getDrawable(R.styleable.HorizontalNumberPicker_hnp_numberBackground);
            numberTextSize = typedArray.getDimensionPixelSize(R.styleable.HorizontalNumberPicker_hnp_numberTextSize, 14);
            contentMargin = typedArray.getDimensionPixelOffset(R.styleable.HorizontalNumberPicker_hnp_contentMargin, 0);
            ColorStateList numberColor = typedArray.getColorStateList(R.styleable.HorizontalNumberPicker_hnp_numberTextColor);
            if (numberColor != null) {
                etNumber.setTextColor(numberColor);
            } else {
                etNumber.setTextColor(Color.rgb(128, 128, 128));
            }
            typedArray.recycle();
        }
        if (numberBackground == null) {
            numberBackground = provideDefaultBackground();
        }
        if (addImage == null) {
            addImage = ContextCompat.getDrawable(context, R.drawable.ic_hnp_add_default);
        }
        if (subtractImage == null) {
            subtractImage = ContextCompat.getDrawable(context, R.drawable.ic_hnp_sub_default);
        }
        etNumber.setBackgroundDrawable(numberBackground);
        etNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, numberTextSize);
        if (contentMargin > 0) {
            ((LayoutParams) etNumber.getLayoutParams()).setMargins(contentMargin, 0, contentMargin, 0);
        }
        btnAdd.setImageDrawable(addImage);
        btnSubtract.setImageDrawable(subtractImage);
    }

    private Drawable provideDefaultBackground() {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(245, 245, 245));
        return bg;
    }

    private ColorStateList provideDefaultTextColorList() {
        int colors[] = {Color.rgb(128, 128, 128), Color.rgb(102, 102, 102)};
        int states[][] = new int[2][];
        states[0] = new int[]{android.R.attr.state_enabled};
        states[1] = new int[]{};
        return new ColorStateList(states, colors);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        if(btnSubtract.getLayoutParams().width < h) {
//            btnSubtract.setWidth(h);
//            btnAdd.setWidth(h);
//        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int h = b - t, w = r - l;
//        if(btnSubtract.getLayoutParams().width < h) {
//            btnSubtract.getLayoutParams().width = h;
//            btnAdd.getLayoutParams().width = h;
//        }
//        this.invalidate();
    }


    private void initListeners() {
        btnSubtract.setOnClickListener(v -> {
            if (onBtnClickListener != null) {
                onBtnClickListener.OnSubtractClick();
            }
            if (number > min && isAutoChangeNumber) {
                number--;
                setNumber(number);
            }
        });

        btnAdd.setOnClickListener(v -> {
            if (onBtnClickListener != null) {
                onBtnClickListener.OnAddClick();
            }
            if (number < max && isAutoChangeNumber) {
                number++;
                setNumber(number);
            }
        });

        etNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (etNumber.getText().length() == 0) {
                    etNumber.setText(String.valueOf(min));
                } else {
                    setNumber(Integer.parseInt(etNumber.getText().toString()));
                }
                // 在失去焦点后如果软键盘开启则关闭软键盘
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (imm.isActive(etNumber)) {
                            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                }, 200);
            }
        });

        etNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    clearNumberFocus();
                }
                return false;
            }
        });
        etNumber.setOnTouchListener((v, event) -> false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * 监听系统back键,通过back键关闭软键盘时,清除EditText焦点
     */
    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            clearNumberFocus();
        }
        return super.dispatchKeyEventPreIme(event);
    }

    public void setEditable(boolean editable) {
        btnAdd.setEnabled(editable);
        btnSubtract.setEnabled(editable);
        btnAdd.setAlpha(editable ? 1f : 0.5f);
        btnSubtract.setAlpha(editable ? 1f : 0.5f);
    }

    /**
     * 发送延时事件,在软键盘收起后清除EditText焦点
     */
    public void clearNumberFocus() {
        new Handler().postDelayed(() -> etNumber.clearFocus(), 200);
    }

    /**
     * 设置数字选择范围
     *
     * @param min 最小值
     * @param max 最大值
     */
    public void setRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * 设置数字,超过最大值或者低于最小值将不会生效,并调用对应的回调方法
     *
     * @param num 设置数字
     */
    public void setNumber(int num) {
        btnAdd.setEnabled(true);
        btnSubtract.setEnabled(true);
        if (num > max) {
            if (onNumberChangeListener != null) {
                onNumberChangeListener.onReachMaximum(max);
            }
            number = max;
            btnAdd.setEnabled(false);
        } else if (num < min) {
            if (onNumberChangeListener != null) {
                onNumberChangeListener.onReachMinimum(min);
            }
            number = min;
            btnSubtract.setEnabled(false);
        } else {
            number = num;
        }
        if (onNumberChangeListener != null) {
            onNumberChangeListener.onNumberChange(number);

        }
        etNumber.setText(String.valueOf(number));
    }

    public int getNumber() {
        return number;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
