package uwc.android.spruce.widget.sticky;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by steven on 17/5/12.
 */

public class StickyHeaderLayout extends FrameLayout{
    StickyRecyclerView mScrollableView;

    public StickyHeaderLayout(Context context) {
        super(context);
    }

    public StickyHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickyHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {
        if(!(getChildCount() == 1 && getChildAt(0) instanceof StickyRecyclerView)){
            throw new IllegalArgumentException("Inflate Error: StickyHeaderLayout must has one child, which instanceof StickyRecyclerView");
        }

        mScrollableView = (StickyRecyclerView)getChildAt(0);
    }
}
