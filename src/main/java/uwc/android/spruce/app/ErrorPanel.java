package uwc.android.spruce.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import uwc.android.spruce.R;

/**
 * Created by steven on 17/6/9.
 * <p>
 * 错误页面
 */

class ErrorPanel extends FrameLayout {
    private FrameLayout mLayoutReal;
    private FrameLayout mLayoutEmpty;

    public ErrorPanel(Context context) {
        super(context);
        initView();
    }

    public ErrorPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ErrorPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.g_error_layout, this);
        mLayoutReal = (FrameLayout) findViewById(R.id.layout_real);
        mLayoutEmpty = (FrameLayout) findViewById(R.id.layout_empty);
    }

    public ErrorPanel wrap(View targetView) {
        ViewGroup parent = (ViewGroup) targetView.getParent();
        if (parent != null) {
            int pos = parent.indexOfChild(targetView);
            parent.removeView(targetView);
            mLayoutReal.addView(targetView);
            parent.addView(this, pos);
            requestLayout();
        }
        return this;
    }

    public void resetHeightOfEmpty(int height) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mLayoutEmpty.getLayoutParams();
        lp.height = height;
        mLayoutEmpty.setLayoutParams(lp);
    }

    public void showEmpty(boolean isShowEmpty) {
        mLayoutReal.setVisibility(isShowEmpty ? GONE : VISIBLE);
        mLayoutEmpty.setVisibility(isShowEmpty ? VISIBLE : GONE);
    }
}
