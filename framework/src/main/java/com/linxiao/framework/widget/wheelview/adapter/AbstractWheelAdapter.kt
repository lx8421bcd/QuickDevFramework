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

import android.database.DataSetObserver
import android.view.View
import android.view.ViewGroup
import java.util.LinkedList

/**
 * Abstract Wheel adapter.
 */
abstract class AbstractWheelAdapter : WheelViewAdapter {

    // Observers
    private val datasetObservers: MutableList<DataSetObserver> = LinkedList()

    override fun getEmptyItem(convertView: View?, parent: ViewGroup?): View? {
        return null
    }

    override fun registerDataSetObserver(observer: DataSetObserver) {
        datasetObservers.add(observer)
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver) {
        datasetObservers.remove(observer)
    }

    /**
     * Notifies observers about data changing
     */
    protected fun notifyDataChangedEvent() {
        for (observer in datasetObservers) {
            observer.onChanged()
        }
    }

    /**
     * Notifies observers about invalidating data
     */
    protected fun notifyDataInvalidatedEvent() {
        for (observer in datasetObservers) {
            observer.onInvalidated()
        }
    }
}
