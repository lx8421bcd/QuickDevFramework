package com.linxiao.framework.common;

/**
 * 分页器，用于分页加载接口
 * <p>Page load counter using in paging load.</p>
 * <p>
 * Before using this class, you should confirm to backend engineer
 * whether the page index is starting from 0 or 1.
 * The default page index is starting from 1.
 * </p>
 *
 * @author linxiao
 * Create on 2018/5/4.
 * */
public class PageLoadCounter {
    private int pageSize = 10;
    private int pageIndex = 0;
    private boolean hasMorePage;
    private int itemCount = 0;
    
    public PageLoadCounter(int pageSize) {
        this.pageSize = pageSize;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    public int getPageIndex() {
        return pageIndex;
    }
    
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }
    
    public int nextPage() {
        return ++pageIndex;
    }
    
    public void reset() {
        itemCount = 0;
        pageIndex = 0;
    }
    
    public boolean checkHasMore(int pageCount) {
        this.itemCount += pageCount;
        hasMorePage = pageCount >= pageSize;
        return hasMorePage;
    }
    
    public boolean hasMore() {
        return hasMorePage;
    }
    
    public boolean isEmpty() {
        return pageIndex <= 1 && itemCount <= 0;
    }
}