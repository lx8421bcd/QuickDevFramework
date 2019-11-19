package com.linxiao.framework.list;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
/**
 * RecyclerView 等距布局装饰器
 * <p>用于实现RecyclerView中各个项目之间拥有相同间隔的布局效果,
 * 不作特殊配置默认为纵向布局，单列
 * </p>
 * <strong>此装饰器不适用于瀑布流布局</strong>
 *
 * @author linxiao
 * Create on 2017-11-09
 */
public class EquidistantDecoration extends RecyclerView.ItemDecoration {
    /**
     * 纵向
     * */
    public static final int VERTICAL = 0;
    /**
     * 横向布局
     * */
    public static final int HORIZONTAL = 1;
    /**
     * 边界控制，使用spacing值
     */
    public static final int BORDER_DEFAULT = -1;
    // 布局方向
    private int orientation = VERTICAL;
    // 行/列数
    private int spanCount = 1;
    // 距离大小
    private int spacingSize;
    // 间距内容
    private Drawable spacingDrawable;
    // 边界宽度
    private int borderLeftWidth = BORDER_DEFAULT;
    private int borderRightWidth = BORDER_DEFAULT;
    private int borderTopWidth = BORDER_DEFAULT;
    private int borderBottomWidth = BORDER_DEFAULT;
    /**
     * @param spanCount 列/行数
     * @param spacingSize 间隔大小，单位 px
     * */
    public EquidistantDecoration(int spanCount, int spacingSize) {
        this(VERTICAL, spanCount, spacingSize);
    }
    /**
     * @param orientation 布局方向
     * @param spanCount 列/行数
     * @param spacingSize 间隔大小，单位 px
     * */
    public EquidistantDecoration(int orientation, int spanCount, int spacingSize) {
        this.orientation = orientation;
        this.spanCount = spanCount;
        this.spacingSize = spacingSize;
        spacingDrawable = new ColorDrawable(Color.TRANSPARENT);
    }
    public void setSpacingDrawable(Drawable spacingDrawable) {
        if (spacingDrawable == null) {
            return;
        }
        this.spacingDrawable = spacingDrawable;
    }
    public void setBorderWidth(int width) {
        borderLeftWidth = width;
        borderRightWidth = width;
        borderTopWidth = width;
        borderBottomWidth = width;
    }
    public void setBorderLeftWidth(int borderLeftWidth) {
        this.borderLeftWidth = borderLeftWidth;
    }
    public void setBorderRightWidth(int borderRightWidth) {
        this.borderRightWidth = borderRightWidth;
    }
    public void setBorderTopWidth(int borderTopWidth) {
        this.borderTopWidth = borderTopWidth;
    }
    public void setBorderBottomWidth(int borderBottomWidth) {
        this.borderBottomWidth = borderBottomWidth;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view);
        int[] rect = getItemOffsetRect(position, state.getItemCount());
        outRect.set(rect[0], rect[1], rect[2], rect[3]);
    }
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int itemCount = parent.getChildCount();
        int left, right, top, bottom;
        for(int i = 0; i < itemCount; i++) {
            View item = parent.getChildAt(i);
            RecyclerView.LayoutParams itemLayoutParams = (RecyclerView.LayoutParams) item.getLayoutParams();
            int itemOffsets[] = getItemOffsetRect(i, itemCount);
            // 控件左侧绘制
            right = item.getLeft() - itemLayoutParams.leftMargin;
            top = item.getTop() - itemLayoutParams.topMargin;
            bottom = item.getBottom() + itemLayoutParams.bottomMargin;
            left = itemOffsets[0];
            spacingDrawable.setBounds(left, top, right, bottom);
            spacingDrawable.draw(c);
            // 控件顶部绘制
            left = item.getLeft() - itemLayoutParams.leftMargin;
            right = item.getRight() + itemLayoutParams.rightMargin;
            bottom = item.getTop() + itemLayoutParams.topMargin;
            top = bottom - itemOffsets[1];
            spacingDrawable.setBounds(left, top, right, bottom);
            spacingDrawable.draw(c);
            // 控件右侧绘制
            left = item.getRight() + itemLayoutParams.rightMargin;
            top = item.getTop() - itemLayoutParams.topMargin;
            right = left + itemOffsets[2];
            bottom = item.getBottom() + itemLayoutParams.bottomMargin;
            spacingDrawable.setBounds(left, top, right, bottom);
            spacingDrawable.draw(c);
            // 控件底部绘制
            left = item.getLeft() - itemLayoutParams.leftMargin;
            right = item.getRight() + itemLayoutParams.rightMargin;
            top = item.getBottom() + itemLayoutParams.bottomMargin;
            bottom = top + itemOffsets[3];
            spacingDrawable.setBounds(left, top, right, bottom);
            spacingDrawable.draw(c);
        }
    }
    private int[] getItemOffsetRect(int position, int itemCount) {
        if (orientation == VERTICAL) {
            return offsetVerticalOrientation(position, itemCount);
        }
        else {
            return offsetHorizontalOrientation(position, itemCount);
        }
    }
    /**
     * 纵向边距计算
     */
    private int[] offsetVerticalOrientation(int position, int itemCount) {
        int left, top, right, bottom;
        int lastRowStartIndex = itemCount - spanCount + itemCount % spanCount;
        // 只有一列
        if (spanCount == 1) {
            left = borderLeftWidth >= 0 ? borderLeftWidth : spacingSize;
            right = borderRightWidth >= 0 ? borderRightWidth : spacingSize;
        }
        // 纵向第一列
        else if(position % spanCount == 0) {
            left = borderLeftWidth >= 0 ? borderLeftWidth : spacingSize;
            if (spanCount == 2) {
                right = (int) (spacingSize * 1.0f / 2);
            }
            else {
                right = (int) (spacingSize * 1.0f / 3);
            }
        }
        // 纵向最后一列
        else if ((position + 1) % spanCount == 0) {
            if (spanCount == 2) {
                left = (int) (spacingSize * 1.0f / 2);
            }
            else {
                left = (int) (spacingSize * 1.0f / 3);
            }
            right = borderRightWidth >= 0 ? borderRightWidth : spacingSize;
        }
        else {
            left = right = (int) (spacingSize * 2.0f / 3);
        }
        // 纵向第一行
        if(position < spanCount) {
            top = borderTopWidth >= 0 ? borderTopWidth : spacingSize;
            bottom = (int) (spacingSize * 1.0f / 2);
        }
        // 纵向最后一行
        else if (position >= lastRowStartIndex) {
            top = (int) (spacingSize * 1.0f / 2);
            bottom = borderBottomWidth >= 0 ? borderBottomWidth : spacingSize;
        }
        else {
            top = bottom = (int) (spacingSize * 1.0f / 2);
        }
        return new int[]{left, top, right, bottom};
    }
    /**
     * 横向边距计算
     */
    private int[] offsetHorizontalOrientation(int position, int itemCount) {
        int left, top, right, bottom;
        int lastColumnStartIndex = itemCount - spanCount + itemCount % spanCount;
        // 只有一行
        if (spanCount == 1) {
            top = borderTopWidth >= 0 ? borderTopWidth : spacingSize;
            bottom = borderBottomWidth >= 0 ? borderBottomWidth : spacingSize;
        }
        // 横向第一行
        else if(position % spanCount == 0) {
            top = borderTopWidth >= 0 ? borderTopWidth : spacingSize;
            if (spanCount == 2) {
                bottom = (int) (spacingSize * 1.0f / 2);
            }
            else {
                bottom = (int) (spacingSize * 1.0f / 3);
            }
        }
        else if((position + 1) % spanCount == 0) {
            if (spanCount == 2) {
                top = (int) (spacingSize * 1.0f / 2);
            }
            else {
                top = (int) (spacingSize * 1.0f / 3);
            }
            bottom = borderBottomWidth >= 0 ? borderBottomWidth : spacingSize;
        }
        else {
            top = bottom = (int) (spacingSize * 2.0f / 3);
        }
        // 横向第一列
        if (position < spanCount) {
            left = borderLeftWidth >= 0 ? borderLeftWidth : spacingSize;
            right = (int) (spacingSize * 1.0f / 2);
        }
        // 横向最后一列
        else if (position >= lastColumnStartIndex) {
            left = (int) (spacingSize * 1.0f / 2);
            right = borderRightWidth >= 0 ? borderRightWidth : spacingSize;
        }
        else {
            left = right = (int) (spacingSize * 1.0f / 2);
        }
        return new int[]{left, top, right, bottom};
    }
}