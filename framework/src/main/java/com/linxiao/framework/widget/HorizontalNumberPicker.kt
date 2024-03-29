package com.linxiao.framework.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.linxiao.framework.R

/**
 * 水平方向数字选择器
 *
 * @author lx8421bcd
 * @version 1.0.0
 * @since 2015-11-15
 */
class HorizontalNumberPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    interface OnNumberChangeListener {
        fun onNumberChange(number: Int)
        fun onReachMinimum(min: Int)
        fun onReachMaximum(max: Int)
    }

    interface OnButtonClickListener {
        fun onAddClick()
        fun onSubtractClick()
    }

    companion object {
        private val TAG = HorizontalNumberPicker::class.java.getSimpleName()
    }

    private val rootView = LayoutInflater.from(context).inflate(R.layout.layout_number_picker, this, true)
    private var etNumber: TextView = rootView.findViewById(R.id.btn_subtract)
    private var btnSubtract: ImageView = rootView.findViewById(R.id.btn_add)
    private var btnAdd: ImageView = rootView.findViewById(R.id.et_number)
    private var min = 0
    private var max = 999
    var onNumberChangeListener: OnNumberChangeListener? = null
    var onButtonClickListener: OnButtonClickListener? = null
    var number = 0
        set(value) {
            btnAdd.setEnabled(true)
            btnSubtract.setEnabled(true)
            if (value > max) {
                field = max
                btnAdd.setEnabled(false)
                onNumberChangeListener?.onReachMaximum(max)
            } else if (value < min) {
                field = min
                btnSubtract.setEnabled(false)
                onNumberChangeListener?.onReachMinimum(min)
            } else {
                field = value
            }
            etNumber.text = field.toString()
            onNumberChangeListener?.onNumberChange(field)
        }

    init {
        this.isFocusable = true
        setFocusableInTouchMode(true)
        initAttrs(context, attrs)
        initListeners()
    }

    fun allowInput(isAllow: Boolean) {
        if (!isAllow) {
            etNumber.setKeyListener(null)
        }
    }

    fun setEditable(editable: Boolean) {
        btnAdd.setEnabled(editable)
        btnSubtract.setEnabled(editable)
        btnAdd.setAlpha(if (editable) 1f else 0.5f)
        btnSubtract.setAlpha(if (editable) 1f else 0.5f)
    }

    /**
     * 设置数字选择范围
     *
     * @param min 最小值
     * @param max 最大值
     */
    fun setRange(min: Int, max: Int) {
        this.min = min
        this.max = max
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        var numberBackground: Drawable? = null
        var addImage: Drawable? = null
        var subtractImage: Drawable? = null
        var numberTextSize = 12f
        var contentMargin = 0
        //设置属性
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HorizontalNumberPicker)
            val buttonWidth = typedArray.getDimensionPixelOffset(
                R.styleable.HorizontalNumberPicker_hnp_buttonWidth,
                0
            )
            val buttonHeight = typedArray.getDimensionPixelOffset(
                R.styleable.HorizontalNumberPicker_hnp_buttonHeight,
                0
            )
            if (buttonWidth > 0) {
                btnSubtract.layoutParams.width = buttonWidth
                btnAdd.layoutParams.width = buttonWidth
            }
            if (buttonHeight > 0) {
                btnSubtract.layoutParams.height = buttonHeight
                btnAdd.layoutParams.height = buttonHeight
            }
            val numberWidth = typedArray.getDimensionPixelOffset(
                R.styleable.HorizontalNumberPicker_hnp_numberWidth,
                0
            )
            val numberHeight = typedArray.getDimensionPixelOffset(
                R.styleable.HorizontalNumberPicker_hnp_numberHeight,
                0
            )
            if (numberWidth > 0) {
                etNumber.setWidth(numberWidth)
            }
            if (numberHeight > 0) {
                etNumber.setHeight(numberHeight)
            }
            addImage = typedArray.getDrawable(R.styleable.HorizontalNumberPicker_hnp_addImage)
            subtractImage = typedArray.getDrawable(R.styleable.HorizontalNumberPicker_hnp_subtractImage)
            numberBackground = typedArray.getDrawable(R.styleable.HorizontalNumberPicker_hnp_numberBackground)
            numberTextSize = typedArray.getDimensionPixelSize(R.styleable.HorizontalNumberPicker_hnp_numberTextSize, 14).toFloat()
            contentMargin = typedArray.getDimensionPixelOffset(R.styleable.HorizontalNumberPicker_hnp_contentMargin, 0)
            val numberColor = typedArray.getColorStateList(R.styleable.HorizontalNumberPicker_hnp_numberTextColor)
            if (numberColor != null) {
                etNumber.setTextColor(numberColor)
            } else {
                etNumber.setTextColor(Color.BLACK)
            }
            typedArray.recycle()
        }
        if (numberBackground == null) {
            numberBackground = provideDefaultBackground()
        }
        if (addImage == null) {
            addImage = ContextCompat.getDrawable(context, R.drawable.ic_hnp_add_default)
        }
        if (subtractImage == null) {
            subtractImage = ContextCompat.getDrawable(context, R.drawable.ic_hnp_sub_default)
        }
        etNumber.background = numberBackground
        etNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, numberTextSize)
        if (contentMargin > 0) {
            (etNumber.layoutParams as LayoutParams).setMargins(contentMargin, 0, contentMargin, 0)
        }
        btnAdd.setImageDrawable(addImage)
        btnSubtract.setImageDrawable(subtractImage)
    }

    private fun provideDefaultBackground(): Drawable {
        val bg = GradientDrawable()
        bg.setColor(Color.rgb(245, 245, 245))
        return bg
    }

    private fun initListeners() {
        btnSubtract.setOnClickListener { v: View? ->
            if (number > min) {
                number--
            }
            onButtonClickListener?.onSubtractClick()
        }
        btnAdd.setOnClickListener { v: View? ->
            if (number < max) {
                number++
            }
            onButtonClickListener?.onAddClick()
        }
        etNumber.onFocusChangeListener = OnFocusChangeListener { v: View?, hasFocus: Boolean ->
            if (!hasFocus) {
                if (etNumber.getText().isEmpty()) {
                    etNumber.text = min.toString()
                } else {
                    number = etNumber.getText().toString().toInt()
                }
                // 在失去焦点后如果软键盘开启则关闭软键盘
                postDelayed({
                    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
                        if (isActive(etNumber)) {
                            hideSoftInputFromWindow(windowToken, 0)
                        }
                    }
                }, 200)
            }
        }
        etNumber.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                clearFocus()
            }
            false
        }
    }

    /**
     * 监听系统back键,通过back键关闭软键盘时,清除EditText焦点
     */
    override fun dispatchKeyEventPreIme(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            clearFocus()
        }
        return super.dispatchKeyEventPreIme(event)
    }

    override fun clearFocus() {
        super.clearFocus()
        postDelayed({ etNumber.clearFocus() }, 200)
    }
}
