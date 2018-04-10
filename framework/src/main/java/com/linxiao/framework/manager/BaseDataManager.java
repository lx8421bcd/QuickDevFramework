package com.linxiao.framework.manager;

/**
 * base data manager class
 * Created by linxiao on 2016-11-24.
 */
public abstract class BaseDataManager {

    protected String TAG;

    public BaseDataManager() {
        TAG = this.getClass().getSimpleName();
    }
    
    /**
     * 分页器，用于分页加载接口
     * <p>Page load counter using in paging load.</p>
     * <p>
     * Before using this class, you should confirm to backend engineer
     * whether the page index is starting from 0 or 1.
     * The default page index is starting from 1.
     * </p>
     * */
    protected class PageLoadCounter {
        private int pageSize = 10;
        private int pageIndex = 0;
    
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
            return pageIndex + 1;
        }
        
        public void onPageLoaded() {
            pageIndex++;
        }
        
        public void reset() {
            pageIndex = 0;
        }
        
        public boolean hasMore(int pageCount) {
            return pageCount >= pageSize;
        }
    }
}
