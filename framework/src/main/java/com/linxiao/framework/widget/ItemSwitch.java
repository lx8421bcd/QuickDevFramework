package com.linxiao.framework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.linxiao.framework.R;


/**
 * 切换开关
 *
 * @author linxiao
 * @since 2017-04-18
 */
public class ItemSwitch extends RelativeLayout {

    private TextView nameView;
    private CheckBox switchView;
    private View dividerView;

    private OnClickListener itemOnClick = null;

    public ItemSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_switch, this, true);
        nameView = (TextView) findViewById(R.id.item_switch_name);
        switchView = (CheckBox) findViewById(R.id.item_switch_switch);
        dividerView = findViewById(R.id.item_switch_divider);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ItemSwitch);
        // name init
        String name = a.getString(R.styleable.ItemSwitch_itemswitch_name);
        if (name != null) {
            nameView.setText(name);
        }
        if (a.hasValue(R.styleable.ItemSwitch_itemswitch_nameTextColor)) {
            nameView.setTextColor(a.getColorStateList(R.styleable.ItemSwitch_itemswitch_nameTextColor));
        } else {
            nameView.setTextColor(getResources().getColor(android.R.color.black));
        }
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int nameTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, dm);
        nameTextSize = a.getDimensionPixelSize(R.styleable.ItemSwitch_itemswitch_nameTextSize, nameTextSize);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, nameTextSize);
        // switch init
        boolean checked = a.getBoolean(R.styleable.ItemSwitch_itemswitch_checked, false);
        switchView.setChecked(checked);
        Drawable switchDrawable = a.getDrawable(R.styleable.ItemSwitch_itemswitch_switchDrawable);
        if (switchDrawable != null) {
            switchView.setButtonDrawable(switchDrawable);
        }
        // divider init
        boolean dividerVisible = a.getBoolean(R.styleable.ItemSwitch_itemswitch_dividerVisible, false);
        if (dividerVisible) {
            dividerView.setVisibility(VISIBLE);
        } else {
            dividerView.setVisibility(GONE);
        }
        int dividerColor = a.getColor(R.styleable.ItemSwitch_itemswitch_dividerColor, Color.parseColor("#F0F0F0"));
        dividerView.setBackgroundColor(dividerColor);
        int dividerSize = a.getDimensionPixelOffset(R.styleable.ItemSwitch_itemswitch_dividerSize, 1);
        dividerView.setMinimumHeight(dividerSize);
        a.recycle();

        super.setOnClickListener(view -> {
            switchView.toggle();
            if (itemOnClick != null) {
                itemOnClick.onClick(this);
            }
        });
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        itemOnClick = l;
    }

    public void setItemName(CharSequence name) {
        nameView.setText(name);
    }

    public void setTextStyle(boolean isBold,float size){
        if (isBold){
            nameView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
        nameView.setTextSize(size);
    }
    public void setItemChecked(boolean checked) {
        switchView.setChecked(checked);
    }

    public boolean isItemChecked() {
        return switchView.isChecked();

    }

}
