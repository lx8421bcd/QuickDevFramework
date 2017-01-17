package com.linxiao.framework.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.linxiao.framework.support.log.LogManager;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

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

    private static String TAG;

    private List<T> mDataSource;
    private Context mContext;
    //在这里使用ViewGroup存储Header、Footer和Empty，因为View是被存储在ViewHolder中的，
    //如果在RecyclerView 构建完毕后修改View引用，不会重新构建ViewHolder也没有办法替换View
    //因此这里让Holden存储ViewGroup，然后需要替换ContentView的时候修改ViewGroup即可
    private LinearLayout mHeaderContainer;
    private LinearLayout mFooterContainer;
    private FrameLayout mNoDataViewContainer;


    private View mEmptyView;
    private View mErrorView;
    private View mLoadingView;

    private boolean useNoDataView = false;
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

    protected View inflateItemView(@LayoutRes int layoutRes, @Nullable ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(layoutRes, parent, false);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        LogManager.d(TAG, "viewType = " + viewType);
        switch (viewType) {
        case HEADER_VIEW :
            return createEmptyViewHolder(mHeaderContainer);
        case EMPTY_VIEW :
            return createEmptyViewHolder(mNoDataViewContainer);
        case FOOTER_VIEW:
            return createEmptyViewHolder(mFooterContainer);
        default:
            return onCreateDataViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        int dataPosition = mHeaderContainer != null ? position - 1 : position;
        int itemViewType = holder.getItemViewType();
        switch (itemViewType) {
        case HEADER_VIEW:
            break;
        case FOOTER_VIEW:
            break;
        case EMPTY_VIEW:
            break;
        default:
            setData(holder, mDataSource.get(dataPosition));
            break;
        }
    }
    @Override
    public int getItemCount() {
        int count = mDataSource.size();
        if (mHeaderContainer != null) {
            count++;
        }
        if (hasEmptyView()) {
            count++;
        }
        if (mFooterContainer != null) {
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
                return FOOTER_VIEW; //TODO 此处逻辑需重写
            }
        }
    }

    private boolean hasEmptyView() {
        return useNoDataView && mNoDataViewContainer != null && mDataSource.size() == 0;
    }

    private boolean hasHeaderView() {
        return useHeaderView && mHeaderContainer != null && mHeaderContainer.getChildCount() > 0;
    }

    private boolean hasFooterView() {
        return useFooterView && mFooterContainer != null && mFooterContainer.getChildCount() > 0;
    }

    private int getHeaderPosition() {
        if (mHeaderContainer != null) {
            return 0;
        }
        return -1;
    }

    private int getFooterPosition() {
        if (mFooterContainer == null) {
            return -1;
        }
        return getItemCount();
    }

    public void setHeaderView(@NonNull View v, int position) {
        if (mHeaderContainer == null) {
            mHeaderContainer = new LinearLayout(mContext);

            mHeaderContainer.setOrientation(LinearLayout.VERTICAL);
            mHeaderContainer.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        }
        if (position < mHeaderContainer.getChildCount()) {
            mHeaderContainer.addView(v, position);
        } else {
            mHeaderContainer.addView(v);
        }
        notifyItemInserted(getHeaderPosition());
    }

    public void setFooterView(@NonNull View v,  int position) {
        if (mHeaderContainer == null) {
            mHeaderContainer = new LinearLayout(mContext);

            mHeaderContainer.setOrientation(LinearLayout.VERTICAL);
            mHeaderContainer.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        }
        if (position < mHeaderContainer.getChildCount()) {
            mHeaderContainer.addView(v, position);
        } else {
            mHeaderContainer.addView(v);
        }
        notifyItemInserted(getFooterPosition());
    }

    /**
     * 设置没有数据时显示的View
     * */
    public void setEmptyView(@NonNull View v) {
        mEmptyView = v;
        useNoDataView = true;
    }

    public void setLoadingView(@NonNull View v) {
        mLoadingView = v;
        useNoDataView = true;
    }

    public void setErrorView(@NonNull View v) {
        mErrorView = v;
        useNoDataView = true;
    }

    /**
     * 显示加载视图
     * */
    public void showLoadingView(boolean isShow) {
        if (!isShow) {
            clearNoDataView();
        }
        showNoDataView(mLoadingView);
    }

    /**
     * 显示空视图
     * */
    public void showEmptyView(boolean isShow) {
        if (!isShow) {
            clearNoDataView();
        }
        showNoDataView(mEmptyView);
    }

    /**
     * 显示加载错误视图
     * */
    public void showErrorView(boolean isShow) {
        if (!isShow) {
            clearNoDataView();
        }
        showNoDataView(mErrorView);
    }

    /**
     * 没有数据时显示的View
     * <p>主要是操作EmptyContainer用于切换在Empty、Loading、Error下显示的View</p>
     * */
    private void showNoDataView(View noDataView) {
        if (noDataView == null) {
            return;
        }
        if (mDataSource.size() > 0) {
            mDataSource.clear();
        }
        boolean isInsert = false;
        if (mNoDataViewContainer == null) {
            mNoDataViewContainer = new FrameLayout(mContext);
            final RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
            final ViewGroup.LayoutParams lp = noDataView.getLayoutParams();
            if (lp != null) {
                layoutParams.width = lp.width;
                layoutParams.height = lp.height;
            }
            mNoDataViewContainer.setLayoutParams(layoutParams);
            isInsert = true;
        }
        if (noDataView.getParent() != null) {
            ((ViewGroup) noDataView.getParent()).removeView(noDataView);
        }
        mNoDataViewContainer.removeAllViews();
        mNoDataViewContainer.addView(noDataView);
        if (isInsert) {
            int position = getHeaderPosition() + 1;
            notifyItemInserted(position);
        }
    }

    private void clearNoDataView() {
        if (mNoDataViewContainer != null) {
            mNoDataViewContainer.removeAllViews();
        }
        notifyDataSetChanged();
    }

    public void removeHeaderView() {
        mHeaderContainer = null;
        useHeaderView = false;
    }

    public void removeFooterView() {
        mFooterContainer = null;
        useFooterView = false;
    }

    public View getHeaderView() {
        return mHeaderContainer;
    }

    public View getFooterView() {
        return mFooterContainer;
    }

    public View getLoadingView() {
        return mLoadingView;
    }

    public View getEmptyView() {
        return mEmptyView;
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
