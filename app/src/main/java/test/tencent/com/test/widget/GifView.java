package test.tencent.com.test.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.FileDescriptor;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import test.tencent.com.test.R;
import test.tencent.com.test.util.Utils;

import static test.tencent.com.test.util.Utils.canUseForInBitmap;

/**
 * Created by brzhang on 16/12/18.
 * Description :
 */

public class GifView extends GLSurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "GifView";

    private long MAX_BITMAP_POOL_SIZE;//最大size，避免oom发生

    private final SurfaceHolder mHolder;
    private long mFrameSpaceTime = 2;  // 每帧图片的间隔时间
    private int mFirstLoadFrame = 1;   //首次加载几张
    private boolean mCanDraw = true;  // 画图开关
    private boolean isViewDetetch = false;//是否销毁
    private BitmapDrawable mCurrentdrawable;
    private int[] resIds;
    private boolean needRepeat = true;//自动重复

    private ConcurrentLinkedQueue<Bitmap> mBitmapQueue = new ConcurrentLinkedQueue<>();
    private AtomicInteger mLoadIndex;
    private volatile long mCurrentSize;//当前大小
    private AtomicBoolean isFrameLoadComplete = new AtomicBoolean(false);

    private long mbeginTime = 0;


    private Set<Bitmap> mReusableBitmaps;

    public static int[] castles = new int[]{
            R.mipmap.castle_1,
            R.mipmap.castle_2,
            R.mipmap.castle_3,
            R.mipmap.castle_4,
            R.mipmap.castle_5,
            R.mipmap.castle_6,
            R.mipmap.castle_7,
            R.mipmap.castle_8,
            R.mipmap.castle_9,
            R.mipmap.castle_10,
            R.mipmap.castle_11,
            R.mipmap.castle_12,
            R.mipmap.castle_13,
            R.mipmap.castle_14,
            R.mipmap.castle_15,
            R.mipmap.castle_16,
            R.mipmap.castle_17,
            R.mipmap.castle_18,
            R.mipmap.castle_19,
            R.mipmap.castle_20,
            R.mipmap.castle_21,
            R.mipmap.castle_22,
            R.mipmap.castle_23,
            R.mipmap.castle_24,
            R.mipmap.castle_25,
            R.mipmap.castle_26,
            R.mipmap.castle_27,
            R.mipmap.castle_28,
            R.mipmap.castle_29,
            R.mipmap.castle_30,
            R.mipmap.castle_31,
            R.mipmap.castle_32,
            R.mipmap.castle_33,
            R.mipmap.castle_34,
            R.mipmap.castle_35,
            R.mipmap.castle_36,
            R.mipmap.castle_37,
            R.mipmap.castle_38,
            R.mipmap.castle_39,
            R.mipmap.castle_40,
            R.mipmap.castle_41,
            R.mipmap.castle_42,
            R.mipmap.castle_43,
            R.mipmap.castle_44,
            R.mipmap.castle_45,
            R.mipmap.castle_46,
            R.mipmap.castle_47,
            R.mipmap.castle_48,
            R.mipmap.castle_49,
            R.mipmap.castle_50,
            R.mipmap.castle_51,
            R.mipmap.castle_52,
            R.mipmap.castle_53,
            R.mipmap.castle_54,
            R.mipmap.castle_55,
            R.mipmap.castle_56,
            R.mipmap.castle_57,
            R.mipmap.castle_58,
            R.mipmap.castle_59,
            R.mipmap.castle_60,
            R.mipmap.castle_61,
            R.mipmap.castle_62,
            R.mipmap.castle_63,
            R.mipmap.castle_64,
            R.mipmap.castle_65,
            R.mipmap.castle_66,
            R.mipmap.castle_67,
            R.mipmap.castle_68,
            R.mipmap.castle_69,
            R.mipmap.castle_70,
            R.mipmap.castle_71,
            R.mipmap.castle_72,
            R.mipmap.castle_73,
            R.mipmap.castle_74,
            R.mipmap.castle_75,
            R.mipmap.castle_76,
            R.mipmap.castle_77,
            R.mipmap.castle_78,
            R.mipmap.castle_79,
            R.mipmap.castle_80,
            R.mipmap.castle_81,
            R.mipmap.castle_82,
            R.mipmap.castle_83,
            R.mipmap.castle_84,
            R.mipmap.castle_85,
            R.mipmap.castle_86,
            R.mipmap.castle_87
    };

    public GifView(Context context) {
        this(context, null);
    }

    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = this.getHolder();       // 获得surfaceholder
        mHolder.addCallback(this);        // 添加回调，这样三个方法才会执行

        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);// 设置背景透明

        resIds = castles;
        MAX_BITMAP_POOL_SIZE = (int) (Runtime.getRuntime().maxMemory()) / 4;

        if (Utils.hasHoneycomb()) {
            mReusableBitmaps = Collections.synchronizedSet(new HashSet<Bitmap>());
        }
    }

    /**
     * Decode and sample down a bitmap from a file input stream to the requested width and height.
     *
     * @param fileDescriptor The file descriptor to read from
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     * that are equal to or greater than the requested width and height
     */
    private Bitmap decodeSampledBitmapFromDescriptor(FileDescriptor fileDescriptor) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        // Calculate inSampleSize
        //options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inSampleSize = 1;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        // If we're running on Honeycomb or newer, try to use inBitmap
        if (Utils.hasHoneycomb()) {
            addInBitmapOptions(options);
        }

        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    private Bitmap decodeSampledBitmapFromRes(@IdRes int res) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inMutable = true;
        //BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        BitmapFactory.decodeResource(getResources(), res, options);

        // Calculate inSampleSize
        //options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inSampleSize = 1;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        // If we're running on Honeycomb or newer, try to use inBitmap
        if (Utils.hasHoneycomb()) {
            Log.e(TAG, "decodeSampledBitmapFromRes() add option inBitmap Option");
            addInBitmapOptions(options);
        }

        //return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        return BitmapFactory.decodeResource(getResources(), res, options);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void addInBitmapOptions(BitmapFactory.Options options) {
        //BEGIN_INCLUDE(add_bitmap_options)
        // inBitmap only works with mutable bitmaps so force the decoder to
        // return mutable bitmaps.
        options.inMutable = true;

        // Try and find a bitmap to use for inBitmap
        Bitmap inBitmap = getBitmapFromReusableSet(options);

        if (inBitmap != null) {
            options.inBitmap = inBitmap;
        }
        //END_INCLUDE(add_bitmap_options)
    }

    /**
     * @param options - BitmapFactory.Options with out* options populated
     * @return Bitmap that case be used for inBitmap
     */
    private Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
        //BEGIN_INCLUDE(get_bitmap_from_reusable_set)
        Bitmap bitmap = null;

        if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
            synchronized (mReusableBitmaps) {
                final Iterator<Bitmap> iterator = mReusableBitmaps.iterator();
                Bitmap item;

                while (iterator.hasNext()) {
                    item = iterator.next();

                    if (null != item && item.isMutable()) {
                        // Check to see it the item can be used for inBitmap
                        if (canUseForInBitmap(item, options)) {
                            bitmap = item;

                            // Remove from reusable set so it can't be used again
                            iterator.remove();
                            break;
                        }
                    } else {
                        // Remove from the set if the reference has been cleared.
                        iterator.remove();
                    }
                }
            }
        }
        return bitmap;
        //END_INCLUDE(get_bitmap_from_reusable_set)
    }

    private void startPlay() {
        mbeginTime = System.currentTimeMillis();
        Log.e(TAG, "startPlay() called with: " + System.currentTimeMillis());
        isFrameLoadComplete = new AtomicBoolean(false);
        mLoadIndex = new AtomicInteger(-1);
        CountDownLatch startSignal = new CountDownLatch(1);
        // FIXME: 16/12/19 多个线程读取，如何保证读取序列按序插入消费队列，而且，开多个线程，进过测试，会出现读取速度下降非常明显。
        new Thread(new ReadBitmapRunnable(startSignal))/*.setPriority(MAX_PRIORITY)*/.start();
        //new Thread(new ReadBitmapRunnable(startSignal)).start();
        //new Thread(new ReadBitmapRunnable(startSignal)).start();
        //new Thread(new ReadBitmapRunnable(startSignal)).start();
        new Thread(new PlayBitmapRunnable(startSignal)).start();
    }

    class ReadBitmapRunnable implements Runnable {
        private final CountDownLatch startSignal;

        ReadBitmapRunnable(CountDownLatch countDownLatch) {
            this.startSignal = countDownLatch;
        }

        @Override
        public void run() {
            long gapTime, lastTime = 0;
            while (resIds.length != mLoadIndex.get() && !isViewDetetch) {
                if (mBitmapQueue.size() < mFirstLoadFrame && mCurrentSize < MAX_BITMAP_POOL_SIZE) {
                    int index = mLoadIndex.addAndGet(1);
                    if (index >= resIds.length) {
                        isFrameLoadComplete.set(true);
                        return;
                    }
                    Bitmap bitmap;
                    if (mReusableBitmaps != null && mReusableBitmaps.size() > 0) {
                        bitmap = decodeSampledBitmapFromRes(resIds[index]);
                        Log.e(TAG, "load from mReusableBitmaps");
                    } else {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inMutable = true;
                        options.inSampleSize = 1;
                        options.inJustDecodeBounds = false;
                        bitmap = BitmapFactory.decodeResource(getResources(), resIds[index], options);
                        Log.e(TAG, "load from decodeResource");
                    }
                    //Log.e(TAG, "mReusableBitmaps size is " + mReusableBitmaps.size());
                    gapTime = System.currentTimeMillis() - lastTime;
                    lastTime = System.currentTimeMillis();
                    Log.d(TAG, "load from  onNext load time = " + gapTime + "  loadIndex = " + index);
                    mCurrentSize += bitmap.getByteCount();
                    mBitmapQueue.add(bitmap);
                } else {
                    mCanDraw = true;
                    startSignal.countDown();//if count down to zero ,then nothing to happen
                }
            }
            isFrameLoadComplete.set(true);
        }
    }


    class PlayBitmapRunnable implements Runnable {
        private final CountDownLatch startSignal;

        PlayBitmapRunnable(CountDownLatch startSignal) {
            this.startSignal = startSignal;
        }

        @Override
        public void run() {
            try {
                startSignal.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (mCanDraw && !isViewDetetch) {
                try {
                    drawView();
                    Thread.sleep(mFrameSpaceTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.e(TAG, "stopPlay() use time: " + (System.currentTimeMillis() - mbeginTime));
            if (needRepeat) {
                startPlay();
            }
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (castles == null) {
            return;
        }
        isViewDetetch = false;
        startPlay();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCanDraw = false;
        isViewDetetch = true;
        if (mReusableBitmaps != null) {
            mReusableBitmaps.clear();
            mReusableBitmaps = null;
        }

        recyclBitmaps();//这里很重要，不做这个，就内存泄露了
        try {
            Thread.sleep(mFrameSpaceTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void drawView() {
        Canvas mCanvas = mHolder.lockCanvas(); // 锁定画布
        if (mCanvas == null) {
            return;
        }
        try {
            mCurrentdrawable = new BitmapDrawable(getResources(), mBitmapQueue.poll());
            mCurrentdrawable.setBounds(0, 0, getWidth(), getHeight());
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); // 清除屏幕
            mCurrentdrawable.draw(mCanvas);
            mCurrentSize -= mCurrentdrawable.getBitmap().getByteCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas); // 提交画布
            }
            if (isFrameLoadComplete.get() && mBitmapQueue.size() <= 0) {
                mCanDraw = false;
            }
            if (mCurrentdrawable != null && mCurrentdrawable.getBitmap() != null && !mCurrentdrawable.getBitmap().isRecycled()) {
                //mCurrentdrawable.getBitmap().recycle();//拿过来重用下，加上读取
                if (mReusableBitmaps != null && mReusableBitmaps.size() < mFirstLoadFrame) {
                    mReusableBitmaps.add(mCurrentdrawable.getBitmap());
                }
            }
        }
    }

    private void recyclBitmaps() {
        for (Bitmap bitmap : mBitmapQueue) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }
}
