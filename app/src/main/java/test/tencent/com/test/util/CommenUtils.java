package test.tencent.com.test.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


/**
 * 共用的工具
 * @author leaf
 *
 */
public class CommenUtils {

    public static Bitmap getRatioBitmap(Context context, int imgId, int reqWidth, int reqHeight) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), imgId, newOpts);
        newOpts.inSampleSize = calculateInSampleSize(newOpts, reqWidth, reqHeight);

        newOpts.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(), imgId, newOpts);

    }

    //这里还是给一个比例缩放的
    public static Bitmap createScaleBitmap(Bitmap src, int dstWidth ,boolean needRecycle) {
        // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false
        float scale = dstWidth * 1f /src.getWidth();
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, (int) (scale * src.getHeight()), false);
        if (src != dst && needRecycle) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize >= reqHeight) && (halfWidth / inSampleSize >= reqWidth)) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;

    }

    /**
     * 转化成灰度图
     * @param bmpOriginal
     * @return
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {

        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    /**
     * Bitmap压缩
     * @param bm
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap compress(final Bitmap bm, int reqWidth, int reqHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        if (height > reqHeight || width > reqWidth) {
            float scaleWidth = (float) reqWidth / width;
            float scaleHeight = (float) reqHeight / height;
            float scale = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap result = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true);
            bm.recycle();
            return result;
        }
        return bm;
    }
}
