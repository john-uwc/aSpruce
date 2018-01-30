package uwc.android.spruce.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * created by steven
 */
public class ScrollTapViewPager extends ViewPager {
    private boolean mScrollable = true;

    public void setScrollable(boolean scrollable) {
        mScrollable = scrollable;
    }

    public ScrollTapViewPager(Context context) {
        this(context, null);
    }

    public ScrollTapViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.mScrollable && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.mScrollable && super.onTouchEvent(event);
    }
}
