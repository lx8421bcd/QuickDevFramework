package com.linxiao.framework.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.linxiao.framework.R;

/**
 * 自定义评星控件
 *
 * @author linxiao
 * @version 1.0
 * Create on 2018-06-07
 */
public class CustomRatingBar extends LinearLayout {
    private static final String TAG = CustomRatingBar.class.getSimpleName();
    
    public interface OnRatingChangeListener {
        
        void onRatingChange(double rating);
    }
    
    private boolean mClickable;
    private boolean showHalfStar;
    private boolean showPercent;
    private int starCount;
    
    private int starWidth = dip2px(24);
    private int starHeight = dip2px(24);
    private int starPaddingBoth;
    
    private Drawable starEmptyDrawable;
    private Drawable starFillDrawable;
    private Drawable starHalfDrawable;
    
    private OnRatingChangeListener onRatingChangeListener;
    
    private double mRating = 0;
    
    private OnClickListener onStarClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = -1;
            for (int i = 0; i < getChildCount(); i++) {
                if (getChildAt(i) == v) {
                    index = i;
                }
            }
            if (index < 0) {
                return;
            }
            int rating = index + 1;
            if (rating != (int) mRating) {
                setRating(rating);
                if (onRatingChangeListener != null) {
                    onRatingChangeListener.onRatingChange(mRating);
                }
            }
        }
    };
    
    public CustomRatingBar(Context context) {
        super(context, null);
    }
    
    public CustomRatingBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public CustomRatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }
    
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomRatingBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
        if (attrs != null) {
            TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomRatingBar);
            starEmptyDrawable = mTypedArray.getDrawable(R.styleable.CustomRatingBar_emptyImage);
            starHalfDrawable = mTypedArray.getDrawable(R.styleable.CustomRatingBar_halfImage);
            starFillDrawable = mTypedArray.getDrawable(R.styleable.CustomRatingBar_filledImage);
            starWidth = mTypedArray.getDimensionPixelOffset(R.styleable.CustomRatingBar_starWidth, dip2px(24));
            starHeight = mTypedArray.getDimensionPixelOffset(R.styleable.CustomRatingBar_starHeight, dip2px(24));
            starPaddingBoth = mTypedArray.getDimensionPixelOffset(R.styleable.CustomRatingBar_starPadding, dip2px(2));
            starCount = mTypedArray.getInteger(R.styleable.CustomRatingBar_starCount, 5);
            mRating = mTypedArray.getFloat(R.styleable.CustomRatingBar_rating, 0);
            mClickable = mTypedArray.getBoolean(R.styleable.CustomRatingBar_starClickable, false);
            showHalfStar = mTypedArray.getBoolean(R.styleable.CustomRatingBar_showHalf, false);
            showPercent = mTypedArray.getBoolean(R.styleable.CustomRatingBar_showPercent, true);
            mTypedArray.recycle();
        }
        if (starEmptyDrawable == null) {
            starEmptyDrawable = ContextCompat.getDrawable(context, R.drawable.ic_rating_empty);
        }
        if (starFillDrawable == null) {
            starFillDrawable = ContextCompat.getDrawable(context, R.drawable.ic_rating_filled);
        }
        
        initStarImageViews();
    }
    
    private void initStarImageViews() {
        removeAllViews();
        for (int i = 0; i < starCount; ++i) {
            ImageView imageView = new ImageView(getContext());
            LayoutParams param = new LayoutParams(starWidth, starHeight, 1.0f);
            imageView.setLayoutParams(param);
            imageView.setPadding(starPaddingBoth, 0, starPaddingBoth, 0);
            imageView.setOnClickListener(onStarClick);
            imageView.setEnabled(mClickable);
            imageView.setImageDrawable(starEmptyDrawable);
            addView(imageView);
        }
        setRating(mRating);
    }
    
    public double getRating() {
        return mRating;
    }
    
    public void setRating(double rating) {
        rating = ((int)(rating * 10)) * 1.0 / 10;
        for (int i = 0; i < starCount; ++i) {
            ImageView ivStar = (ImageView) getChildAt(i);
            double starIndex = i + 1;
            if (starIndex - rating <= 0) {
                ivStar.setImageDrawable(starFillDrawable);
            }
            else if (starIndex - rating < 1) {
                ivStar.setImageDrawable(getLastStarDrawable(rating));
            }
            else {
                ivStar.setImageDrawable(starEmptyDrawable);
            }
        }
        this.mRating = rating;
    }
    
    private Drawable getLastStarDrawable(double rating) {
        // decimal part of rating, used to decide show half star or not
        double decimalPart = rating - (int) rating;
        
        // neither show percentage star image nor half style image, treat decimal part as 0
        if (!showPercent && !showHalfStar) {
            return starEmptyDrawable;
        }
        // if only enabled show half style image, treat decimal part lower than 0.7 as half star
        if (!showPercent && starHalfDrawable != null) {
            if (decimalPart > 0.7) {
                return starFillDrawable;
            }
            else {
                return starHalfDrawable;
            }
        }
        /*
         if both show percentage and show half style is enabled,
         use half style image as background layer first if it is not null
         */
        Bitmap backLayer;
        if (starHalfDrawable != null) {
            backLayer = drawableToBitmap(starHalfDrawable);
        }
        else {
            backLayer = drawableToBitmap(starEmptyDrawable);
        }
        
        Bitmap frontLayer = drawableToBitmap(starFillDrawable);
        
        int frontWidth = (int) (frontLayer.getWidth() * decimalPart);
        frontLayer = Bitmap.createBitmap(frontLayer, 0, 0, frontWidth, frontLayer.getHeight());
        
        Bitmap bitmap = backLayer.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Rect baseRect  = new Rect(0, 0, backLayer.getWidth(), backLayer.getHeight());
        Rect frontRect = new Rect(0, 0, backLayer.getHeight(), frontLayer.getHeight());
        canvas.drawBitmap(frontLayer, frontRect, baseRect, null);
        return new BitmapDrawable(bitmap);
    }
    
    private static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }
    
    public void setStarHalfDrawable(Drawable starHalfDrawable) {
        this.starHalfDrawable = starHalfDrawable;
    }
    
    public void setOnRatingChangeListener(OnRatingChangeListener onRatingChangeListener) {
        this.onRatingChangeListener = onRatingChangeListener;
    }

    public void setStarClickable(boolean clickable) {
        this.mClickable = clickable;
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setEnabled(clickable);
        }
    }

    public void showHalfStar(boolean show) {
        this.showHalfStar = show;
        setRating(mRating);
    }
    
    public void setShowPercent(boolean showPercent) {
        this.showPercent = showPercent;
        setRating(mRating);
    }
    
    public void setStarImage(Drawable empty, Drawable half, Drawable filled) {
        if (empty != null) {
            starEmptyDrawable = empty;
        }
        if (half != null) {
            starHalfDrawable = half;
        }
        if (filled != null) {
            starFillDrawable = filled;
        }
        setRating(mRating);
    }
    
    public void setStarImage(Drawable empty, Drawable filled) {
        if (empty != null) {
            starEmptyDrawable = empty;
        }
        if (filled != null) {
            starFillDrawable = filled;
        }
        setRating(mRating);
    }

    public void setStarWidth(int starWidth) {
        if (this.starWidth == starWidth) {
            return;
        }
        this.starWidth = starWidth;
        for (int i = 0; i < getChildCount(); i++) {
            ImageView ivStar = (ImageView) getChildAt(i);
            ivStar.getLayoutParams().width = starWidth;
        }
        requestLayout();
    }

    public void setStarHeight(int starHeight) {
        if (this.starHeight == starHeight) {
            return;
        }
        this.starHeight = starHeight;
        for (int i = 0; i < getChildCount(); i++) {
            ImageView ivStar = (ImageView) getChildAt(i);
            ivStar.getLayoutParams().height = starHeight;
        }
        requestLayout();
    }


    public void setStarCount(int starCount) {
        if (this.starCount == starCount) {
            return;
        }
        this.starCount = starCount;
        initStarImageViews();
    }

    public void setStarPadding(int padding) {
        this.starPaddingBoth = padding;
        for (int i = 0; i < getChildCount(); i++) {
            ImageView ivStar = (ImageView) getChildAt(i);
            ivStar.setPadding(starPaddingBoth, 0, starPaddingBoth, 0);
        }
    }
    
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    
    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    private int px2dip(float pxValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
