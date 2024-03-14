package com.linxiao.framework.list

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import kotlin.math.ceil

/**
 * RecyclerView 等距布局装饰器
 *
 * 用于实现RecyclerView中各个项目之间拥有相同间隔的布局效果,
 * 不作特殊配置默认为纵向布局，单列
 *
 * **此装饰器不适用于瀑布流布局**
 *
 * @author linxiao
 * @since 2017-11-09
 */
class EquidistantDecoration(// 布局方向
    private val orientation: Int, // 行/列数
    private val spanCount: Int, // 距离大小
    private val spacingSize: Int
) : ItemDecoration() {

    companion object {
        /**
         * 纵向
         */
        const val VERTICAL = 0

        /**
         * 横向布局
         */
        const val HORIZONTAL = 1

        /**
         * 边界控制，使用spacing值
         */
        const val BORDER_DEFAULT = -1
    }

    // 间距内容
    private var spacingDrawable: Drawable

    // 边界宽度
    private var borderLeftWidth = BORDER_DEFAULT
    private var borderRightWidth = BORDER_DEFAULT
    private var borderTopWidth = BORDER_DEFAULT
    private var borderBottomWidth = BORDER_DEFAULT

    /**
     * @param spanCount 列/行数
     * @param spacingSize 间隔大小，单位 px
     */
    constructor(spanCount: Int, spacingSize: Int) : this(VERTICAL, spanCount, spacingSize)

    /**
     * @param orientation 布局方向
     * @param spanCount 列/行数
     * @param spacingSize 间隔大小，单位 px
     */
    init {
        spacingDrawable = ColorDrawable(Color.TRANSPARENT)
    }

    fun setSpacingDrawable(spacingDrawable: Drawable?) {
        if (spacingDrawable == null) {
            return
        }
        this.spacingDrawable = spacingDrawable
    }

    fun setBorderWidth(width: Int) {
        borderLeftWidth = width
        borderRightWidth = width
        borderTopWidth = width
        borderBottomWidth = width
    }

    fun setBorderLeftWidth(borderLeftWidth: Int) {
        this.borderLeftWidth = borderLeftWidth
    }

    fun setBorderRightWidth(borderRightWidth: Int) {
        this.borderRightWidth = borderRightWidth
    }

    fun setBorderTopWidth(borderTopWidth: Int) {
        this.borderTopWidth = borderTopWidth
    }

    fun setBorderBottomWidth(borderBottomWidth: Int) {
        this.borderBottomWidth = borderBottomWidth
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildLayoutPosition(view)
        val rect = getItemOffsetRect(position, state.itemCount)
        outRect[rect[0], rect[1], rect[2]] = rect[3]
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val itemCount = parent.childCount
        var left: Int
        var right: Int
        var top: Int
        var bottom: Int
        for (i in 0 until itemCount) {
            val item = parent.getChildAt(i)
            val itemLayoutParams = item.layoutParams as RecyclerView.LayoutParams
            val itemOffsets = getItemOffsetRect(i, itemCount)
            // 控件左侧绘制
            right = item.left - itemLayoutParams.leftMargin
            top = item.top - itemLayoutParams.topMargin
            bottom = item.bottom + itemLayoutParams.bottomMargin
            left = itemOffsets[0]
            spacingDrawable.setBounds(left, top, right, bottom)
            spacingDrawable.draw(c)
            // 控件顶部绘制
            left = item.left - itemLayoutParams.leftMargin
            right = item.right + itemLayoutParams.rightMargin
            bottom = item.top + itemLayoutParams.topMargin
            top = bottom - itemOffsets[1]
            spacingDrawable.setBounds(left, top, right, bottom)
            spacingDrawable.draw(c)
            // 控件右侧绘制
            left = item.right + itemLayoutParams.rightMargin
            top = item.top - itemLayoutParams.topMargin
            right = left + itemOffsets[2]
            bottom = item.bottom + itemLayoutParams.bottomMargin
            spacingDrawable.setBounds(left, top, right, bottom)
            spacingDrawable.draw(c)
            // 控件底部绘制
            left = item.left - itemLayoutParams.leftMargin
            right = item.right + itemLayoutParams.rightMargin
            top = item.bottom + itemLayoutParams.bottomMargin
            bottom = top + itemOffsets[3]
            spacingDrawable.setBounds(left, top, right, bottom)
            spacingDrawable.draw(c)
        }
    }

    private fun getItemOffsetRect(position: Int, itemCount: Int): IntArray {
        return if (orientation == VERTICAL) {
            offsetVerticalOrientation(position, itemCount)
        } else {
            offsetHorizontalOrientation(position, itemCount)
        }
    }

    /**
     * 纵向边距计算
     */
    private fun offsetVerticalOrientation(position: Int, itemCount: Int): IntArray {
        var left: Int
        var top: Int
        var right: Int
        var bottom: Int
        val lastRowStartIndex = spanCount * (ceil(itemCount * 1.0 / spanCount) - 1).toInt()
        bottom = (spacingSize / 2f).toInt()
        top = bottom
        right = top
        left = right
        // 纵向第一列
        if (position % spanCount == 0) {
            left = if (borderLeftWidth >= 0) borderLeftWidth else spacingSize
        }
        // 纵向最后一列
        if ((position + 1) % spanCount == 0) {
            right = if (borderRightWidth >= 0) borderRightWidth else spacingSize
        }
        // 纵向第一行
        if (position < spanCount) {
            top = if (borderTopWidth >= 0) borderTopWidth else spacingSize
        }
        // 纵向最后一行
        if (position >= lastRowStartIndex) {
            bottom = if (borderBottomWidth >= 0) borderBottomWidth else spacingSize
        }
        return intArrayOf(left, top, right, bottom)
    }

    /**
     * 横向边距计算
     */
    private fun offsetHorizontalOrientation(position: Int, itemCount: Int): IntArray {
        var left: Int
        var top: Int
        var right: Int
        var bottom: Int
        val lastColumnStartIndex = spanCount * (ceil(itemCount * 1.0 / spanCount) - 1).toInt()
        bottom = (spacingSize / 2f).toInt()
        top = bottom
        right = top
        left = right
        // 横向第一行
        if (position % spanCount == 0) {
            top = if (borderTopWidth >= 0) borderTopWidth else spacingSize
        }
        // 横向最后一行
        if ((position + 1) % spanCount == 0) {
            bottom = if (borderBottomWidth >= 0) borderBottomWidth else spacingSize
        }
        // 横向第一列
        if (position < spanCount) {
            left = if (borderLeftWidth >= 0) borderLeftWidth else spacingSize
        } else if (position >= lastColumnStartIndex) {
            right = if (borderRightWidth >= 0) borderRightWidth else spacingSize
        }
        return intArrayOf(left, top, right, bottom)
    }
}