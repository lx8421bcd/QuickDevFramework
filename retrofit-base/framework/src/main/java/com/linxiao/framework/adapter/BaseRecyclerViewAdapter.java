package com.linxiao.framework.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView 基类
 * TODO：添加Header和Footer支持， Header、Footer控制
 * TODO：复杂数据基类，混合数据基类
 * Created by linxiao on 2017/1/4.
 */
public abstract class BaseRecyclerViewAdapter<T, VH extends BaseRecyclerViewHolder> extends RecyclerView.Adapter<VH> {

    /**
     * item click listener in RecyclerView
     * */
    public interface OnItemClickListener {

        void onItemClick(BaseRecyclerViewAdapter adapter, View itemView, int position);
    }

    /**
     * item long click Listener in RecyclerView
     * */
    public interface OnItemLongClickListener {

        void onItemLongClick(BaseRecyclerViewAdapter adapter, View itemView, int position);
    }

    /**
     * used for default page load
     * */
    public interface LoadMoreListener {

        void onLoadMore();
    }

    private static final int HEADER_VIEW = 101;
    private static final int EMPTY_VIEW = 102;
    private static final int FOOTER_VIEW = 103;
    private static final int LOADING_VIEW = 104;

    private static String TAG;

    private List<T> mDataSource;
    private Context mContext;
    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;
    private View mLoadingView;

    private boolean useEmptyView = false;
    private boolean useHeaderView = false;
    private boolean useFooterView = false;
    private boolean useLoadingView = false;

    public BaseRecyclerViewAdapter(Context context) {
        TAG = this.getClass().getSimpleName();
        mContext = context;
        mDataSource = new ArrayList<>();
    }

    /**
     * 向Holder设置数据
     * */
    protected abstract void setData(VH holder, T data);

    /**
     *
     * */
    protected abstract VH onCreateDataViewHolder(ViewGroup parent, int viewType);

    /**
     * 创建不使用数据的ViewHolder
     * <p>主要用于承载Adapter的Header、Footer、EmptyView等默认View</p>
     * */
    @SuppressWarnings("unchecked")
    public VH createEmptyViewHolder(View itemView) {
        return (VH) new BaseRecyclerViewHolder(itemView);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
        case HEADER_VIEW :
            createEmptyViewHolder(mHeaderView);
            break;
        case EMPTY_VIEW :
            createEmptyViewHolder(mEmptyView);
            break;
        case FOOTER_VIEW:
            createEmptyViewHolder(mFooterView);
            break;
        default:
            break;
        }
        return onCreateDataViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        int realPosition = mHeaderView != null ? position - 1 : position;
        int itemViewType = holder.getItemViewType();
        switch (itemViewType) {
        case HEADER_VIEW:
            break;
        case FOOTER_VIEW:
            break;
        case EMPTY_VIEW:
            break;
        default:
            setData(holder, mDataSource.get(realPosition));
            break;
        }
    }
    @Override
    public int getItemCount() {
        int count = mDataSource.size();
        if (mHeaderView != null) {
            count++;
        }
        if (hasEmptyView()) {
            count++;
        }
        if (mFooterView != null) {
            count++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        boolean hasHeaderView = hasHeaderView();
        if (hasEmptyView()) {
            switch (position) {
            case 0 :
                return hasHeaderView ? HEADER_VIEW : EMPTY_VIEW;
            case 1 :
                return hasHeaderView ? EMPTY_VIEW : FOOTER_VIEW;
            case 2 :
                return FOOTER_VIEW;
            default :
                return super.getItemViewType(position);
            }
        } else {
            int realPosition = hasHeaderView ? position - 1 : position;
            if (realPosition < 0) {
                return HEADER_VIEW;
            } else if (mDataSource.size() > 0) {
                return realPosition < mDataSource.size() ? super.getItemViewType(position) : FOOTER_VIEW;
            } else {
                return (hasLoadingView() && realPosition == 0) ? LOADING_VIEW : FOOTER_VIEW;
            }
        }
    }

    private boolean hasEmptyView() {
        return useEmptyView && mEmptyView != null && mDataSource.size() == 0;
    }

    private boolean hasHeaderView() {
        return useHeaderView && mHeaderView != null;
    }

    private boolean hasLoadingView() {
        return useLoadingView && mLoadingView != null;
    }

    private boolean hasFooterView() {
        return useFooterView && mFooterView != null;
    }

    public void setHeaderView(@NonNull View v) {
        mHeaderView = v;
        useHeaderView = true;
        notifyItemInserted(0);
    }

    public void setLoadingView(@NonNull View v) {
        mLoadingView = v;
    }

    public void setFooterView(@NonNull View v) {
        mFooterView = v;
        useFooterView = true;
    }

    /**
     * 设置没有数据时显示的View
     * */
    public void setEmptyView(@NonNull View v) {
        mEmptyView = v;
    }

    /**
     * 显示加载视图
     * */
    public void showLoadingView() {
        useEmptyView = false;
        useLoadingView = true;
        this.notifyDataSetChanged();
    }

    /**
     * 显示空视图
     * */
    public void showEmptyView() {
        useEmptyView = true;
        useLoadingView = false;
        this.notifyDataSetChanged();
    }

    public void removeHeaderView() {
        mHeaderView = null;
        useHeaderView = false;
    }

    public void removeFooterView() {
        mFooterView = null;
        useFooterView = false;
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public View getFooterView() {
        return mFooterView;
    }

    public View getLoadingView() {
        return mLoadingView;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    protected View inflateItemView(@LayoutRes int layoutRes, @Nullable ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(layoutRes, parent, false);
    }

    public void setDataSource(List<T> dataSource) {
        this.mDataSource.clear();
        this.mDataSource.addAll(dataSource);
        this.notifyDataSetChanged();
    }

    public void addToDataSource(T data) {
        this.mDataSource.add(data);
        this.notifyItemInserted(mDataSource.size());
    }

    public void addToDataSource(List<T> data) {
        this.mDataSource.addAll(data);
        this.notifyItemRangeInserted(mDataSource.size() - data.size(), data.size());
    }

    public void insertIntoDataSource(int position, T data) {
        if (position < 0 || position >= mDataSource.size() || data == null) {
            return;
        }
        this.mDataSource.add(position, data);
        this.notifyItemInserted(position);
    }

    public void insertIntoDataSource(int position, List<T> data) {
        if (position < 0 || position >= mDataSource.size() || data == null || data.size() == 0) {
            return;
        }
        this.mDataSource.addAll(position, data);
        this.notifyItemRangeInserted(position, data.size());
    }

    public void removeFromDataSource(int position) {
        if (position < 0 || position >= mDataSource.size()) {
            return;
        }
        this.mDataSource.remove(position);
        this.notifyItemRemoved(position);
    }

    public void removeFromDataSource(T data) {
        if (data == null) {
            return;
        }
        int position = mDataSource.indexOf(data);
        mDataSource.remove(data);
        this.notifyItemRemoved(position);
    }

    public void removeFromDataSource(List<T> data) {
        if (data == null || data.size() == 0) {
            return;
        }
        for (T t : data) {
            int position = mDataSource.indexOf(t);
            mDataSource.remove(t);
            this.notifyItemRemoved(position);
        }
    }

    public void removeAll() {
        int count = mDataSource.size();
        this.mDataSource.clear();
        this.notifyItemRangeRemoved(0, count);
    }

    public List<T> getDataSource() {
        return mDataSource;
    }

    protected Context getContext() {
        return mContext;
    }

}
