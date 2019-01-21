package com.linxiao.framework.list;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

/**
 * RecyclerView 滚动式分页加载监听器
 *
 * create by linxiao on 2018/1/22
 */
public abstract class ScrollPageLoader extends RecyclerView.OnScrollListener {

    // 触发滑动方向标志位
    private boolean movingToTrigger;

    private int offset;
    private boolean reverse = false;

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
     * @param reverse 是否反向分页，比如下拉分页
     */
    public ScrollPageLoader(boolean reverse) {
        this.reverse = reverse;
    }

    public ScrollPageLoader(int offset, boolean reverse) {
        this.offset = offset;
        this.reverse = reverse;
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
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        movingToTrigger = (!reverse && dy > 0) || (reverse && dy < 0);
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        // 如果未向触发方向滑动,不处理
        if (!movingToTrigger) {
            return;
        }
        boolean pageLoadEnabled = false;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            if (!reverse) {
                int pos = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();
                int lastPos = layoutManager.getItemCount() - 1;
                pageLoadEnabled = pos >= (lastPos - offset);
            }
            else {
                int pos = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                pageLoadEnabled = pos <= offset;
            }
        }
        else if (layoutManager instanceof StaggeredGridLayoutManager) {
            if (!reverse) {
                int[] lastPositions = ((StaggeredGridLayoutManager)layoutManager).findLastVisibleItemPositions(null);
                if (lastPositions != null) {
                    int lastVisiblePos = max(lastPositions);
                    int lastPos = layoutManager.getItemCount() - 1;
                    pageLoadEnabled = lastVisiblePos >= (lastPos - offset);
                }
            }
            else {
                int[] firstPositions = ((StaggeredGridLayoutManager)layoutManager).findFirstVisibleItemPositions(null);
                if (firstPositions != null) {
                    int firstVisiblePos = min(firstPositions);
                    pageLoadEnabled = firstVisiblePos <= offset;
                }
            }
        }
        // add more default judgement class here
        else {
            pageLoadEnabled = onCustomJudgePageLoadEnabled(layoutManager);
        }

        if (pageLoadEnabled && hasMoreData()) {
            onPageLoadEnabled();
        }
        movingToTrigger = false;
    }

    private int max(int [] arr) {
        if (arr == null || arr.length == 0) {
            return Integer.MIN_VALUE;
        }
        int ret = arr[0];
        for (int a : arr) {
            if (a > ret) {
                ret = a;
            }
        }
        return ret;
    }

    private int min(int [] arr) {
        if (arr == null || arr.length == 0) {
            return Integer.MIN_VALUE;
        }
        int ret = arr[0];
        for (int a : arr) {
            if (a < ret) {
                ret = a;
            }
        }
        return ret;
    }
}
