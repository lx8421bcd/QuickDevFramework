package com.linxiao.framework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linxiao.framework.R;

/**
 * 列表项视图<BR>
 * 主要提供的功能(从左到右):<BR>
 *
 * @author lx8421bcd
 * @since 2017-04-17
 */
public class ItemView extends RelativeLayout {

    private ImageView iconView;
    private TextView nameView;
    private TextView infoView;
    private View ivMore;
    private View dividerView;
    private ImageView infoImageView;

    private int nameTextSize = 14;

    public ItemView(Context context) {
        this(context, null);
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_view, this, true);

        iconView = (ImageView) findViewById(R.id.iv_icon);
        nameView = (TextView) findViewById(R.id.tv_name);
        infoView = (TextView) findViewById(R.id.tv_info);
        ivMore = findViewById(R.id.iv_more);
        dividerView = findViewById(R.id.divider);
        infoImageView = (ImageView) findViewById(R.id.tv_info_image);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ItemView);
        CharSequence name = a.getText(R.styleable.ItemView_itemview_name);
        if (name != null) nameView.setText(name);
        CharSequence info = a.getText(R.styleable.ItemView_itemview_info);
        if (info != null) infoView.setText(info);
        Drawable icon = a.getDrawable(R.styleable.ItemView_itemview_icon);
        int iconSize = a.getDimensionPixelSize(R.styleable.ItemView_itemview_iconSize, 0);
        if (icon != null) {
            iconView.setImageDrawable(icon);
            iconView.getLayoutParams().width = iconSize;
            iconView.getLayoutParams().height = iconSize;
        } else {
            iconView.setVisibility(GONE);
        }

        Drawable avatar = a.getDrawable(R.styleable.ItemView_itemview_infoImageSrc);
        if (avatar != null) {
            infoImageView.setImageDrawable(avatar);
        } else {
            infoImageView.setVisibility(GONE);
        }

        boolean showMore = a.getBoolean(R.styleable.ItemView_itemview_showMoreIcon, true);
        ivMore.setVisibility(showMore ? View.VISIBLE : View.GONE);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        nameTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, nameTextSize, dm);
        nameTextSize = a.getDimensionPixelSize(R.styleable.ItemView_itemview_nameTextSize, nameTextSize);
        if (a.hasValue(R.styleable.ItemView_itemview_nameTextColor)) {
            nameView.setTextColor(a.getColorStateList(R.styleable.ItemView_itemview_nameTextColor));
        } else {
            nameView.setTextColor(getResources().getColor(android.R.color.black));
        }
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, nameTextSize);

        int infoTextSize = a.getDimensionPixelSize(R.styleable.ItemView_itemview_infoTextSize, nameTextSize);
        infoView.setTextSize(TypedValue.COMPLEX_UNIT_PX, infoTextSize);
        if (a.hasValue(R.styleable.ItemView_itemview_infoTextColor)) {
            infoView.setTextColor(a.getColorStateList(R.styleable.ItemView_itemview_infoTextColor));
        }

        // divider init
        boolean dividerVisible = a.getBoolean(R.styleable.ItemView_itemview_dividerVisible, false);
        if (dividerVisible) {
            dividerView.setVisibility(VISIBLE);
        } else {
            dividerView.setVisibility(GONE);
        }
        int dividerColor = a.getColor(R.styleable.ItemView_itemview_dividerColor, Color.parseColor("#F0F0F0"));
        dividerView.setBackgroundColor(dividerColor);
        int dividerSize = a.getDimensionPixelOffset(R.styleable.ItemView_itemview_dividerSize, 1);
        dividerView.setMinimumHeight(dividerSize);
        a.recycle();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        nameView.setEnabled(enabled);
        iconView.setEnabled(enabled);
    }

    public ImageView getInfoImageView() {
        return infoImageView;
    }

    public ImageView getItemIconView() {
        return iconView;
    }

    public TextView getNameView() {
        return nameView;
    }

    public TextView getInfoView() {
        return infoView;
    }

    /**
     * 设置是否显示更多标识
     *
     * @param show true显示,false不显示
     */
    public void showMoreIcon(boolean show) {
        ivMore.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setDividerVisible(boolean show) {
        dividerView.setVisibility(show ? VISIBLE : GONE);
    }
}
