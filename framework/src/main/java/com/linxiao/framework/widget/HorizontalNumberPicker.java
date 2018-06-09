package com.linxiao.framework.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linxiao.framework.R;


/**
 * 水平方向数字选择器
 * <p>
 * TODO:需细调在缺少宽高设定情况下的默认宽高设置
 * TODO:设定在Orientation为Vertical的情况下布局处理
 * TODO:通过第三方输入法的收起键盘按钮收起键盘时取消当前EditText焦点
 * </p>
 *
 * @author linxiao
 * @version 1.0.0
 * Create on 2015-11-15
 */
public class HorizontalNumberPicker extends LinearLayout {

    private static final String TAG = HorizontalNumberPicker.class.getSimpleName();

    private InputMethodManager imm;

    public interface OnNumberChangeListener {
        void onNumberCount(int number);

        void onNumberChange(int number);

        void onReachMinimum(int min);

        void onReachMaximum(int max);
    }

    public interface OnBtnClickListener {
        void OnAddClick();
        void OnReduceClick();
    }

    public void setOnBtnClickListener(OnBtnClickListener listener){
        onBtnClickListener = listener;
    }

    public void setOnNumberChangeListener(OnNumberChangeListener listener) {
        onNumberChangeListener = listener;
    }

    private NumberPickerEditText etNumber;

    private TextView btnReduce;

    private TextView btnAdd;

    private int number = 0;
    private int min = 0;
    private int max = 999;
    private OnNumberChangeListener onNumberChangeListener;
    private OnBtnClickListener onBtnClickListener ;


