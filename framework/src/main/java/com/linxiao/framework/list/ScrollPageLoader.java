package com.linxiao.framework.list;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * RecyclerView 滚动式分页加载监听器
 *
 * create by linxiao on 2018/1/22
 */
public abstract class ScrollPageLoader extends RecyclerView.OnScrollListener {

    private boolean isSlidingToLast;
    private int offset;

    public ScrollPageLoader() {
        offset = 1;
    }

    /**
     *
     * @param offset 偏移量，即倒数第offset个item可见时触发分页加载
     */
    public ScrollPageLoader(int offset) {
        this.offset = offset;
    }

    /**
     * 是否有更多的分页数据
     * <p>用于告诉分页加载器是否有更多的数据，如果此方法返回false则{@link #onPageLoadEnabled()}不再会触发.
     * 可以将接口返回数据是否有更多分页的判断置于此处</p>
     *
     * */
    public abstract boolean hasMoreData();

    /**
     * 分页加载触发方法
     * <p>此方法触发时可以执行分页加载</p>
     * */
    public abstract void onPageLoadEnabled();

    /**
     * 自定义判断是否执行分页
     * <p>用于判断自定义LayoutManager是否滑动到可触发分页的阈值</p>
     * */
    public boolean onCustomJudgePageLoadEnabled(RecyclerView.LayoutManager layoutManager) {
        return false;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        isSlidingToLast = dy > 0;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        // 只检测下划
        if (!isSlidingToLast) {
            return;
        }
        boolean pageLoadEnabled = false;
        if (!hasMoreData()) {
            return;
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int pos = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();
            int totalItemCount = layoutManager.getItemCount();
            pageLoadEnabled = pos >= (totalItemCount - offset);
        }
        else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastPositions = ((StaggeredGridLayoutManager)layoutManager).findLastVisibleItemPositions(null);
            int lastVisiblePos = 0;
            if (lastPositions == null) {
                return;
            }
            for (int pos : lastPositions) {
                if (pos > lastVisiblePos) {
                    lastVisiblePos = pos;
                }
            }
            int totalItemCount = layoutManager.getItemCount();
            pageLoadEnabled = lastVisiblePos >= (totalItemCount - offset);
        }
        // add more default judgement class here

        else {
            pageLoadEnabled = onCustomJudgePageLoadEnabled(layoutManager);
        }

        if (pageLoadEnabled) {
            onPageLoadEnabled();
        }
    }
}
