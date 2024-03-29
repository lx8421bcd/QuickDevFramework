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

import android.view.View
import android.widget.LinearLayout
import java.util.LinkedList

/**
 * Recycle stores wheel items to reuse.
 */
class WheelRecycle(
    private val wheel: WheelView
) {
    // Cached items
    private var items: MutableList<View>? = null

    // Cached empty items
    private var emptyItems: MutableList<View>? = null

    /**
     * Recycles items from specified layout.
     * There are saved only items not included to specified range.
     * All the cached items are removed from original layout.
     *
     * @param layout the layout containing items to be cached
     * @param firstItem the number of first item in layout
     * @param range the range of current wheel items
     * @return the new value of first item number
     */
    fun recycleItems(layout: LinearLayout, firstItem: Int, range: ItemsRange): Int {
        var item = firstItem
        var index = item
        var i = 0
        while (i < layout.childCount) {
            if (!range.contains(index)) {
                recycleView(layout.getChildAt(i), index)
                layout.removeViewAt(i)
                if (i == 0) { // first item
                    item++
                }
            } else {
                i++ // go to next item
            }
            index++
        }
        return item
    }

    val item: View?
        /**
         * Gets item view
         * @return the cached view
         */
        get() = getCachedView(items)
    val emptyItem: View?
        /**
         * Gets empty item view
         * @return the cached empty view
         */
        get() = getCachedView(emptyItems)

    /**
     * Clears all views
     */
    fun clearAll() {
        items?.clear()
        emptyItems?.clear()
    }

    /**
     * Adds view to specified cache. Creates a cache list if it is null.
     * @param view the view to be cached
     * @param cache the cache list
     * @return the cache list
     */
    private fun addView(view: View, cache: MutableList<View>?): MutableList<View> {
        val ret = cache ?: LinkedList()
        ret.add(view)
        return ret
    }

    /**
     * Adds view to cache. Determines view type (item view or empty one) by index.
     * @param view the view to be cached
     * @param index the index of view
     */
    private fun recycleView(view: View, index: Int) {
        var viewIndex = index
        val count = wheel.viewAdapter?.getItemsCount() ?: 0
        if ((viewIndex < 0 || viewIndex >= count) && !wheel.cyclic) {
            // empty view
            emptyItems = addView(view, emptyItems)
        } else {
            while (viewIndex < 0) {
                viewIndex += count
            }
            items = addView(view, items)
        }
    }

    /**
     * Gets view from specified cache.
     * @param cache the cache
     * @return the first view from cache.
     */
    private fun getCachedView(cache: MutableList<View>?): View? {
        if (!cache.isNullOrEmpty()) {
            return cache.removeAt(0)
        }
        return null
    }
}