    private boolean isAutoChangeNumber = true ;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HorizontalNumberPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
        initAttrs(context, attrs);
    }

    public void allowInput(boolean isAllow){
        if (!isAllow){
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
        //初始化软件盘监听
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //初始化 "-" 号
        LayoutParams reduceParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        btnReduce = new TextView(context);
        btnReduce.setGravity(Gravity.CENTER);
        btnReduce.setText("-");
        btnReduce.setLayoutParams(reduceParams);
        //初始化 "+" 号
        LayoutParams addParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        btnAdd = new TextView(context);
        btnAdd.setGravity(Gravity.CENTER);
        btnAdd.setText("+");
        btnAdd.setLayoutParams(addParams);
        //初始化数字框
        LayoutParams etParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        etNumber = new NumberPickerEditText(context);
        etNumber.setText(String.valueOf(min));
        etNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        etNumber.setSingleLine(true);
        etNumber.setGravity(Gravity.CENTER);
        etNumber.setMinEms(2);
        etNumber.setLayoutParams(etParams);
        etNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        etNumber.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etNumber.setPadding(0, 0, 0, 0);

        this.addView(btnReduce);
        this.addView(etNumber);
        this.addView(btnAdd);

        initListeners();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        ColorStateList buttonTextColor = null;
        Drawable buttonBackground = null;
        Drawable numberBackground = null;
        float buttonTextSize = 12f;
        float numberTextSize = 12f;
        int contentMargin = 0;
        //设置属性
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HorizontalNumberPicker);
            int buttonWidth = typedArray.getDimensionPixelOffset(
                    R.styleable.HorizontalNumberPicker_hnp_buttonWidth, 0);
            if (buttonWidth > 0) {
                btnReduce.setWidth(buttonWidth);
                btnAdd.setWidth(buttonWidth);
            }
            int buttonHeight = typedArray.getDimensionPixelOffset(
                    R.styleable.HorizontalNumberPicker_hnp_buttonHeight, 0);
            if (buttonHeight > 0) {
                btnReduce.setHeight(buttonHeight);
                btnAdd.setHeight(buttonHeight);
            }
            int numberWidth = typedArray.getDimensionPixelOffset(
                    R.styleable.HorizontalNumberPicker_hnp_numberWidth, 0);
            if (numberWidth > 0) {
                etNumber.setWidth(numberWidth);
            }
            int numberHeight = typedArray.getDimensionPixelOffset(
                    R.styleable.HorizontalNumberPicker_hnp_numberHeight, 0);
            if (numberHeight > 0) {
                etNumber.setHeight(numberHeight);
            }
            buttonTextColor = typedArray.getColorStateList(
                    R.styleable.HorizontalNumberPicker_hnp_buttonTextColor);
            buttonBackground = typedArray.getDrawable(
                    R.styleable.HorizontalNumberPicker_hnp_buttonBackground);
            numberBackground = typedArray.getDrawable(
                    R.styleable.HorizontalNumberPicker_hnp_numberBackground);
            buttonTextSize = typedArray.getDimensionPixelSize(
                    R.styleable.HorizontalNumberPicker_hnp_buttonTextSize, 14);
            numberTextSize = typedArray.getDimensionPixelSize(
                    R.styleable.HorizontalNumberPicker_hnp_numberTextSize, 14);
            contentMargin = typedArray.getDimensionPixelOffset(
                    R.styleable.HorizontalNumberPicker_hnp_contentMargin, 0);
            ColorStateList numberColor = typedArray.getColorStateList(
                    R.styleable.HorizontalNumberPicker_hnp_numberTextColor);
            if (numberColor != null) {
                etNumber.setTextColor(numberColor);
            } else {
                etNumber.setTextColor(Color.rgb(128, 128, 128));
            }
            typedArray.recycle();
        }
        if (buttonBackground == null) {
            buttonBackground = provideDefaultBackground();
        }
        if (buttonTextColor == null) {
            buttonTextColor = provideDefaultTextColorList();
        }
        if (numberBackground == null) {
            numberBackground = provideDefaultBackground();
        }
        etNumber.setBackgroundDrawable(numberBackground);
        etNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, numberTextSize);
        btnReduce.setBackgroundDrawable(buttonBackground);
        btnAdd.setBackgroundDrawable(buttonBackground);
        btnReduce.setTextColor(buttonTextColor);
        btnAdd.setTextColor(buttonTextColor);
        btnReduce.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonTextSize);
        btnReduce.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonTextSize);
        if (contentMargin > 0) {
            ((LayoutParams) etNumber.getLayoutParams()).setMargins(contentMargin, 0, contentMargin, 0);
        }

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
//        if(btnReduce.getLayoutParams().width < h) {
//            btnReduce.setWidth(h);
//            btnAdd.setWidth(h);
//        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int h = b - t, w = r - l;
//        if(btnReduce.getLayoutParams().width < h) {
//            btnReduce.getLayoutParams().width = h;
//            btnAdd.getLayoutParams().width = h;
//        }
//        this.invalidate();
    }


    private void initListeners() {
        btnReduce.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBtnClickListener != null) {
                    onBtnClickListener.OnReduceClick();
                }
                if (number > min && isAutoChangeNumber) {
                    number--;
                    setNumber(number);
                }
            }
        });

        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBtnClickListener != null) {
                    onBtnClickListener.OnAddClick();
                }
                if (number < max && isAutoChangeNumber) {
                    number++;
                    setNumber(number);
                }
            }
        });

        etNumber.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
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
        etNumber.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
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

    /**
     * 发送延时事件,在软键盘收起后清除EditText焦点
     */
    public void clearNumberFocus() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                etNumber.clearFocus();
            }
        }, 200);
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
        if (num > max) {
            if (onNumberChangeListener != null) {
                onNumberChangeListener.onReachMaximum(max);
            }
            number = max;
        } else if (num < min) {
            if (onNumberChangeListener != null) {
                onNumberChangeListener.onReachMinimum(min);
            }
            number = min;
        } else {
            if (num != number && onNumberChangeListener != null) {
                onNumberChangeListener.onNumberChange(num);
            }
            number = num;
        }
        if (onNumberChangeListener != null) {
            onNumberChangeListener.onNumberCount(number);

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

    class NumberPickerEditText extends EditText {

        public NumberPickerEditText(Context context) {
            super(context);
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            return super.onKeyDown(keyCode, event);
        }


    }

}
