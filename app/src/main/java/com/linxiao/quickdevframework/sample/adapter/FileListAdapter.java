package com.linxiao.quickdevframework.sample.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.linxiao.framework.list.BaseRecyclerViewAdapter;
import com.linxiao.framework.list.BaseRecyclerViewHolder;
import com.linxiao.quickdevframework.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 文件列表管理Adapter
 * FIXME: 如何记录当前文件列表的选中状态？
 * solution1：建立个等长boolean列表用于记录
 * solution2：在操作时扫描
 * Created by linxiao on 2017/4/17.
 */
public class FileListAdapter extends BaseRecyclerViewAdapter<File, FileListAdapter.FileItemHolder> {

    public FileListAdapter(Context context) {
        super(context);
    }

    @Override
    protected void setData(FileItemHolder holder, int position, File data) {
        holder.tvFileName.setText(data.getName());

    }

    @Override
    protected FileItemHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        return new FileItemHolder(inflateItemView(R.layout.item_file, parent));
    }

    class FileItemHolder extends BaseRecyclerViewHolder {

        @BindView(R.id.tvFileName)
        TextView tvFileName;

        @BindView(R.id.cbSelect)
        CheckBox cbSelect;

        public FileItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
