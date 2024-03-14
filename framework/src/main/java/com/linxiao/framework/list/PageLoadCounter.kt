package com.linxiao.framework.list

/**
 * 分页器，用于分页加载接口
 *
 * Page load counter using in paging load.
 *
 *
 * Before using this class, you should confirm to backend engineer
 * whether the page index is starting from 0 or 1.
 * The default page index is starting from 1.
 *
 *
 * @author linxiao
 * @since 2018/5/4.
 */
class PageLoadCounter(pageSize: Int) {

    var pageSize = 10
    var pageIndex = 0
    private var hasMorePage = false
    private var itemCount = 0

    init {
        this.pageSize = pageSize
    }

    fun nextPage(): Int {
        return ++pageIndex
    }

    fun reset() {
        itemCount = 0
        pageIndex = 0
    }

    fun checkHasMore(pageCount: Int): Boolean {
        itemCount += pageCount
        hasMorePage = pageCount >= pageSize
        return hasMorePage
    }

    fun hasMore(): Boolean {
        return hasMorePage
    }

    val isEmpty: Boolean
        get() = pageIndex <= 1 && itemCount <= 0
}