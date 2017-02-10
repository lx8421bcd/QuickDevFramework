package com.linxiao.quickdevframework.frameworkapi;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.linxiao.framework.fragment.BaseFragment;
import com.linxiao.framework.support.ToastWrapper;
import com.linxiao.framework.support.dialog.AlertDialogBuilder;
import com.linxiao.framework.support.dialog.AlertDialogWrapper;
import com.linxiao.quickdevframework.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogApiFragment extends BaseFragment {

    @BindView(R.id.cbShowTitle)
    CheckBox cbShowTitle;

    @BindView(R.id.cbShowIcon)
    CheckBox cbShowIcon;

    @BindView(R.id.cbSetPositive)
    CheckBox cbSetPositive;

    @BindView(R.id.cbSetNegative)
    CheckBox cbSetNegative;

    @BindView(R.id.cbSetCancelAble)
    CheckBox cbSetCancelAble;


    @Override
    protected int getInflateLayoutRes() {
        return R.layout.fragment_dialog_api;
    }

    @Override
    protected void onCreateContentView(View contentView, LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, contentView);

        cbShowIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    cbShowTitle.setChecked(true);
                }
            }
        });
        cbShowTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked){
                    cbShowIcon.setChecked(false);
                }
            }
        });
    }

    @OnClick(R.id.btnShowAlertDialog)
    public void onShowAlertDialogClick(View v) {
        AlertDialogBuilder builder = AlertDialogWrapper.createAlertDialogBuilder();
        builder.setMessage(getString(R.string.sample_dialog_message));
        if (cbShowTitle.isChecked()) {
            builder.setTitle(getString(R.string.sample_dialog_title));
        }
        if (cbShowIcon.isChecked()) {
            builder.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_notify));
        }
        if (cbSetPositive.isChecked()) {
            builder.setPositiveText(getString(R.string.sample_positive));
            builder.setPositiveButton(new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ToastWrapper.showToast(getContext(), getString(R.string.positive_click));
                    dialogInterface.dismiss();
                }
            });
        }
        if (cbSetNegative.isChecked()) {
            builder.setNegativeText(getString(R.string.sample_negative));
            builder.setNegativeButton(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ToastWrapper.showToast(getContext(), getString(R.string.negative_click));
                    dialogInterface.dismiss();
                }
            });
        }
        if (cbSetCancelAble.isChecked()) {
            builder.setCancelable(true);
        } else {
            builder.setCancelable(false);
        }
        builder.show();
    }

    @OnClick(R.id.btnShowSimpleDialog)
    public void onSimpleDialogClick(View v) {
        AlertDialogWrapper.showAlertDialog(getString(R.string.sample_dialog_message));
    }

    @OnClick(R.id.btnShowOnStartActivity)
    public void onShowStartActivityClick(View v) {
        startActivity(new Intent(getActivity(), NotificationTargetActivity.class));
        AlertDialogWrapper.showAlertDialog( "dialog after start activity");
    }

    @OnClick(R.id.btnShowTopDialog)
    public void onShowTopDialogClick(View v) {
        Intent backServiceIntent = new Intent(getActivity(), BackgroundService.class);
        getActivity().startService(backServiceIntent);
    }

    @OnClick(R.id.btnShowBottomDialog)
    public void onShowBottomDialogClick(View v) {
        SampleBottomDialogFragment dialogFragment = new SampleBottomDialogFragment();
        dialogFragment.show(getFragmentManager(), "SampleDialog");
    }
}
