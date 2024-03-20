package com.linxiao.framework.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.NinePatch
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

/**
 * btimap 工具类
 *
 * @author lx8421bcd
 * @since 2016-07-30.
 */
object BitmapUtil {
    fun getBitmap(filePath: String?): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    /**
     * 通过URI加载图片
     *
     * @param context
     * @param uri     图片URI
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getBitmap(context: Context, uri: Uri?): Bitmap? {
        return getScaledBitmap(context, uri, Bitmap.Config.ARGB_8888, Int.MAX_VALUE, Int.MAX_VALUE)
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
    @Throws(IOException::class)
    fun getScaledBitmap(
        context: Context,
        uri: Uri?,
        config: Bitmap.Config?,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap? {
        val maxW = if (maxWidth <= 0) Int.MAX_VALUE else maxWidth
        val maxH = if (maxHeight <= 0) Int.MAX_VALUE else maxHeight
        val cr = context.contentResolver
        val opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = true // 设置为true, 加载器不会返回图片,
        // 而是设置Options对象中以out开头的字段.即仅仅解码边缘区域
        var inputStream = cr.openInputStream(uri!!)
        BitmapFactory.decodeStream(inputStream, null, opts)
        // 得到图片的宽和高
        var imageWidth = opts.outWidth
        var imageHeight = opts.outHeight
        // 计算缩放比例
        var scale = 1
        if (imageWidth > maxW || imageHeight > maxH) {
            val widthScale = imageWidth / maxW
            val heightScale = imageHeight / maxH
            scale = max(widthScale.toDouble(), heightScale.toDouble()).toInt()
        }
        // 指定加载可以加载出图片.
        opts.inJustDecodeBounds = false
        // 使用计算出来的比例进行缩放(这里的缩放只会以2的幂次方缩放)
        opts.inSampleSize = floor(ln(scale.toDouble()) / ln(2.0))
            .toInt()
        opts.inPreferredConfig = config
        inputStream = cr.openInputStream(uri)
        val ret = BitmapFactory.decodeStream(inputStream, null, opts) ?: return null
        // 以上的绽放不精确,以下缩放到指定的大小
        imageWidth = ret.getWidth()
        imageHeight = ret.getHeight()
        if (imageWidth > maxW || imageHeight > maxH) {
            val widthScale = 1.0f * maxW / imageWidth
            val heightScale = 1.0f * maxH / imageHeight
            val fScale = min(widthScale.toDouble(), heightScale.toDouble())
                .toFloat()
            val dstWidth = (imageWidth * fScale).toInt()
            val dstHeight = (imageHeight * fScale).toInt()
            return Bitmap.createScaledBitmap(ret, dstWidth, dstHeight, false)
        }
        return ret
    }

    fun getBitmapOptions(filePath: String?): BitmapFactory.Options {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true // 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
        BitmapFactory.decodeFile(filePath, options)
        return options
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun bitmapToBase64(bitmap: Bitmap?): String? {
        if (bitmap == null) {
            return null
        }
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        baos.flush()
        baos.close()
        val bitmapBytes = baos.toByteArray()
        return Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    fun base64ToBitmap(base64Data: String?): Bitmap {
        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun getBitmapFromUrl(imageUri: String?): Bitmap? {
        // 显示网络上的图片
        var bitmap: Bitmap?
        try {
            val myFileUrl = URL(imageUri)
            val conn = myFileUrl.openConnection() as HttpURLConnection
            conn.setDoInput(true)
            conn.connect()
            val inputStream = conn.inputStream
            bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            bitmap = null
        } catch (e: IOException) {
            e.printStackTrace()
            bitmap = null
        }
        return bitmap
    }

    /**
     * 截取View的bitmap快照
     */
    fun convertViewToBitmap(view: View?): Bitmap? {
        if (view == null) {
            return null
        }
        view.setLayoutParams(
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
        )
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildDrawingCache()
        return view.drawingCache
    }

    /**
     * 创建圆形头像
     *
     * @param img
     * @return
     */
    fun createRoundIcon(img: Bitmap?): Bitmap? {
        if (img == null) {
            return null
        }
        // 原图大小
        val imgWidth = img.getWidth()
        val imgHeight = img.getHeight()
        // 生成圆形图像大小
        val imgSize = min(imgWidth.toDouble(), imgHeight.toDouble()).toInt()
        // 绘制图片的起始位置
        val left = (imgSize - imgWidth) / 2.0f
        val top = (imgSize - imgHeight) / 2.0f
        // 填充边距
        val padding = 2
        // 图像半径
        val r = imgSize / 2

        // 1.创建新图
        val ret = Bitmap.createBitmap(imgSize, imgSize, Bitmap.Config.ARGB_8888)
        val c = Canvas(ret)
        val p = Paint()
        p.isAntiAlias = true

        // 2.绘制圆形头像
        p.setColor(Color.WHITE)
        c.drawCircle(r.toFloat(), r.toFloat(), (r - padding).toFloat(), p)
        p.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        c.drawBitmap(img, left, top, p)

        // 3.绘制边框
        p.setXfermode(PorterDuffXfermode(PorterDuff.Mode.DST_OVER))
        c.drawCircle(r.toFloat(), r.toFloat(), r.toFloat(), p)
        return ret
    }

