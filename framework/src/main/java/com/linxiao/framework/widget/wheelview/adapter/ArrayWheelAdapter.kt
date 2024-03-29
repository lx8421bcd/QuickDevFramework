/*
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
package com.linxiao.framework.widget.wheelview.adapter

import android.content.Context

/**
 * The simple Array wheel adapter
 * @param <T> the element type
 */
class ArrayWheelAdapter<T>(
    context: Context,
    private val items: Array<T>
) : AbstractWheelTextAdapter(context) {

    public override fun getItemText(index: Int): CharSequence? {
        if (index >= 0 && index < items.size) {
            val item = items[index]
            return if (item is CharSequence) item else item.toString()
        }
        return null
    }

    override fun getItemsCount(): Int {
        return items.size
    }
}
