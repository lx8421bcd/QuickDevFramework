package com.linxiao.quickdevframework.sample.frameworkapi;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.linxiao.framework.common.ToastAlert;
import com.linxiao.framework.dialog.AlertDialogBuilder;
import com.linxiao.framework.dialog.AlertDialogManager;
import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.databinding.FragmentDialogApiBinding;
import com.linxiao.quickdevframework.main.SimpleViewBindingFragment;

public class DialogApiFragment extends SimpleViewBindingFragment<FragmentDialogApiBinding> {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewBinding().cbShowIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    getViewBinding().cbShowTitle.setChecked(true);
                }
            }
        });
        getViewBinding().cbShowTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked){
                    getViewBinding().cbShowIcon.setChecked(false);
                }
            }
        });
        getViewBinding().btnShowAlertDialog.setOnClickListener(this::onShowAlertDialogClick);
        getViewBinding().btnShowSimpleDialog.setOnClickListener(this::onSimpleDialogClick);
        getViewBinding().btnShowOnStartActivity.setOnClickListener(this::onShowStartActivityClick);
        getViewBinding().btnShowBottomDialog.setOnClickListener(this::onShowBottomDialogClick);
        getViewBinding().btnShowTopDialog.setOnClickListener(this::onShowTopDialogClick);
    }

    public void onShowAlertDialogClick(View v) {
        AlertDialogBuilder builder = AlertDialogManager.createAlertDialogBuilder();
        builder.setMessage(getString(R.string.sample_dialog_message));
        if (getViewBinding().cbShowTitle.isChecked()) {
            builder.setTitle(getString(R.string.sample_dialog_title));
        }
        if (getViewBinding().cbShowIcon.isChecked()) {
            builder.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_notify));
        }
        if (getViewBinding().cbSetPositive.isChecked()) {
            builder.setPositiveText(getString(R.string.sample_positive));
            builder.setPositiveButton(new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ToastAlert.showToast(getContext(), getString(R.string.positive_click));
                    dialogInterface.dismiss();
                }
            });
        }
        if (getViewBinding().cbSetNegative.isChecked()) {
            builder.setNegativeText(getString(R.string.sample_negative));
            builder.setNegativeButton(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ToastAlert.showToast(getContext(), getString(R.string.negative_click));
                    dialogInterface.dismiss();
                }
            });
        }
        if (getViewBinding().cbSetCancelAble.isChecked()) {
            builder.setCancelable(true);
        } else {
            builder.setCancelable(false);
        }
        builder.show();
    }

    public void onSimpleDialogClick(View v) {
        AlertDialogManager.showAlertDialog(getString(R.string.sample_dialog_message));
    }

    public void onShowStartActivityClick(View v) {
        startActivity(new Intent(getActivity(), NotificationTargetActivity.class));
        AlertDialogManager.showAlertDialog( "dialog after start activity");
    }

    public void onShowTopDialogClick(View v) {
        Intent backServiceIntent = new Intent(getActivity(), BackgroundService.class);
        getActivity().startService(backServiceIntent);
    }

    public void onShowBottomDialogClick(View v) {
        SampleBottomDialogFragment dialogFragment = new SampleBottomDialogFragment();
        dialogFragment.show(getFragmentManager(), "SampleDialog");
    }
}
