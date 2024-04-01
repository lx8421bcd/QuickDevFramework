package com.linxiao.quickdevframework.sample.frameworkapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linxiao.framework.architecture.SimpleViewBindingFragment;
import com.linxiao.framework.common.ToastAlert;
import com.linxiao.framework.dialog.AlertDialogFragment;
import com.linxiao.framework.dialog.DialogExtensionsKt;
import com.linxiao.quickdevframework.R;
import com.linxiao.quickdevframework.databinding.FragmentDialogApiBinding;

public class DialogApiFragment extends SimpleViewBindingFragment<FragmentDialogApiBinding> {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewBinding().btnShowAlertDialog.setOnClickListener(this::onShowAlertDialogClick);
        getViewBinding().btnShowSimpleDialog.setOnClickListener(this::onSimpleDialogClick);
        getViewBinding().btnShowOnStartActivity.setOnClickListener(this::onShowStartActivityClick);
        getViewBinding().btnShowBottomDialog.setOnClickListener(this::onShowBottomDialogClick);
        getViewBinding().btnShowTopDialog.setOnClickListener(this::onShowTopDialogClick);
    }

    public void onShowAlertDialogClick(View v) {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();
        if (getViewBinding().rbTypeString.isChecked()) {
            dialogFragment.setMessage(getString(R.string.sample_dialog_message));
        }
        if (getViewBinding().rbTypeHtmlString.isChecked()) {
            dialogFragment.setContentHtml("<ul>\n" +
                    "<li>html unordered list item 1</li>\n" +
                    "<li>html unordered list item 2</li>\n" +
                    "<li>html unordered list item 3</li>\n" +
                    "</ul>"
            );
        }
        if (getViewBinding().rbTypeHtmlLink.isChecked()) {
            dialogFragment.setContentLink("https://www.google.com");
        }
        dialogFragment.setPositiveButton(getString(R.string.sample_positive), (dialogInterface, i) -> {
            ToastAlert.showToast(getContext(), getString(R.string.positive_click));
            dialogInterface.dismiss();
        });
        dialogFragment.setNegativeButton(getString(R.string.sample_negative), (dialogInterface, i) -> {
            ToastAlert.showToast(getContext(), getString(R.string.negative_click));
            dialogInterface.dismiss();
        });
        dialogFragment.setDialogCancelable(false);
        dialogFragment.show(getChildFragmentManager());
    }

    public void onSimpleDialogClick(View v) {
        DialogExtensionsKt.showAlert(getString(R.string.sample_dialog_message));
    }

    public void onShowStartActivityClick(View v) {
        startActivity(new Intent(getActivity(), NotificationTargetActivity.class));
        DialogExtensionsKt.showAlert( "dialog after start activity");
    }

    public void onShowTopDialogClick(View v) {
        Intent backServiceIntent = new Intent(getActivity(), BackgroundService.class);
        requireActivity().startService(backServiceIntent);
    }

    public void onShowBottomDialogClick(View v) {
        SampleBottomDialogFragment dialogFragment = new SampleBottomDialogFragment();
        dialogFragment.show(getChildFragmentManager(), "SampleDialog");
    }
}
