package com.linxiao.quickdevframework.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linxiao.quickdevframework.R;

import java.util.List;

/**
 *
 * Created by linxiao on 2016/11/30.
 */
public class ApiSampleListAdapter extends RecyclerView.Adapter<ApiSampleListAdapter.ApiSampleHolder> {

    private List<ApiSampleObject> dataSource;
    private Context mContext;

    public ApiSampleListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public List<ApiSampleObject> getDataSource() {
        return dataSource;
    }

    public void setDataSource(List<ApiSampleObject> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ApiSampleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ApiSampleHolder(LayoutInflater.from(mContext).inflate(R.layout.item_api_sample, parent, false));
    }

    @Override
    public void onBindViewHolder(ApiSampleHolder holder, int position) {
        holder.setData(dataSource.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    class ApiSampleHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ApiSampleHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tvApiSample);
        }

        public void setData(final ApiSampleObject object) {
            textView.setText(object.getApiName());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, object.getDestActivity()));
                }
            });
        }
    }
}
