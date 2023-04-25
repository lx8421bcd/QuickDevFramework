package com.linxiao.framework.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.linxiao.framework.R
import com.linxiao.framework.common.DensityHelper
import com.linxiao.framework.databinding.ViewSettingItemViewBinding

/**
 * 列表项视图<BR></BR>
 * 主要提供的功能(从左到右):<BR></BR>
 *
 * @author lx8421bcd
 * @since 2017-04-17
 */
class SettingItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    enum class ItemType(val code: Int) {
        NORMAL(0),
        SWITCH(1),
    }

    private val DEFAULT_VIEW_HEIGHT = dip2Px(50f)

    private val viewBinding: ViewSettingItemViewBinding

    private var itemType = ItemType.NORMAL
    private var clickCListener: OnClickListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_setting_item_view, this, true)
        viewBinding = ViewSettingItemViewBinding.bind(this)
        val styleAttrs = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView)
        viewBinding.ivIcon.apply {
            styleAttrs.getDrawable(R.styleable.SettingItemView_itemview_icon)?.let {
                val iconSize = styleAttrs.getDimensionPixelSize(R.styleable.SettingItemView_itemview_iconSize, 0)
                this.setImageDrawable(it)
                this.layoutParams.width = iconSize
                this.layoutParams.height = iconSize
            }
        }
        viewBinding.tvName.apply {
            styleAttrs.getText(R.styleable.SettingItemView_itemview_name)?.let {
                this.text = it
            }
            styleAttrs.getDimensionPixelSize(R.styleable.SettingItemView_itemview_nameTextSize, 14).let {
                this.setTextSize(TypedValue.COMPLEX_UNIT_SP, it.toFloat())
            }
            styleAttrs.getColorStateList(R.styleable.SettingItemView_itemview_nameTextColor)?.let {
                this.setTextColor(it)
            }
        }
        viewBinding.tvInfo.apply {
            styleAttrs.getText(R.styleable.SettingItemView_itemview_info)?.let {
                this.text = it
            }
            styleAttrs.getDimensionPixelSize(R.styleable.SettingItemView_itemview_infoTextSize, 14).let {
                this.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.toFloat())
            }
            styleAttrs.getColorStateList(R.styleable.SettingItemView_itemview_infoTextColor)?.let {
                this.setTextColor(it)
            }
        }
        styleAttrs.getDrawable(R.styleable.SettingItemView_itemview_infoImageSrc)?.let {
            viewBinding.ivInfoImage.setImageDrawable(it)
        }
        styleAttrs.getBoolean(R.styleable.SettingItemView_itemview_showMoreIcon, true).let {
            viewBinding.ivMore.isVisible = it
        }
        // divider init
        viewBinding.divider.apply {
            this.isVisible = styleAttrs.getBoolean(R.styleable.SettingItemView_itemview_dividerVisible, false)
            val defaultDividerColor = ContextCompat.getColor(context, R.color.default_divider_color)
            minimumHeight = styleAttrs.getDimensionPixelOffset(R.styleable.SettingItemView_itemview_dividerSize, 1)
            setBackgroundColor(styleAttrs.getColor(R.styleable.SettingItemView_itemview_dividerColor, defaultDividerColor))
        }
        viewBinding.cbSwitch.isChecked = styleAttrs.getBoolean(R.styleable.SettingItemView_itemview_switch_checked, false)
        // item type init
        val inputTypeCode = styleAttrs.getInt(R.styleable.SettingItemView_itemview_itemType, ItemType.NORMAL.code)
        val itemType = ItemType.values().find { it.code == inputTypeCode } ?: ItemType.NORMAL
        setItemType(itemType)
        styleAttrs.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            setMeasuredDimension(width, DEFAULT_VIEW_HEIGHT)
            layoutParams.height = DEFAULT_VIEW_HEIGHT
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        viewBinding.tvName.isEnabled = enabled
        viewBinding.ivIcon.isEnabled = enabled
    }

    override fun setOnClickListener(l: OnClickListener?) {
        clickCListener = l
        super.setOnClickListener {
            if (itemType == ItemType.SWITCH) {
                viewBinding.cbSwitch.isChecked = !viewBinding.cbSwitch.isChecked
            }
            clickCListener?.onClick(this)
        }
    }

    fun setItemType(itemType: ItemType) {
        when(itemType) {
            ItemType.SWITCH -> {
                viewBinding.tvInfo.isVisible = false
                viewBinding.ivInfoImage.isVisible = false
                viewBinding.ivMore.isVisible = false
                viewBinding.cbSwitch.isVisible = true
            }
            else -> {
                viewBinding.cbSwitch.isVisible = false
                viewBinding.cbSwitch.isChecked = false
            }
        }
        setOnClickListener(clickCListener)
        this.itemType = itemType
    }

    fun getItemIconView(): ImageView {
        return viewBinding.ivIcon
    }

    fun getNameView(): TextView {
        return viewBinding.tvName
    }

    fun getInfoView(): TextView {
        return viewBinding.tvInfo
    }

    fun getInfoImageView(): ImageView {
        return viewBinding.ivInfoImage
    }

    fun getSwitchView(): CheckBox {
        return viewBinding.cbSwitch
    }

    fun getDividerView(): View {
        return viewBinding.divider
    }

    /**
     * 设置是否显示更多标识
     *
     * @param show true显示,false不显示
     */
    fun showMoreIcon(show: Boolean) {
        viewBinding.ivMore.visibility = if (show) VISIBLE else GONE
    }

    fun setDividerVisible(show: Boolean) {
        viewBinding.divider.visibility = if (show) VISIBLE else GONE
    }

    private fun dip2Px(dip: Float): Int {
        return (dip * resources.displayMetrics.density + 0.5f).toInt()
    }
}