    /**
     * 根据设定尺寸压缩图片
     */
    fun compressBySize(pathName: String?, targetWidth: Int, targetHeight: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true // 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
        BitmapFactory.decodeFile(pathName, options)

        // 得到图片的宽度、高度；
        val imgWidth = options.outWidth.toFloat()
        val imgHeight = options.outHeight.toFloat()
        var compressW = targetWidth
        var compressH = targetHeight
        // 处理横竖问题
        if (imgWidth > imgHeight && targetWidth < targetHeight) {
            compressW = targetHeight
            compressH = targetWidth
        } else if (imgWidth < imgHeight && targetWidth > targetHeight) {
            compressW = targetHeight
            compressH = targetWidth
        }

        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于等于该比例的最小整数；
        val widthRatio = ceil((imgWidth / compressW.toFloat()).toDouble()).toInt()
        val heightRatio = ceil((imgHeight / compressH.toFloat()).toDouble())
            .toInt()
        options.inSampleSize = 1

        // 如果尺寸接近则不压缩，否则进行比例压缩
        if (widthRatio > 1 || heightRatio > 1) {
            options.inSampleSize = max(widthRatio.toDouble(), heightRatio.toDouble()).toInt()
        }

        //设置好缩放比例后，加载图片进内容；
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(pathName, options)
    }

    /**
     * 设定限制大小采用缩放法压缩图片，单位 B
     *
     * 每次压缩，等比例缩小图片直到bitmap小于限制大小
     */
    fun matrixCompressByLimit(bitmap: Bitmap?, quality: Int, limitSize: Int): Bitmap? {
        var ret = bitmap ?: return null
        while (getBitmapSize(ret, quality) > limitSize) {
            val matrix = Matrix()
            matrix.setScale(0.9f, 0.9f)
            ret = Bitmap.createBitmap(
                ret, 0, 0,
                ret.getWidth(),
                ret.getHeight(),
                matrix,
                true
            )
            Log.d(
                "matrixCompressByLimit",
                " compressedSize: " + getBitmapSize(ret) +
                        " compressedW = " + ret.getWidth() +
                        " compressedH = " + ret.getHeight()
            )
        }
        return ret
    }

    /**
     * 设定限制大小压缩图片，单位 B
     *
     * 注意，此方法只压缩了图片在磁盘上的存储空间，直接输出的bitmap大小不会变化
     */
    fun compressDiskSizeByLimit(bitmap: Bitmap?, limitSize: Int): Bitmap? {
        if (bitmap == null) {
            return null
        }
        val baos = ByteArrayOutputStream()
        var options = 100
        do {
            baos.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos)
            Log.d("compressDiskSizeByLimit", "options: $options")
            options--
            Log.d("compressDiskSizeByLimit", "baos.size : " + baos.toByteArray().size)
        } while (baos.toByteArray().size > limitSize && options > 0)
        val compressedData = baos.toByteArray()
        try {
            baos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return BitmapFactory.decodeByteArray(compressedData, 0, compressedData.size)
    }

    fun getBitmapSize(bitmap: Bitmap): Long {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val imageInByte = stream.toByteArray()
        val len = imageInByte.size.toLong()
        try {
            stream.flush()
            stream.close()
        } catch (ignored: IOException) {
        }
        return len
    }

    fun getBitmapSize(bitmap: Bitmap?, quality: Int): Long {
        val stream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        val imageInByte = stream.toByteArray()
        val len = imageInByte.size.toLong()
        try {
            stream.flush()
            stream.close()
        } catch (ignored: IOException) {
        }
        return len
    }

    fun getCompressQuality(bitmap: Bitmap, fileSize: Long): Int {
        var quality = 100
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        while (stream.toByteArray().size > fileSize && quality > 10) {
            stream.reset()
            quality -= 2
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            Log.d("ImageUtil", "quality: $quality")
        }
        try {
            stream.flush()
            stream.close()
        } catch (ignored: IOException) {
        }
        return quality
    }

    /**
     * 加载图片到View
     *
     * @param url  图片url
     * @param view View
     */
    fun loadUrlImageInto(context: Context, url: String, view: View) {
        try {
            val bitmap: Bitmap = if (url.startsWith("file:///android_asset/")) {
                BitmapFactory.decodeStream(
                    context.assets.open(url.substring("file:///android_asset/".length))
                )
            } else if (url.startsWith("file://")) {
                BitmapFactory.decodeFile(url.substring("file://".length))
            } else {
                BitmapFactory.decodeStream(
                    context.contentResolver.openInputStream(Uri.parse(url))
                )
            }
            val chunk = bitmap.ninePatchChunk
            val result = NinePatch.isNinePatchChunk(chunk)
            val drawable: Drawable = if (result) {
                NinePatchDrawable(null, bitmap, chunk, Rect(), null)
            } else {
                BitmapDrawable(null, bitmap)
            }
            if (view is ImageView) {
                view.setImageDrawable(drawable)
            } else {
                view.background = drawable
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
