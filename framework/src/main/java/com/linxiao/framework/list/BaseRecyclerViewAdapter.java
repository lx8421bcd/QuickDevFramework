package com.linxiao.framework.list;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * basic class template of {@link RecyclerView.Adapter}, provide to whole project
 * <p>
 * This base adapter contains headerView, emptyView, footerView management,
 * support for single DataSource and multiple DataSource, see codes for more details
 * </p>
 *
 * @author linxiao
 * @since 2018/6/10.
 */
public abstract class BaseRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    
    protected String TAG;
    
    protected static final int HEADER_VIEW = 101;
    protected static final int EMPTY_VIEW = 102;
    protected static final int FOOTER_VIEW = 103;
    
    private Context mContext;
    
    /* Using ViewGroup as the container to hold the fixed view because if just change the reference of
     the fixed view cannot trigger the onCreateViewHolder() method after the ViewHolder was created,
     which means you cannot replace fixed view in the ViewHolder directly, so we use the ViewGroup
     to contain the fixedViews and we can change them by editing the child views of the ViewGroup */
    private LinearLayout mHeaderContainer;
    private LinearLayout mFooterContainer;
    private FrameLayout mNoDataViewContainer;
    
    private View mEmptyView;
    private View mErrorView;
    private View mLoadingView;
    
    private boolean showNoDataView = false;
    private boolean showHeaderView = true;
    private boolean showFooterView = true;
    
    public BaseRecyclerViewAdapter(Context context) {
        TAG = this.getClass().getSimpleName();
        this.mContext = context;
    
        mHeaderContainer = new LinearLayout(mContext);
        mHeaderContainer.setOrientation(LinearLayout.VERTICAL);
        mHeaderContainer.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
    
        mFooterContainer = new LinearLayout(mContext);
        mFooterContainer.setOrientation(LinearLayout.VERTICAL);
        mFooterContainer.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
    }
    
    /**
     * instead of {@link #onCreateViewHolder(ViewGroup, int)}
     * @param parent parent
     * @param viewType viewType
     */
    protected abstract VH onCreateDataViewHolder(ViewGroup parent, int viewType);
    
    /**
     * used to instead of {@link #onBindViewHolder(RecyclerView.ViewHolder, int)}
     * @param holder holder
     * @param dataPosition position that uncounted header
     */
    protected abstract void onBindDataViewHolder(@NonNull VH holder, int dataPosition);
    
    /**
     * used to instead of {@link #onBindViewHolder(RecyclerView.ViewHolder, int)}
     * @param holder holder
     * @param dataPosition position that uncounted header
     * @param payloads payloads
     */
    protected void onBindDataViewHolder(@NonNull VH holder, int dataPosition, @NonNull List<Object> payloads) {
        onBindDataViewHolder(holder, dataPosition);
    }
    
    /**
     * used to instead of {@link #getItemCount()}
     * @return the items count of the data source
     */
    protected abstract int getDataItemCount();
    
    protected View inflateItemView(@LayoutRes int layoutRes, @Nullable ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(layoutRes, parent, false);
    }
    
    /**
     * create default view holder to hold fixed views
     * <p>fixed view include mHeaderContainer, mNoDataViewContainer, mFooterContainer</p>
     * */
    @SuppressWarnings("unchecked")
    private VH createEmptyViewHolder(View itemView) {
        return (VH) new BaseRecyclerViewHolder(itemView);
    }
    
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(@NonNull VH holder, int realPosition) {
        Log.d(TAG, "onBindViewHolder");
        int itemViewType = holder.getItemViewType();
        switch (itemViewType) {
        case HEADER_VIEW:
            return;
        case FOOTER_VIEW:
            return;
        case EMPTY_VIEW:
            return;
        }
        int dataPosition = getDataPosition(realPosition);
        onBindDataViewHolder(holder, dataPosition);
    }
    
    @Override
    public void onBindViewHolder(@NonNull VH holder, int realPosition, @NonNull List<Object> payloads) {
        Log.d(TAG, "onBindViewHolder: payloads");
        int itemViewType = holder.getItemViewType();
        switch (itemViewType) {
        case HEADER_VIEW:
            return;
        case FOOTER_VIEW:
            return;
        case EMPTY_VIEW:
            return;
        }
        int dataPosition = getDataPosition(realPosition);
        onBindDataViewHolder(holder, dataPosition, payloads);
    }
    
    @Override
    public int getItemCount() {
        int count = getDataItemCount();
        if (count == 0 &&showNoDataView && mNoDataViewContainer.getChildCount() > 0) {
            count ++;
        }
        if (isShowHeaderView()) {
            count++;
        }
        if (isShowFooterView()) {
            count++;
        }
        return count;
    }
    
    @Override
    public int getItemViewType(int realPosition) {
        int dataPosition = getDataPosition(realPosition);
        if (realPosition == 0) {
            if (isShowHeaderView()) {
                return HEADER_VIEW;
            }
            if (getDataItemCount() > 0) {
                return super.getItemViewType(dataPosition);
            }
            if (isShowNoDataView()) {
                return EMPTY_VIEW;
            }
            if (isShowFooterView()) {
                return FOOTER_VIEW;
            }
        }
        else if (realPosition == 1) {
            if (getDataItemCount() > 0) {
                return super.getItemViewType(dataPosition);
            }
            if (isShowNoDataView()) {
                return EMPTY_VIEW;
            }
            /* one of headerView and emptyView is displayed, and the footerView is also displayed,
            then the realPosition 1 should be footerView */
            if (isShowFooterView() && (isShowHeaderView() || isShowNoDataView())) {
                return FOOTER_VIEW;
            }
        }
        else if (realPosition == 2) {
            if (getDataItemCount() > 0) {
                return super.getItemViewType(dataPosition);
            }
            /* only the HeaderView and the emptyView are displayed can confirm that
               the realPosition 2 this footerView */
            if (isShowFooterView() && isShowHeaderView() && isShowFooterView()) {
                return FOOTER_VIEW;
            }
        }
        else if (realPosition == getItemCount() - 1) {
            if (isShowFooterView()) {
                return FOOTER_VIEW;
            }
        }
        // handle data item type
        return super.getItemViewType(dataPosition >= 0 ? dataPosition : 0);
    }
    
    /**
     * add headerView to the HeaderContainer
     * @param v headerView
     * @param position insert position
     */
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
    
    /**
     * add headerView to the HeaderContainer
     * @param v headerView
     */
    public void addHeaderView(@NonNull View v) {
        // -1 is the default parameter inside addView(View)
        addHeaderView(v, -1);
    }
    
    /**
     * remove headerView from the headerContainer
     * @param v headerView
     */
    public void removeHeaderView(View v) {
        if (mHeaderContainer != null) {
            mHeaderContainer.removeView(v);
            notifyDataSetChanged();
        }
    }
    
    /**
     * remove headerView from the headerContainer
     * @param position position of the headerView
     */
    public void removeHeaderView(int position) {
        if (mHeaderContainer != null) {
            View v = mHeaderContainer.getChildAt(position);
            if (v != null) {
                removeHeaderView(v);
            }
        }
    }
    
    /**
     * add footerView to the footerContainer
     * @param v footerView
     * @param position insert position
     */
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
    
    /**
     * add footerView to the footerContainer
     * @param v footerView
     */
    public void addFooterView(@NonNull View v) {
        // -1 is the default parameter inside addView(View)
        addFooterView(v, -1);
    }
    
    /**
     * remove footerView from the footerContainer
     * @param v footerView
     */
    public void removeFooterView(View v) {
        if (mFooterContainer != null) {
            mFooterContainer.removeView(v);
            notifyDataSetChanged();
        }
    }
    
    /**
     * remove footerView from the footerContainer
     * @param position position of the footerView
     */
    public void removeFooterView(int position) {
        if (mFooterContainer != null) {
            View v = mFooterContainer.getChildAt(position);
            if (v != null) {
                removeFooterView(v);
            }
        }
    }
    
    /**
     * set is show headerContainer
     * @param visible is show header
     */
    public void setHeaderVisible(boolean visible) {
        if (showHeaderView == visible) {
            return;
        }
        showHeaderView = visible;
        notifyItemChanged(getHeaderPosition());
    }
    
    /**
     * is show headerContainer
     */
    public boolean isHeaderVisible() {
        
        return showHeaderView;
    }
    
    /**
     * set is show footerContainer
     * @param visible is show footer
     */
    public void setFooterVisible(boolean visible) {
        showFooterView = visible;
        if (isShowFooterView()) {
            notifyItemChanged(getFooterPosition());
        }
    }
    
    /**
     * is show footerContainer
     */
    public boolean isFooterVisible() {
        
        return showFooterView;
    }
    

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
     * show loadingView if it's set
     * call {@link #hideNoDataView()} to dismiss after data are loaded
     */
    public void showLoadingView() {
        showNoDataView(mLoadingView);
    }
    
    /**
     * show emptyView if it's set
     * call {@link #hideNoDataView()} to dismiss after data are loaded
     */
    public void showEmptyView() {
        showNoDataView(mEmptyView);
    }
    
    /**
     * show errorView if it's set
     * call {@link #hideNoDataView()} to dismiss after data are loaded
     */
    public void showErrorView() {
        showNoDataView(mErrorView);
    }
    
    /**
     * 隐藏没有数据时显示的View
     * */
    public void hideNoDataView() {
        if (isShowNoDataView()) {
            mNoDataViewContainer.removeAllViews();
        }
        notifyItemRemoved(isShowHeaderView() ? 1 : 0);
    }
    
    private void showNoDataView(View noDataView) {
        if (noDataView == null) {
            return;
        }
//        if (mDataSource.size() > 0) {
//            mDataSource.clear();
//        }
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
    
    /**
     * provide to subclasses, convert dataPosition to realPosition that counted header
     * @param dataPosition dataPosition
     * @return realPosition
     */
    protected int getRealPosition(int dataPosition) {
        return isShowHeaderView() ? dataPosition + 1 : dataPosition;
    }
    
    /**
     * convert realPosition to dataPosition
     * @param realPosition realPosition
     * @return dataPosition
     */
    protected int getDataPosition(int realPosition) {
        return isShowHeaderView() ? realPosition - 1 : realPosition;
    }
    
    protected int getHeaderPosition() {
        if (mHeaderContainer != null) {
            return 0;
        }
        return -1;
    }
    
    protected int getFooterPosition() {
        if (mFooterContainer != null) {
            return getItemCount() - 1;
        }
        return -1;
    }
    
    private boolean isShowHeaderView() {
        return mHeaderContainer.getChildCount() > 0 && showHeaderView;
    }
    
    private boolean isShowFooterView() {
        return mFooterContainer.getChildCount() > 0 && showFooterView;
    }
    
    private boolean isShowNoDataView() {
        return showNoDataView && mNoDataViewContainer.getChildCount() > 0 && getDataItemCount() == 0;
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
}
