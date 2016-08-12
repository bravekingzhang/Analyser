package test.tencent.com.test.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by hoollyzhang on 16/7/25.
 * Description :
 */
public class ScrollerLayout extends LinearLayout {

    private static final String TAG = "ScrollerLayout";

    // 创建一个Scroller
    private Scroller mScroller;

    private float mLastX;
    private float mLastY;

    public ScrollerLayout(Context context)
    {
        this(context, null);
    }

    // 1、创建Scroller
    public ScrollerLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mScroller = new Scroller(context);
    }

    // 2、触摸回调，每次X轴方向加100，然后调用smoothScrollTo
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
       // Log.d(TAG, "onTouchEvent() called with: " + "event = [" + event + "]");
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                mLastY = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                return true;
            case MotionEvent.ACTION_MOVE:
                float detax = event.getX() - mLastX;
                float detay = event.getY() - mLastY;
                Log.d(TAG, "onTouchEvent() called with: " + "detax = [" + detax + "]");
                Log.d(TAG, "onTouchEvent() called with: " + "detay = [" + detay + "]");
                mLastY = event.getY();
                mLastX = event.getX();
                smoothScrollBy((int) -detax, (int) -detay);
                return false;
        }
        return super.onTouchEvent(event);
    }

    // 4、调用startScroll设置坐标，然后invalidate重绘
    public void smoothScrollBy(int dx, int dy)
    {

        // 参数一：startX 参数二：startY为开始滚动的位置，dx,dy为滚动的偏移量, 1500ms为完成滚动的时间
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx,
                dy, 1000);
        invalidate();
    }

    // 5、重绘过程会调用computeScroll 真正调用scrollTo进行滚动 然后再进行重绘
    @Override
    public void computeScroll()
    {

        // 判断滚动是否完成 true就是未完成
        if (mScroller.computeScrollOffset())
        {

            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            // 本案例中 调用postInvalidate和invalidate都可以
            invalidate();
        }
        super.computeScroll();
    }

}
