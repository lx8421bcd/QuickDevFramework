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

/**
 * Range for visible items.
 */
class ItemsRange @JvmOverloads constructor(
	/**
     * Gets number of  first item
     * @return the number of the first item
     */
    // First item number
	@JvmField val first: Int = 0,
	/**
     * Get items count
     * @return the count of items
     */
    // Items count
	@JvmField val count: Int = 0
) {
    val last: Int
        /**
         * Gets number of last item
         * @return the number of last item
         */
        get() = first + count - 1

    /**
     * Tests whether item is contained by range
     * @param index the item number
     * @return true if item is contained
     */
    operator fun contains(index: Int): Boolean {
        return index in first..last
    }
}