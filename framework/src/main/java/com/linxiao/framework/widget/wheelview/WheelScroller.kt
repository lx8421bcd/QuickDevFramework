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

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.animation.Interpolator
import android.widget.Scroller
import kotlin.math.abs

/**
 * Scroller class handles scrolling events and updates the
 */
class WheelScroller(
    val context: Context,
    val listener: ScrollingListener
) {
    /**
     * Scrolling listener interface
     */
    interface ScrollingListener {
        /**
         * Scrolling callback called when scrolling is performed.
         * @param distance the distance to scroll
         */
        fun onScroll(distance: Int)

        /**
         * Starting callback called when scrolling is started
         */
        fun onStarted()

        /**
         * Finishing callback called after justifying
         */
        fun onFinished()

        /**
         * Justifying callback called to justify a view when scrolling is ended
         */
        fun onJustify()
    }

    companion object {
        /** Scrolling duration  */
        private const val SCROLLING_DURATION = 400

        /** Minimum delta for scrolling  */
        const val MIN_DELTA_FOR_SCROLLING = 1
    }

    // Messages
    private val MESSAGE_SCROLL = 0
    private val MESSAGE_JUSTIFY = 1
    // gesture listener
    private val gestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            // Do scrolling in onTouchEvent() since onScroll() are not call immediately
            //  when user touch and move the wheel
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            lastScrollY = 0
            val maxY = 0x7FFFFFFF
            val minY = -maxY
            scroller.fling(0, lastScrollY, 0, -velocityY.toInt(), 0, 0, minY, maxY)
            setNextMessage(MESSAGE_SCROLL)
            return true
        }
    }
    // Scrolling
    private val gestureDetector: GestureDetector = GestureDetector(context, gestureListener)
    private var scroller: Scroller = Scroller(context)
    private var lastScrollY = 0
    private var lastTouchedY = 0f
    private var isScrollingPerformed = false

    init {
        gestureDetector.setIsLongpressEnabled(false)
    }

    /**
     * Set the the specified scrolling interpolator
     * @param interpolator the interpolator
     */
    fun setInterpolator(interpolator: Interpolator?) {
        scroller.forceFinished(true)
        scroller = Scroller(context, interpolator)
    }

    /**
     * Scroll the wheel
     * @param distance the scrolling distance
     * @param time the scrolling duration
     */
    fun scroll(distance: Int, time: Int) {
        scroller.forceFinished(true)
        lastScrollY = 0
        scroller.startScroll(0, 0, 0, distance, if (time != 0) time else SCROLLING_DURATION)
        setNextMessage(MESSAGE_SCROLL)
        startScrolling()
    }

    /**
     * Stops scrolling
     */
    fun stopScrolling() {
        scroller.forceFinished(true)
    }

    /**
     * Handles Touch event
     * @param event the motion event
     * @return
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchedY = event.y
                scroller.forceFinished(true)
                clearMessages()
            }

            MotionEvent.ACTION_MOVE -> {
                // perform scrolling
                val distanceY = (event.y - lastTouchedY).toInt()
                if (distanceY != 0) {
                    startScrolling()
                    listener.onScroll(distanceY)
                    lastTouchedY = event.y
                }
            }
        }
        if (!gestureDetector.onTouchEvent(event) && event.action == MotionEvent.ACTION_UP) {
            justify()
        }
        return true
    }

    /**
     * Set next message to queue. Clears queue before.
     *
     * @param message the message to set
     */
    private fun setNextMessage(message: Int) {
        clearMessages()
        animationHandler.sendEmptyMessage(message)
    }

    /**
     * Clears messages from queue
     */
    private fun clearMessages() {
        animationHandler.removeMessages(MESSAGE_SCROLL)
        animationHandler.removeMessages(MESSAGE_JUSTIFY)
    }

    @SuppressLint("HandlerLeak")
    private val animationHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            scroller.computeScrollOffset()
            var currY = scroller.currY
            val delta = lastScrollY - currY
            lastScrollY = currY
            if (delta != 0) {
                listener.onScroll(delta)
            }

            // scrolling is not finished when it comes to final Y
            // so, finish it manually 
            if (abs((currY - scroller.finalY).toDouble()) < MIN_DELTA_FOR_SCROLLING) {
                currY = scroller.finalY
                scroller.forceFinished(true)
            }
            if (!scroller.isFinished) {
                sendEmptyMessage(msg.what)
            } else if (msg.what == MESSAGE_SCROLL) {
                justify()
            } else {
                finishScrolling()
            }
        }
    }

    /**
     * Justifies wheel
     */
    private fun justify() {
        listener.onJustify()
        setNextMessage(MESSAGE_JUSTIFY)
    }

    /**
     * Starts scrolling
     */
    private fun startScrolling() {
        if (!isScrollingPerformed) {
            isScrollingPerformed = true
            listener.onStarted()
        }
    }

    /**
     * Finishes scrolling
     */
    fun finishScrolling() {
        if (isScrollingPerformed) {
            listener.onFinished()
            isScrollingPerformed = false
        }
    }
}
