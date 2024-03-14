package com.linxiao.framework.list

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * RecyclerView 滚动式分页加载监听器
 *
 * create by linxiao on 2018/1/22
 */
abstract class ScrollPageLoader : RecyclerView.OnScrollListener {

    // 触发滑动方向标志位
    private var movingToTrigger = false
    private var offset = 0
    private var reverse = false

    constructor() {
        offset = 1
    }

    /**
     *
     * @param offset 偏移量，即倒数第offset个item可见时触发分页加载
     */
    constructor(offset: Int) {
        this.offset = offset
    }

    /**
     * @param reverse 是否反向分页，比如下拉分页
     */
    constructor(reverse: Boolean) {
        this.reverse = reverse
    }

    constructor(offset: Int, reverse: Boolean) {
        this.offset = offset
        this.reverse = reverse
    }

    /**
     * 是否有更多的分页数据
     *
     * 用于告诉分页加载器是否有更多的数据，如果此方法返回false则[.onPageLoadEnabled]不再会触发.
     * 可以将接口返回数据是否有更多分页的判断置于此处
     *
     */
    abstract fun hasMoreData(): Boolean

    /**
     * 分页加载触发方法
     *
     * 此方法触发时可以执行分页加载
     */
    abstract fun onPageLoadEnabled()

    /**
     * 判断是否执行分页
     *
     * 用于判断LayoutManager是否滑动到可触发分页的阈值，如有自定义LayoutManager需充血次判断方法
     */
    open fun onJudgePageLoadEnabled(layoutManager: RecyclerView.LayoutManager?): Boolean {
        if (layoutManager is LinearLayoutManager) {
            if (reverse) {
                val pos = layoutManager.findFirstVisibleItemPosition()
                return pos <= offset
            }
            else {
                val pos = layoutManager.findLastVisibleItemPosition()
                val lastPos = layoutManager.getItemCount() - 1
                return pos >= lastPos - offset
            }
        }
        if (layoutManager is StaggeredGridLayoutManager) {
            if (reverse) {
                val firstPositions = layoutManager.findFirstVisibleItemPositions(null)
                if (firstPositions != null) {
                    val firstVisiblePos = min(firstPositions)
                    return firstVisiblePos <= offset
                }
            }
            else {
                val lastPositions = layoutManager.findLastVisibleItemPositions(null)
                if (lastPositions != null) {
                    val lastVisiblePos = max(lastPositions)
                    val lastPos = layoutManager.getItemCount() - 1
                    return lastVisiblePos >= lastPos - offset
                }
            }
        }
        return false
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        movingToTrigger = !reverse && dy > 0 || reverse && dy < 0
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        // 如果未向触发方向滑动,不处理
        if (!movingToTrigger) {
            return
        }
        val pageLoadEnabled = onJudgePageLoadEnabled(recyclerView.layoutManager)
        if (pageLoadEnabled && hasMoreData()) {
            onPageLoadEnabled()
        }
        movingToTrigger = false
    }

    private fun max(arr: IntArray?): Int {
        if (arr == null || arr.isEmpty()) {
            return Int.MIN_VALUE
        }
        var ret = arr[0]
        for (a in arr) {
            if (a > ret) {
                ret = a
            }
        }
        return ret
    }

    private fun min(arr: IntArray?): Int {
        if (arr == null || arr.isEmpty()) {
            return Int.MIN_VALUE
        }
        var ret = arr[0]
        for (a in arr) {
            if (a < ret) {
                ret = a
            }
        }
        return ret
    }
}
