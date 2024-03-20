package com.linxiao.framework.common;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.os.Build.VERSION_CODES.KITKAT;

/**
 * btimap 工具类
 *
 * @author lx8421bcd
 * @since 2016-07-30.
 */
public class BitmapUtil {


    public static Bitmap getBitmap(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 通过URI加载图片
     *
     * @param context
     * @param uri     图片URI
     * @return
     * @throws IOException
     */
    public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
        return getScaledBitmap(context, uri, Bitmap.Config.ARGB_8888, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * 获取指定最大尺寸的图片
     *
     * @param context
     * @param uri     图片URI
     * @param config
     * @return
     * @throws IOException
     */
    public static Bitmap getScaledBitmap(Context context, Uri uri, Bitmap.Config config, int maxWidth, int maxHeight) throws IOException {
        maxWidth = maxWidth <= 0 ? Integer.MAX_VALUE : maxWidth;
        maxHeight = maxHeight <= 0 ? Integer.MAX_VALUE : maxHeight;
        ContentResolver cr = context.getContentResolver();
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true; // 设置为true, 加载器不会返回图片,
        // 而是设置Options对象中以out开头的字段.即仅仅解码边缘区域
        InputStream is = cr.openInputStream(uri);
        BitmapFactory.decodeStream(is, null, opts);
        // 得到图片的宽和高
        int imageWidth = opts.outWidth;
        int imageHeight = opts.outHeight;
        // 计算缩放比例
        int scale = 1;
        if (imageWidth > maxWidth || imageHeight > maxHeight) {
            int widthScale = imageWidth / maxWidth;
            int heightScale = imageHeight / maxHeight;
            scale = Math.max(widthScale, heightScale);
        }
        // 指定加载可以加载出图片.
        opts.inJustDecodeBounds = false;
        // 使用计算出来的比例进行缩放(这里的缩放只会以2的幂次方缩放)
        opts.inSampleSize = (int) Math.floor(Math.log(scale) / Math.log(2));
        opts.inPreferredConfig = config;
        is = cr.openInputStream(uri);
        Bitmap ret = BitmapFactory.decodeStream(is, null, opts);
        if (ret == null) {
            return null;
        }
        // 以上的绽放不精确,以下缩放到指定的大小
        imageWidth = ret.getWidth();
        imageHeight = ret.getHeight();
        if (imageWidth > maxWidth || imageHeight > maxHeight) {
            float widthScale = 1.0F * maxWidth / imageWidth;
            float heightScale = 1.0F * maxHeight / imageHeight;
            float fScale = Math.min(widthScale, heightScale);
            int dstWidth = (int) (imageWidth * fScale);
            int dstHeight = (int) (imageHeight * fScale);
            return Bitmap.createScaledBitmap(ret, dstWidth, dstHeight, false);
        }
        return ret;
    }


    public static BitmapFactory.Options getBitmapOptions(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
        BitmapFactory.decodeFile(filePath, options);
        return options;
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     * @throws IOException
     */
    public static String bitmapToBase64(Bitmap bitmap) throws IOException {
        if (bitmap == null) {
            return null;
        }
        String result = null;
        ByteArrayOutputStream baos = null;
        baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        baos.flush();
        baos.close();
        byte[] bitmapBytes = baos.toByteArray();
        result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Bitmap getbitmap(String imageUri) {
        // 显示网络上的图片
        Bitmap bitmap = null;
        try {
            URL myFileUrl = new URL(imageUri);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap = null;
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    /**
     * 截取View的bitmap快照
     * */
    public static Bitmap convertViewToBitmap(View view) {
        if (view == null) {
            return null;
        }
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    /**
     * 创建圆形头像
     *
     * @param img
     * @return
     */
    public static Bitmap createRoundIcon(Bitmap img) {
        if (img == null) {
            return null;
        }
        // 原图大小
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        // 生成圆形图像大小
        int imgSize = Math.min(imgWidth, imgHeight);
        // 绘制图片的起始位置
        float left = (imgSize - imgWidth) / 2.0F;
        float right = (imgSize - imgHeight) / 2.0F;
        // 填充边距
        int padding = 2;
        // 图像半径
        int r = imgSize / 2;

        // 1.创建新图
        Bitmap ret = Bitmap.createBitmap(imgSize, imgSize, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(ret);
        Paint p = new Paint();
        p.setAntiAlias(true);

        // 2.绘制圆形头像
        p.setColor(Color.WHITE);
        c.drawCircle(r, r, r - padding, p);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        c.drawBitmap(img, left, right, p);

        // 3.绘制边框
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        c.drawCircle(r, r, r, p);

        return ret;
    }

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
        if (widthRatio > 1 || heightRatio > 1) {
            options.inSampleSize = Math.max(widthRatio, heightRatio);
        }

        //设置好缩放比例后，加载图片进内容；
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    /**
     * 设定限制大小采用缩放法压缩图片，单位 B
     * <p>每次压缩，等比例缩小图片直到bitmap小于限制大小</p>
     * */
    public static Bitmap matrixCompressByLimit(Bitmap bitmap, int quality, int limitSize) {
        if (bitmap == null) {
            return null;
        }
        while(getBitmapSize(bitmap, quality) > limitSize) {
            Matrix matrix = new Matrix();
            matrix.setScale(0.9f, 0.9f);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    matrix,
                    true);
            Log.d("matrixCompressByLimit",
                    " compressedSize: " + getBitmapSize(bitmap) +
                            " compressedW = " + bitmap.getWidth() +
                            " compressedH = " + bitmap.getHeight()
            );
        };
        return bitmap;
    }

    /**
     * 设定限制大小压缩图片，单位 B
     * <p>注意，此方法只压缩了图片在磁盘上的存储空间，直接输出的bitmap大小不会变化</p>
     * */
    public static Bitmap compressDiskSizeByLimit(Bitmap bitmap, int limitSize) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        do {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            Log.d("compressDiskSizeByLimit", "options: " + options);
            options--;
            Log.d("compressDiskSizeByLimit", "baos.size : " + baos.toByteArray().length);
        } while (baos.toByteArray().length > limitSize && options > 0);

        byte[] compressedData = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeByteArray(compressedData, 0, compressedData.length);
    }

    public static long getBitmapSize(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] imageInByte = stream.toByteArray();
        long len = imageInByte.length;
        try {
            stream.flush();
            stream.close();
        } catch (IOException ignored) {}
        return len;
    }

    public static long getBitmapSize(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        byte[] imageInByte = stream.toByteArray();
        long len = imageInByte.length;
        try {
            stream.flush();
            stream.close();
        } catch (IOException ignored) {}
        return len;
    }

    public static int getCompressQuality(Bitmap bitmap, long fileSize) {
        int quality = 100;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        while(stream.toByteArray().length > fileSize && quality > 10) {
            stream.reset();
            quality -= 2;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
            Log.d("ImageUtil", "quality: " + quality);
        }
        try {
            stream.flush();
            stream.close();
        } catch (IOException ignored) {}
        return quality;
    }

    /**
     * 加载图片到View
     *
     * @param url  图片url
     * @param view View
     */
    public static void loadUrlImageInto(Context context, String url, View view) {
        try {
            Bitmap bitmap;
            if(url.startsWith("file:///android_asset/")) {
                bitmap = BitmapFactory.decodeStream(
                        context.getAssets().open(url.substring("file:///android_asset/".length())));
            } else if (url.startsWith("file://")) {
                bitmap = BitmapFactory.decodeFile(url.substring("file://".length()));
            } else {
                bitmap = BitmapFactory.decodeStream(
                        context.getContentResolver().openInputStream(Uri.parse(url)));
            }
            byte[] chunk = bitmap.getNinePatchChunk();
            boolean result = NinePatch.isNinePatchChunk(chunk);
            Drawable drawable;
            if (result) {
                drawable = new NinePatchDrawable(bitmap, chunk, new Rect(), null);
            } else {
                drawable = new BitmapDrawable(bitmap);
            }
            if (view instanceof ImageView) {
                ((ImageView) view).setImageDrawable(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
