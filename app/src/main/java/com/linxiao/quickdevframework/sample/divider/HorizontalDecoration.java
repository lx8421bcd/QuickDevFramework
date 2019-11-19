package com.linxiao.quickdevframework.sample.divider;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * 水平列表分隔线
 * Created by linxiao on 2016-08-07.
 */
public class HorizontalDecoration extends RecyclerView.ItemDecoration {

    private int dividerSize;

    private Drawable divider;

    public HorizontalDecoration(int dividerSize, Drawable divider) {
        this.dividerSize = dividerSize;
        this.divider = divider;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int itemCount = parent.getChildCount();
        for (int i = 0; i < itemCount; i++) {
            View item = parent.getChildAt(i);
            RecyclerView.LayoutParams itemLayoutParams = (RecyclerView.LayoutParams) item.getLayoutParams();
            int left = item.getLeft() - itemLayoutParams.leftMargin;
            int right = item.getRight() + itemLayoutParams.rightMargin;
            int top = item.getBottom() + itemLayoutParams.bottomMargin;
            int bottom = top + dividerSize;
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
            if(i == 0) {
                bottom = item.getTop() + itemLayoutParams.topMargin;
                top = bottom - dividerSize;
                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        super.getItemOffsets(outRect, view, parent, state);
        int top = 0;
        if(parent.getChildLayoutPosition(view) == 0) {
            top = dividerSize;
        }
        outRect.set(0, top, 0, dividerSize);
    }
}
