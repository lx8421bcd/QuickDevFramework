package com.linxiao.framework.manager;

/**
 *
 * Created by LinXiao on 2016-08-09.
 */
public abstract class BasePageLoadManager extends BaseManager {
    /** 总页数 */
    protected int totalPageNum = 0;
    /** 每页条数 */
    protected int pageSize = 0;
    /** 当前页 */
    protected int currPageIndex = 0;
    /** 总条数 */
    protected int totalItemNum = 0;

    public boolean hasMore() {
        return currPageIndex < totalPageNum;
    }

    public BasePageLoadManager() {
        setPageSize(configPageItemSize());
    }

    /**
     * 设置分页加载每页条目数量
     *
     * @return 每页条目数量
     * */
    protected abstract int configPageItemSize();

    public int getTotalItemNum() {
        return totalItemNum;
    }

    public int getCurrPageIndex() {
        return currPageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPageNum() {
        return totalPageNum;
    }

    protected void setTotalPageNum(int totalPageNum) {
        this.totalPageNum = totalPageNum;
    }

    protected void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    protected void setCurrPageIndex(int currPageIndex) {
        this.currPageIndex = currPageIndex;
    }

    protected void setTotalItemNum(int totalItemNum) {
        this.totalItemNum = totalItemNum;
    }
}
