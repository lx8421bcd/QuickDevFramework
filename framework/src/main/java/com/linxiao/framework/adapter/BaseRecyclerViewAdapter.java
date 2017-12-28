package com.linxiao.framework.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * RecyclerView 基类
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

    private static final int HEADER_VIEW = 101;
    private static final int EMPTY_VIEW = 102;
    private static final int FOOTER_VIEW = 103;

    protected String TAG;

    private List<T> mDataSource;
    private Context mContext;
    //在这里使用ViewGroup存储Header、Footer和Empty，因为View是被存储在ViewHolder中的，
    //如果在RecyclerView 构建完毕后修改View引用，不会重新构建ViewHolder也没有办法替换View
    //因此这里让Holder存储ViewGroup，然后需要替换ContentView的时候修改ViewGroup即可
    private LinearLayout mHeaderContainer;
    private LinearLayout mFooterContainer;
    private FrameLayout mNoDataViewContainer;


    private View mEmptyView;
    private View mErrorView;
    private View mLoadingView;

    private boolean showNoDataView = false;
    private boolean showHeaderView = true;
    private boolean showFooterView = true;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            mOnItemClickListener.onItemClick(BaseRecyclerViewAdapter.this, v, position);
        }
    };

    private View.OnLongClickListener itemLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            int position = (int) v.getTag();
            mOnItemLongClickListener.onItemLongClick(BaseRecyclerViewAdapter.this, v, position);
            return true;
        }
    };

    public BaseRecyclerViewAdapter(Context context) {
        TAG = this.getClass().getSimpleName();
        mContext = context;
        mDataSource = new ArrayList<>();
    }

    /**
     * 向Holder设置数据
     * */
    protected abstract void setData(VH holder, int position, T data);

    /**
     * create ViewHolder which used to handle data
     * <p>called on On</p>
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
        int dataPosition = hasHeaderView() && showHeaderView ? position - 1 : position;
        int itemViewType = holder.getItemViewType();
        switch (itemViewType) {
        case HEADER_VIEW:
            break;
        case FOOTER_VIEW:
            break;
        case EMPTY_VIEW:
            break;
        default:
            setData(holder, dataPosition, mDataSource.get(dataPosition));
            holder.itemView.setTag(dataPosition);
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(itemClickListener);
            }
            if (mOnItemLongClickListener != null) {
                holder.itemView.setOnLongClickListener(itemLongClickListener);
            }
            break;
        }
    }
    @Override
    public int getItemCount() {
        int count = mDataSource.size();
        if (hasHeaderView() && showHeaderView) {
            count++;
        }
        if (hasNoDataView() && showNoDataView) {
            count++;
        }
        if (hasFooterView() && showFooterView) {
            count++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        boolean showHeader = hasHeaderView() && showHeaderView;
        if (hasNoDataView() && showNoDataView) {
            switch (position) {
            case 0 :
                return showHeader ? HEADER_VIEW : EMPTY_VIEW;
            case 1 :
                return showHeader ? EMPTY_VIEW : FOOTER_VIEW;
            case 2 :
                return FOOTER_VIEW;
            default :
                return super.getItemViewType(position);
            }
        }
        int dataPosition = showHeader ? position - 1 : position;
        if (dataPosition < 0) {
            return HEADER_VIEW;
        }
        else if (mDataSource.size() > 0) {
            if (dataPosition >= mDataSource.size()) {
                return FOOTER_VIEW;
            }
        }
        else {
            if (hasFooterView() && showFooterView) {
                return FOOTER_VIEW;
            }
        }
        return super.getItemViewType(dataPosition);
    }

    /**
     * 获取高度更新
     * */
    private int getRealPosition(int dataPosition) {
        return hasHeaderView() ? dataPosition + 1 : dataPosition;
    }

    private boolean hasNoDataView() {
        return mNoDataViewContainer != null && mNoDataViewContainer.getChildCount() > 0 && mDataSource.size() == 0;
    }

    private boolean hasHeaderView() {
        return mHeaderContainer != null && mHeaderContainer.getChildCount() > 0;
    }

    private boolean hasFooterView() {
        return mFooterContainer != null && mFooterContainer.getChildCount() > 0;
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

    public void addHeaderView(@NonNull View v) {
        addHeaderView(v, -1);
    }

    public void addHeaderView(@NonNull View v, int position) {
        if (mHeaderContainer == null) {
            mHeaderContainer = new LinearLayout(mContext);

            mHeaderContainer.setOrientation(LinearLayout.VERTICAL);
            mHeaderContainer.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        }
        if (position > 0 && position < mHeaderContainer.getChildCount()) {
            mHeaderContainer.addView(v, position);
        } else {
            mHeaderContainer.addView(v);
        }
        notifyDataSetChanged();
    }

    public void addFooterView(@NonNull View v) {
        addFooterView(v, -1);
    }

    public void addFooterView(@NonNull View v, int position) {
        if (mFooterContainer == null) {
            mFooterContainer = new LinearLayout(mContext);

            mFooterContainer.setOrientation(LinearLayout.VERTICAL);
            mFooterContainer.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        }
        if (position > 0 && position < mHeaderContainer.getChildCount()) {
            mFooterContainer.addView(v, position);
        } else {
            mFooterContainer.addView(v);
        }
        notifyDataSetChanged();

    }

    public void removeHeaderView(View v) {
        if (mHeaderContainer != null) {
            mHeaderContainer.removeView(v);
            notifyDataSetChanged();
        }
    }

    public void removeHeaderView(int position) {
        if (mHeaderContainer != null) {
            View v = mHeaderContainer.getChildAt(position);
            if (v != null) {
                removeHeaderView(v);
            }
        }
    }

    public void removeFooterView(View v) {
        if (mFooterContainer != null) {
            mFooterContainer.removeView(v);
            notifyDataSetChanged();
        }
    }

    public void removeFooterView(int position) {
        if (mFooterContainer != null) {
            View v = mFooterContainer.getChildAt(position);
            if (v != null) {
                removeFooterView(v);
            }
        }
    }

    /**
     * 是否显示Header
     * */
    public void setHeaderVisible(boolean visible) {
        showHeaderView = visible;
        if (hasHeaderView()) {
            notifyItemChanged(getHeaderPosition());
        }
    }

    public boolean isHeaderVisible() {
        return hasHeaderView() && showHeaderView;
    }

    /**
     * 是否显示Footer
     * */
    public void setFooterVisible(boolean visible) {
        showFooterView = visible;
        if (hasFooterView()) {
            notifyItemChanged(getFooterPosition());
        }
    }

    public boolean isFooterVisible() {
        return hasFooterView() && showFooterView;
    }

    /**
     * 设置没有数据时显示的View
     * */
    public void setEmptyView(@NonNull View v) {
        mEmptyView = v;
        showNoDataView = true;
    }

    public void setLoadingView(@NonNull View v) {
        mLoadingView = v;
        showNoDataView = true;
    }

    public void setErrorView(@NonNull View v) {
        mErrorView = v;
        showNoDataView = true;
    }

    /**
     * 显示加载视图
     * */
    public void showLoadingView() {
        showNoDataView(mLoadingView);
    }

    /**
     * 显示空视图
     * */
    public void showEmptyView() {
        showNoDataView(mEmptyView);
    }

    /**
     * 显示加载错误视图
     * */
    public void showErrorView() {
        showNoDataView(mErrorView);
    }

    /**
     * 隐藏没有数据时显示的View
     * */
    private void hideNoDataView() {
        if (hasNoDataView()) {
            mNoDataViewContainer.removeAllViews();
        }
//        notifyDataSetChanged();
        notifyItemRemoved(hasHeaderView() ? 1 : 0);
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
        else {
            notifyDataSetChanged();
        }
    }

    @Override
    public void onViewAttachedToWindow(VH holder) {
        super.onViewAttachedToWindow(holder);
        int type = holder.getItemViewType();
        if (type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW) {
            if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                params.setFullSpan(true);
            }
        }
    }

    public int getDataSize() {
        if (mDataSource == null) {
            return 0;
        }
        return mDataSource.size();
    }

    public int getLastIndex() {
        if (mDataSource == null) {
            return 0;
        }
        return mDataSource.size() - 1;
    }

    public void notifyDataInserted(int dataPosition) {

        notifyItemInserted(getRealPosition(dataPosition)); // count header container
    }

    public void notifyDataChanged(int dataPosition) {

        notifyItemChanged(getRealPosition(dataPosition));
    }

    public void notifyDataChanged(int dataPosition, Object payload) {

        notifyItemChanged(getRealPosition(dataPosition), payload);
    }

    public void notifyDataRemoved(int dataPosition) {

        notifyItemRemoved(getRealPosition(dataPosition));
    }

    public void notifyDataMoved(int fromPosition, int toPosition) {
        notifyItemMoved(getRealPosition(fromPosition), getRealPosition(toPosition));
    }

    public void notifyDataRangeChanged(int dataPosition, int itemCount) {
        notifyItemRangeChanged(getRealPosition(dataPosition), itemCount);
    }

    public void notifyDataRangeChanged(int dataPosition, int itemCount, Object payload) {
        notifyItemRangeChanged(getRealPosition(dataPosition), itemCount, payload);
    }

    public void notifyDataRangeRemoved(int dataPosition, int itemCount) {
        notifyItemRangeRemoved(getRealPosition(dataPosition), itemCount);
    }

    public void notifyDataRangeInserted(int dataPosition, int itemCount) {
        notifyItemRangeInserted(getRealPosition(dataPosition), itemCount);
    }

    public void setDataSource(@NonNull List<T> dataSource) {
        this.mDataSource.clear();
        this.mDataSource = dataSource;
        this.notifyDataSetChanged();
    }

    public void addToDataSource(@NonNull T data) {
        hideNoDataView();
        this.mDataSource.add(data);
        this.notifyDataInserted(getLastIndex());
    }

    public void addToDataSource(@NonNull List<T> data) {
        hideNoDataView();
        this.mDataSource.addAll(data);
        this.notifyDataRangeInserted(getLastIndex(), data.size());
    }

    public void addToDataSource(int position, @NonNull T data) {
        if (position < 0 || position > mDataSource.size()) {
            return;
        }
        hideNoDataView();
        this.mDataSource.add(position, data);
        this.notifyDataInserted(position);
    }

    public void addToDataSource(int position, @NonNull List<T> data) {
        if (position < 0 || position >= mDataSource.size() || data.size() == 0) {
            return;
        }
        hideNoDataView();
        this.mDataSource.addAll(position, data);
        notifyDataRangeInserted(position, data.size());
    }

    public void removeFromDataSource(int position) {
        if (position < 0 || position >= mDataSource.size()) {
            return;
        }
        this.mDataSource.remove(position);
        notifyDataRemoved(position);
    }

    public void removeFromDataSource(T data) {
        if (data == null) {
            return;
        }
        int position = mDataSource.indexOf(data);
        mDataSource.remove(data);
        notifyDataRemoved(position);
    }

    public void removeFromDataSource(List<T> data) {
        if (data == null || data.size() == 0) {
            return;
        }
        for (T t : data) {
            int position = mDataSource.indexOf(t);
            mDataSource.remove(t);
            notifyDataRemoved(position);
        }
    }

    public void removeAll() {
        int count = mDataSource.size();
        this.mDataSource.clear();
        notifyDataRangeRemoved(0, count);
    }

    public T getFromDataSource(int position) {
        if (position >= 0 && position < mDataSource.size()) {
            return mDataSource.get(position);
        }
        return null;
    }

    public List<T> getDataSource() {
        return mDataSource;
    }

    protected Context getContext() {
        return mContext;
    }

    public LinearLayout getHeaderContainer() {
        return mHeaderContainer;
    }

    public LinearLayout getFooterContainer() {
        return mFooterContainer;
    }

    public FrameLayout getNoDataContainer() {
        return mNoDataViewContainer;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }
}
