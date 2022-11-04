package com.linxiao.framework.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.linxiao.framework.common.DensityHelper;
import com.linxiao.framework.common.ScreenUtil;
import com.linxiao.framework.databinding.ItemTextOptionsBinding;
import com.linxiao.framework.databinding.PopupTextOptionsBinding;
import com.linxiao.framework.list.ViewBindingRecyclerHolder;
import com.linxiao.framework.list.ViewBindingSingleItemAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 单列表PopupWindow
 *
 * @author lx8421bcd
 * Create on 2017-06-19
 */
public class TextOptionsPopupWindow extends PopupWindow {

    private PopupTextOptionsBinding viewBinding;
    private final TextListAdapter adapter;
    private int textColor = -1;
    private int textSize = -1;
    private int textGravity = -1;
    private final List<String> textList = new ArrayList<>();
    private final HashMap<String, View.OnClickListener> itemClickMap = new HashMap<>();

    public TextOptionsPopupWindow(@NonNull Context context) {
        super(context);
        viewBinding = PopupTextOptionsBinding.inflate(LayoutInflater.from(context));
        setContentView(viewBinding.getRoot());
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        adapter = new TextListAdapter(context);
        adapter.setOnItemClickListener((adapter, itemView, dataPosition) -> {
            dismiss();
            String text = textList.get(dataPosition);
            View.OnClickListener listener = itemClickMap.get(text);
            if (listener != null) {
                listener.onClick(itemView);
            }
        });
        adapter.setDataSource(textList);
        viewBinding.rcvList.setLayoutManager(new LinearLayoutManager(context));
        viewBinding.rcvList.setAdapter(adapter);
    }

    public TextOptionsPopupWindow setWindowWidth(int windowWidth) {
        setWidth(windowWidth + DensityHelper.dp2px(8));
        return this;
    }

    public void show(View anchor) {
        int[] xy = new int[]{0, 0};
        anchor.getLocationInWindow(xy);
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        getContentView().measure(spec, spec);
        int width = getContentView().getMeasuredWidth();
        int height = getContentView().getMeasuredHeight();

        int xOffset = DensityHelper.dp2px(-4);
        int yOffset = -(anchor.getHeight() + height);
        int gravity = Gravity.START | Gravity.TOP;
        if (ScreenUtil.getUsableScreenWidth(anchor.getContext()) - xy[0] < width) {
            xOffset -= width;
        }
        if (xy[1] - ScreenUtil.getStatusBarHeight() < height) {
            yOffset = 0;
            gravity = Gravity.START | Gravity.BOTTOM;
        }
        showAsDropDown(anchor, xOffset, yOffset, gravity);
    }


    public TextOptionsPopupWindow addOptionItem(String title, View.OnClickListener click) {
        if (TextUtils.isEmpty(title)) {
            return this;
        }
        if (!textList.contains(title)) {
            textList.add(title);
        }
        itemClickMap.put(title, click);
        if (adapter != null) {
            adapter.notifyDataRangeChanged(0, textList.size() + 1);
        }
        return this;
    }

    public TextOptionsPopupWindow setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public TextOptionsPopupWindow setTextGravity(int textGravity) {
        this.textGravity = textGravity;
        return this;
    }

    public TextOptionsPopupWindow setTextSize(int textSize) {
        this.textSize = textSize;
        return this;
    }

    private class TextListAdapter extends ViewBindingSingleItemAdapter<String, ItemTextOptionsBinding> {

        public TextListAdapter(Context context) {
            super(context);
        }

        @Override
        protected void setData(ViewBindingRecyclerHolder<ItemTextOptionsBinding> holder, int position, String data) {
            if (textSize > 0) {
                holder.getViewBinding().getRoot().setTextSize(textSize);
            }
            if (textColor >= 0) {
                holder.getViewBinding().getRoot().setTextColor(textColor);
            }
            if (textGravity >= 0) {
                holder.getViewBinding().getRoot().setGravity(textGravity);
            }
            holder.getViewBinding().getRoot().setText(data);
        }
    }
}
