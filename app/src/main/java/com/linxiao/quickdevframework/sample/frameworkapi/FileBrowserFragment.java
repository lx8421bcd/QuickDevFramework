package com.linxiao.quickdevframework.sample.frameworkapi;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linxiao.framework.list.BaseRecyclerViewAdapter;
import com.linxiao.framework.dialog.AlertDialogManager;
import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.framework.file.FileManager;
import com.linxiao.framework.permission.PermissionProhibitedListener;
import com.linxiao.framework.permission.PermissionManager;
import com.linxiao.framework.permission.RequestPermissionCallback;
import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.sample.adapter.FileListAdapter;
import com.linxiao.quickdevframework.sample.divider.HorizontalDecoration;

import java.io.File;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 文件浏览Fragment
 * Created by linxiao on 2017/4/15.
 */
public class FileBrowserFragment extends BaseFragment {

    @BindView(R.id.tvPath)
    TextView tvPath;
    @BindView(R.id.rcvFileList)
    RecyclerView rcvFileList;

    private FileListAdapter fileListAdapter;

    private File currentPath;

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setContentView(R.layout.fragment_file_browser, container);
        ButterKnife.bind(this, getContentView());
        initView();

        currentPath = FileManager.getExternalStorageRoot();
        loadPath(currentPath);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initView() {
        rcvFileList.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvFileList.setItemAnimator(new DefaultItemAnimator());
        rcvFileList.addItemDecoration(new HorizontalDecoration(1, new ColorDrawable(Color.BLACK)));

        fileListAdapter = new FileListAdapter(getContext());
        rcvFileList.setAdapter(fileListAdapter);
        fileListAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter adapter, View itemView, int position) {

            }
        });

    }

    /**
     * 加载文件路径，将文件夹下的列表
     * */
    private void loadPath(final File path) {
        PermissionManager.performWithPermission(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE)
        .doOnProhibited(new PermissionProhibitedListener() {
            @Override
            public void onProhibited(String permission) {
                AlertDialogManager.showAlertDialog("请授予文件管理权限以查看演示效果");
            }
        })
        .perform(getActivity(), new RequestPermissionCallback() {
            @Override
            public void onGranted() {

                fileListAdapter.setDataSource(Arrays.asList(path.listFiles()));
                tvPath.setText(path.getName());
                currentPath = path;
            }

            @Override
            public void onDenied() {
                AlertDialogManager.showAlertDialog("未授予权限");
            }
        });
    }
}
