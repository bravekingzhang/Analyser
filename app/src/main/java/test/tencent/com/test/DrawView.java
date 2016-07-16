package test.tencent.com.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by brzhang on 16/7/16.
 * Description :
 */
public class DrawView extends SurfaceView implements SurfaceHolder.Callback {

    private int mWidth;
    private int mHeight;

    private DrawThread mDrawThread;

    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        mDrawThread = new DrawThread(holder);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mDrawThread.isRunning = true;
        mDrawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mDrawThread.isRunning = false;
        //这块的意思其实就是，等待DrawThread执行完，上面的isRunning其实也达到这个目的咯
        try {
            mDrawThread.join();
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 这里的线程专门负责绘制
    ///////////////////////////////////////////////////////////////////////////
    class DrawThread extends Thread {

        SurfaceHolder surfaceHolder;
        boolean       isRunning;
        int radius = 10;
        Paint mPaintCirlce;

        Paint mPaintText;

        public DrawThread(SurfaceHolder surfaceHolder) {

            this.surfaceHolder = surfaceHolder;
            isRunning = false;

            mPaintCirlce = new Paint();
            mPaintCirlce.setStrokeWidth(4);
            mPaintCirlce.setColor(Color.YELLOW);
            mPaintCirlce.setStyle(Paint.Style.FILL_AND_STROKE);

            mPaintText = new Paint();
            mPaintText.setTextSize(24);

        }

        @Override
        public void run() {

            Canvas c = null;

            while (isRunning) {

                try {
                    synchronized (surfaceHolder) {

                        c = surfaceHolder.lockCanvas(null);
                        doDraw(c);
                        //通过它来控制帧数执行一次绘制后休息500ms,实际上，我们知道，ondraw如果保证60FPS的话，看到的画面会比较流畅
                        //因此，这里，可能就是16ms咯，但是，我们这里完全可以设置的比16ms都小，知道surface的好处了吧。
                        Thread.sleep(500);
                    }
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                } finally {
                    surfaceHolder.unlockCanvasAndPost(c);
                }

            }

        }

        public void doDraw(Canvas c) {

            //这个很重要，清屏操作，清楚掉上次绘制的残留图像，这里和view的ondraw不一样，view的ondraw每次执行之前，实际上会自动替你清除之前画的。
            c.drawColor(Color.WHITE);
            c.translate(mWidth / 2, mHeight / 2); //这里将画笔放到view的中间

            c.drawCircle(0, 0, radius++, mPaintCirlce);

            String text = "这里是surface测试ooo";

            c.drawText(text.substring(0,radius % text.length()+1), -mPaintText.measureText(text.substring(0,radius % text.length()+1)) / 2, 0f, mPaintText);

            if (radius > mWidth / 2) {
                radius = 10;
            }

        }

    }
}
