package com.linxiao.framework.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import java.io.ByteArrayOutputStream;

import static android.os.Build.VERSION_CODES.KITKAT;

/**
 * btimap 工具类
 *
 * Created by linxiao on 2016/7/30.
 */
public class BitmapUtil {
    
    /**
     * 根据设定尺寸压缩图片
     * */
    public static Bitmap compressBySize(String pathName, int targetWidth, int targetHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
        BitmapFactory.decodeFile(pathName, options);
        
        // 得到图片的宽度、高度；
        float imgWidth = options.outWidth;
        float imgHeight = options.outHeight;
        
        int compressW = targetWidth;
        int compressH = targetHeight;
        // 处理横竖问题
        if (imgWidth > imgHeight && targetWidth < targetHeight) {
            compressW = targetHeight;
            compressH = targetWidth;
        }
        else if (imgWidth < imgHeight && targetWidth > targetHeight) {
            compressW = targetHeight;
            compressH = targetWidth;
        }
        
        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于等于该比例的最小整数；
        int widthRatio = (int) Math.ceil(imgWidth / (float) compressW);
        int heightRatio = (int) Math.ceil(imgHeight / (float) compressH);
        options.inSampleSize = 1;
        
        // 如果尺寸接近则不压缩，否则进行比例压缩
        if (widthRatio > 1 || widthRatio > 1) {
            if (widthRatio > heightRatio) {
                options.inSampleSize = widthRatio;
            } else {
                options.inSampleSize = heightRatio;
            }
        }
        
        //设置好缩放比例后，加载图片进内容；
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }
    
    /**
     * 设定限制大小压缩图片，单位 B
     * <p>此方法将会降低图像质量</p>
     * */
    public static Bitmap compressByLimit(Bitmap bitmap, int limitSize) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.size() > limitSize && options > 0) {
            options--;
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        byte[] compressedData = baos.toByteArray();
        return BitmapFactory.decodeByteArray(compressedData, 0, compressedData.length);
    }
    
    /**
     * 获取Bitmap 大小，单位 B
     * */
    public static int getBitmapSize(Bitmap bitmap) {
        
        // From KitKat onward use getAllocationByteCount() as allocated bytes can potentially be
        // larger than bitmap byte count.
        if (Build.VERSION.SDK_INT >= KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        // pre kitkat
        return bitmap.getByteCount();
    }
}
