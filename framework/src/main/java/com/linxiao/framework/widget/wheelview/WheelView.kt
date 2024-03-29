/*
 *  Android Wheel Control.
 *  https://code.google.com/p/android-wheel/
 *  
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.linxiao.framework.widget.wheelview

import android.content.Context
import android.database.DataSetObserver
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.LinearLayout
import com.linxiao.framework.widget.wheelview.WheelScroller.ScrollingListener
import com.linxiao.framework.widget.wheelview.adapter.WheelViewAdapter
import java.util.LinkedList
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.max
import kotlin.math.min

class WheelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        /** Top and bottom shadows colors  */
        private val SHADOWS_COLORS = intArrayOf(
            -0xeeeeef,
            0x00AAAAAA,
            0x00AAAAAA
        )
        /** Top and bottom items offset (to hide that)  */
        private const val ITEM_OFFSET_PERCENT = 10
        /** Left and right padding value  */
        private const val PADDING = 0
        /** Default count of visible items  */
        private const val DEF_VISIBLE_ITEMS = 1
    }
    /**
     * Count of visible items
     */
	@JvmField
	var visibleItems = DEF_VISIBLE_ITEMS
    
    private var currentItem = 0
    private var itemHeight = 0
        get() {
            if (field != 0) {
                return field
            }
            itemsLayout.getChildAt(0)?.apply {
                itemHeight = this.height
                return field
            }
            return height / visibleItems
        }
    private var showStroke = false
    private var strokeColor = Color.GRAY
    private var strokeWidth = 1 // 1px
    private var strokePaint: Paint? = null
    // Shadows drawables
    private val topShadow: GradientDrawable? = null
    private val bottomShadow: GradientDrawable? = null
    // Scrolling listener
    private var scrollingListener: ScrollingListener = object : ScrollingListener {

        override fun onStarted() {
            isScrollingPerformed = true
            notifyScrollingListenersAboutStart()
        }

        override fun onScroll(distance: Int) {
            doScroll(distance)
            val height = height
            if (scrollingOffset > height) {
                scrollingOffset = height
                scroller.stopScrolling()
            } else if (scrollingOffset < -height) {
                scrollingOffset = -height
                scroller.stopScrolling()
            }
        }

        override fun onFinished() {
            if (isScrollingPerformed) {
                notifyScrollingListenersAboutEnd()
                isScrollingPerformed = false
            }
            scrollingOffset = 0
            invalidate()
        }

        override fun onJustify() {
            if (abs(scrollingOffset.toDouble()) > WheelScroller.MIN_DELTA_FOR_SCROLLING) {
                scroller.scroll(scrollingOffset, 0)
            }
        }
    }
    private val itemsRange: ItemsRange?
        get() {
            if (itemHeight == 0) {
                return null
            }
            var first = currentItem
            var count = 1
            while (count * itemHeight < height) {
                first--
                count += 2 // top + bottom items
            }
            if (scrollingOffset != 0) {
                if (scrollingOffset > 0) {
                    first--
                }
                count++
                // process empty items above the first or below the second
                val emptyItems = scrollingOffset / itemHeight
                first -= emptyItems
                count += asin(emptyItems.toDouble()).toInt()
            }
            return ItemsRange(first, count)
        }
    private var scroller = WheelScroller(getContext(), scrollingListener)
    private var isScrollingPerformed = false
    private var scrollingOffset = 0
    // Cyclic
    var cyclic = false
        set(value) {
            field = value
            invalidateWheel(false)
        }
    // Items layout
    private val itemsLayout by lazy {
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        return@lazy layout
    }
    // The number of first item in layout
    private var firstItem = 0
    // View adapter
    var viewAdapter: WheelViewAdapter? = null
        set(value) {
            field?.unregisterDataSetObserver(dataObserver)
            field = value
            field?.registerDataSetObserver(dataObserver)
            invalidateWheel(true)
        }
    private val dataObserver: DataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            invalidateWheel(false)
        }

        override fun onInvalidated() {
            invalidateWheel(true)
        }
    }
    // Recycle
    private val recycle = WheelRecycle(this)
    // Listeners
    private val changingListeners: MutableList<OnWheelChangedListener> = LinkedList()
    private val scrollingListeners: MutableList<OnWheelScrollListener> = LinkedList()
    private val clickingListeners: MutableList<OnWheelClickedListener> = LinkedList()
    
    /**
     * Set the the specified scrolling interpolator
     * @param interpolator the interpolator
     */
    fun setInterpolator(interpolator: Interpolator?) {
        scroller.setInterpolator(interpolator)
    }

    /**
     * Adds wheel changing listener
     * @param listener the listener
     */
    fun addChangingListener(listener: OnWheelChangedListener) {
        changingListeners.add(listener)
    }

    /**
     * Removes wheel changing listener
     * @param listener the listener
     */
    fun removeChangingListener(listener: OnWheelChangedListener) {
        changingListeners.remove(listener)
    }

    /**
     * Notifies changing listeners
     * @param oldValue the old wheel value
     * @param newValue the new wheel value
     */
    protected fun notifyChangingListeners(oldValue: Int, newValue: Int) {
        for (listener in changingListeners) {
            listener.onChanged(this, oldValue, newValue)
        }
    }

    /**
     * Adds wheel scrolling listener
     * @param listener the listener
     */
    fun addScrollingListener(listener: OnWheelScrollListener) {
        scrollingListeners.add(listener)
    }

    /**
     * Removes wheel scrolling listener
     * @param listener the listener
     */
    fun removeScrollingListener(listener: OnWheelScrollListener) {
        scrollingListeners.remove(listener)
    }

    /**
     * Notifies listeners about starting scrolling
     */
    protected fun notifyScrollingListenersAboutStart() {
        for (listener in scrollingListeners) {
            listener.onScrollingStarted(this)
        }
    }

    /**
     * Notifies listeners about ending scrolling
     */
    protected fun notifyScrollingListenersAboutEnd() {
        for (listener in scrollingListeners) {
            listener.onScrollingFinished(this)
        }
    }

    /**
     * Adds wheel clicking listener
     * @param listener the listener
     */
    fun addClickingListener(listener: OnWheelClickedListener) {
        clickingListeners.add(listener)
    }

    /**
     * Removes wheel clicking listener
     * @param listener the listener
     */
    fun removeClickingListener(listener: OnWheelClickedListener) {
        clickingListeners.remove(listener)
    }

    /**
     * Notifies listeners about clicking
     */
    protected fun notifyClickListenersAboutClick(item: Int) {
        for (listener in clickingListeners) {
            listener.onItemClicked(this, item)
        }
    }

    /**
     * Gets current value
     *
     * @return the current value
     */
    fun getCurrentItem(): Int {
        return currentItem
    }

    /**
     * Sets the current item. Does nothing when index is wrong.
     *
     * @param index the item index
     * @param animated the animation flag
     */
    @JvmOverloads
    fun setCurrentItem(index: Int, animated: Boolean = false) {
        var viewIndex = index
        viewAdapter?.apply {
            val itemCount = getItemsCount()
            if (itemCount == 0) {
                return
            }
            if (viewIndex < 0 || viewIndex >= itemCount) {
                if (cyclic) {
                    while (viewIndex < 0) {
                        viewIndex += itemCount
                    }
                    viewIndex %= itemCount
                } else {
                    return  // throw?
                }
            }
            if (viewIndex != currentItem) {
                if (animated) {
                    var itemsToScroll = viewIndex - currentItem
                    if (cyclic) {
                        val scroll = (itemCount + min(
                            viewIndex.toDouble(),
                            currentItem.toDouble()
                        ) - max(viewIndex.toDouble(), currentItem.toDouble())).toInt()
                        if (scroll < abs(itemsToScroll.toDouble())) {
                            itemsToScroll = if (itemsToScroll < 0) scroll else -scroll
                        }
                    }
                    scroll(itemsToScroll, 0)
                } else {
                    scrollingOffset = 0
                    val old = currentItem
                    currentItem = viewIndex
                    notifyChangingListeners(old, currentItem)
                    invalidate()
                }
            }
        }
    }

    /**
     * Invalidates wheel
     * @param clearCaches if true then cached views will be clear
     */
    fun invalidateWheel(clearCaches: Boolean) {
        if (clearCaches) {
            recycle.clearAll()
            itemsLayout.removeAllViews()
            scrollingOffset = 0
        } 
        else {
            recycle.recycleItems(itemsLayout, firstItem, ItemsRange())
        }
        invalidate()
    }

    /**
     * Calculates desired height for layout
     *
     * @param layout
     * the source layout
     * @return the desired layout height
     */
    private fun getDesiredHeight(layout: LinearLayout?): Int {
        if (layout?.getChildAt(0) != null) {
            itemHeight = layout.getChildAt(0).measuredHeight
        }
        val desired = itemHeight * visibleItems - itemHeight * ITEM_OFFSET_PERCENT / 50
        return max(desired.toDouble(), suggestedMinimumHeight.toDouble()).toInt()
    }

    /**
     * Calculates control width and creates text layouts
     * @param widthSize the input layout width
     * @param mode the layout mode
     * @return the calculated control width
     */
    private fun calculateLayoutWidth(widthSize: Int, mode: Int): Int {
        itemsLayout.setLayoutParams(
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        itemsLayout.measure(
            MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        var width = itemsLayout.measuredWidth
        if (mode == MeasureSpec.EXACTLY) {
            width = widthSize
        } else {
            width += 2 * PADDING
            // Check against our minimum width
            width = max(width.toDouble(), suggestedMinimumWidth.toDouble()).toInt()
            if (mode == MeasureSpec.AT_MOST && widthSize < width) {
                width = widthSize
            }
        }
        itemsLayout.measure(
            MeasureSpec.makeMeasureSpec(width - 2 * PADDING, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        return width
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        buildViewForMeasuring()
        val width = calculateLayoutWidth(widthSize, widthMode)
        var height: Int
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize
        } else {
            height = getDesiredHeight(itemsLayout)
            if (heightMode == MeasureSpec.AT_MOST) {
                height = min(height.toDouble(), heightSize.toDouble()).toInt()
            }
        }
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        layout(r - l, b - t)
    }

    /**
     * Sets layouts width and height
     * @param width the layout width
     * @param height the layout height
     */
    private fun layout(width: Int, height: Int) {
        val itemsWidth = width - 2 * PADDING
        itemsLayout.layout(0, 0, itemsWidth, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (viewAdapter != null && viewAdapter!!.getItemsCount() > 0) {
            updateView()
            drawItems(canvas)
            drawCenterRect(canvas)
        }
        drawShadows(canvas)
    }

    /**
     * Draws shadows on top and bottom of control
     * @param canvas the canvas for drawing
     */
    private fun drawShadows(canvas: Canvas) {
        val height = (1.5 * itemHeight).toInt()
//		topShadow.setBounds(0, 0, getWidth(), height);
//		topShadow.draw(canvas);
//
//		bottomShadow.setBounds(0, getHeight() - height, getWidth(), getHeight());
//		bottomShadow.draw(canvas);
    }

    /**
     * Draws items
     * @param canvas the canvas for drawing
     */
    private fun drawItems(canvas: Canvas) {
        canvas.save()
        val top = (currentItem - firstItem) * itemHeight + (itemHeight - height) / 2
        canvas.translate(PADDING.toFloat(), (-top + scrollingOffset).toFloat())
        itemsLayout.draw(canvas)
        canvas.restore()
    }

    fun setSelectStroke(strokeColor: Int, strokeWidth: Int) {
        this.strokeColor = strokeColor
        this.strokeWidth = strokeWidth
    }

    fun showSelectStroke(show: Boolean) {
        showStroke = show
    }

    /**
     * Draws rect for current value
     * @param canvas the canvas for drawing
     */
    private fun drawCenterRect(canvas: Canvas) {
        if (!showStroke) {
            if (strokePaint == null) {
                return
            }
            strokePaint!!.clearShadowLayer()
            return
        }
        val center = height / 2
        val offset = (itemHeight / 2 * 1.2).toInt()
        if (strokePaint == null) {
            strokePaint = Paint()
        }
        strokePaint!!.setColor(strokeColor)
        // 设置线宽
        strokePaint!!.strokeWidth = strokeWidth.toFloat()
        // 绘制上边直线
        canvas.drawLine(
            0f,
            (center - offset).toFloat(),
            width.toFloat(),
            (center - offset).toFloat(),
            strokePaint!!
        )
        // 绘制下边直线
        canvas.drawLine(
            0f,
            (center + offset).toFloat(),
            width.toFloat(),
            (center + offset).toFloat(),
            strokePaint!!
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled || viewAdapter == null) {
            return true
        }
        when (event.action) {
            MotionEvent.ACTION_MOVE -> if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_UP -> if (!isScrollingPerformed) {
                var distance = event.y.toInt() - height / 2
                if (distance > 0) {
                    distance += itemHeight / 2
                } else {
                    distance -= itemHeight / 2
                }
                val items = distance / itemHeight
                if (items != 0 && isValidItemIndex(currentItem + items)) {
                    notifyClickListenersAboutClick(currentItem + items)
                }
            }
        }
        return scroller.onTouchEvent(event)
    }

    /**
     * Scrolls the wheel
     * @param delta the scrolling value
     */
    private fun doScroll(delta: Int) {
        scrollingOffset += delta
        val itemHeight = itemHeight
        var count = scrollingOffset / itemHeight
        var pos = currentItem - count
        val itemCount = viewAdapter!!.getItemsCount()
        var fixPos = scrollingOffset % itemHeight
        if (abs(fixPos.toDouble()) <= itemHeight / 2) {
            fixPos = 0
        }
        if (cyclic && itemCount > 0) {
            if (fixPos > 0) {
                pos--
                count++
            } else if (fixPos < 0) {
                pos++
                count--
            }
            // fix position by rotating
            while (pos < 0) {
                pos += itemCount
            }
            pos %= itemCount
        } else {
            // 
            if (pos < 0) {
                count = currentItem
                pos = 0
            } else if (pos >= itemCount) {
                count = currentItem - itemCount + 1
                pos = itemCount - 1
            } else if (pos > 0 && fixPos > 0) {
                pos--
                count++
            } else if (pos < itemCount - 1 && fixPos < 0) {
                pos++
                count--
            }
        }
        val offset = scrollingOffset
        if (pos != currentItem) {
            setCurrentItem(pos, false)
        } else {
            invalidate()
        }

        // update offset
        scrollingOffset = offset - count * itemHeight
        if (scrollingOffset > height) {
            scrollingOffset = scrollingOffset % height + height
        }
    }

    /**
     * Scroll the wheel
     * @param itemsToScroll items to scroll
     * @param time scrolling duration
     */
    fun scroll(itemsToScroll: Int, time: Int) {
        val distance = itemsToScroll * itemHeight - scrollingOffset
        scroller.scroll(distance, time)
    }

    /**
     * Rebuilds wheel items if necessary. Caches all unused items.
     *
     * @return true if items are rebuilt
     */
    private fun rebuildItems(): Boolean {
        var updated: Boolean
        val range = itemsRange
        var first = recycle.recycleItems(itemsLayout, firstItem, range!!)
        updated = firstItem != first
        firstItem = first
        if (!updated) {
            updated = firstItem != range.first || itemsLayout.childCount != range.count
        }
        if (firstItem > range.first && firstItem <= range.last) {
            for (i in firstItem - 1 downTo range.first) {
                if (!addViewItem(i, true)) {
                    break
                }
                firstItem = i
            }
        } else {
            firstItem = range.first
        }
        first = firstItem
        for (i in itemsLayout.childCount until range.count) {
            if (!addViewItem(firstItem + i, false) && itemsLayout.childCount == 0) {
                first++
            }
        }
        firstItem = first
        return updated
    }

    /**
     * Updates view. Rebuilds items and label if necessary, recalculate items sizes.
     */
    private fun updateView() {
        if (rebuildItems()) {
            calculateLayoutWidth(width, MeasureSpec.EXACTLY)
            layout(width, height)
        }
    }

    /**
     * Builds view for measuring
     */
    private fun buildViewForMeasuring() {
        // clear all items
        recycle.recycleItems(itemsLayout, firstItem, ItemsRange())
        // add views
        val addItems = visibleItems / 2
        for (i in currentItem + addItems downTo currentItem - addItems) {
            if (addViewItem(i, true)) {
                firstItem = i
            }
        }
    }

    /**
     * Adds view for item to items layout
     * @param index the item index
     * @param first the flag indicates if view should be first
     * @return true if corresponding item exists and is added
     */
    private fun addViewItem(index: Int, first: Boolean): Boolean {
        val view = getItemView(index)
        if (view != null) {
            if (first) {
                itemsLayout.addView(view, 0)
            } else {
                itemsLayout.addView(view)
            }
            return true
        }
        return false
    }

    /**
     * Checks whether intem index is valid
     * @param index the item index
     * @return true if item index is not out of bounds or the wheel is cyclic
     */
    private fun isValidItemIndex(index: Int): Boolean {
        return viewAdapter != null && viewAdapter!!.getItemsCount() > 0 &&
                (cyclic || index >= 0 && index < viewAdapter!!.getItemsCount())
    }

    /**
     * Returns view for specified item
     * @param index the item index
     * @return item view or empty view if index is out of bounds
     */
    private fun getItemView(index: Int): View? {
        var viewIndex = index
        viewAdapter?.apply {
            val count = getItemsCount()
            if (!isValidItemIndex(viewIndex)) {
                return getEmptyItem(recycle.emptyItem, itemsLayout)
            } else {
                while (viewIndex < 0) {
                    viewIndex += count
                }
            }
            viewIndex %= count
            return getItem(viewIndex, recycle.item, itemsLayout)
        }
        return null
    }

    /**
     * Stops scrolling
     */
    fun stopScrolling() {
        scroller.stopScrolling()
    }
}
