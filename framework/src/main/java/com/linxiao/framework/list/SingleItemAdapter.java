package com.linxiao.framework.list;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;

import com.linxiao.framework.R;

import java.util.ArrayList;
import java.util.List;

/**
 * adapter template that contains single type data source management
 * <p>
 * template class extends from {@link BaseRecyclerViewAdapter}, contains single
 * type data source management, fit for most situation of using RecyclerView.
 * see code for more details
 * </p>
 *
 * @author linxiao
 * @since 2018/6/10.
 */
public abstract class SingleItemAdapter<T, VH extends BaseRecyclerViewHolder> extends BaseRecyclerViewAdapter<VH> {
    /**
     * item click listener in RecyclerView
     * */
    public interface OnItemClickListener {
        
        void onItemClick(SingleItemAdapter adapter, View itemView, int position);
    }
    /**
     * item long click Listener in RecyclerView
     * */
    public interface OnItemLongClickListener {
        
        void onItemLongClick(SingleItemAdapter adapter, View itemView, int position);
    }
    
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    
    private View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag(R.id.tag_on_item_click);
            mOnItemClickListener.onItemClick(SingleItemAdapter.this, v, position);
        }
    };
    
    private View.OnLongClickListener itemLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            int position = (int) v.getTag(R.id.tag_on_item_click);
            mOnItemLongClickListener.onItemLongClick(SingleItemAdapter.this, v, position);
            return true;
        }
    };
    
    private List<T> mDataSource;
    
    public SingleItemAdapter(Context context) {
        super(context);
        mDataSource = new ArrayList<>();
    }
    
    /**
     * set data to view holder
     * @param holder ViewHolder
     * @param position data position
     * @param data data at the position
     */
    protected abstract void setData(VH holder, int position, T data);
    
    @Override
    protected void onBindDataViewHolder(@NonNull VH holder, int dataPosition) {
        holder.itemView.setTag(R.id.tag_on_item_click, dataPosition);
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(itemClickListener);
        }
        if (mOnItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(itemLongClickListener);
        }
        setData(holder, dataPosition, mDataSource.get(dataPosition));
    }
    
    @Override
    protected int getDataItemCount() {
        if (mDataSource == null) {
            return 0;
        }
        return mDataSource.size();
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
