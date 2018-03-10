package uwc.android.spruce.webkit;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;

/**
 * Created by steven on 17/4/27.
 * H5页面全屏处理
 */
public class FullScreenHolder extends FrameLayout {

    private WebChromeClient.CustomViewCallback mVTargetCallback;
    private View mVTarget;

    private Activity mCtx;

    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mCtx.getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public FullScreenHolder(Activity ctx) {
        super(ctx);
        setBackgroundColor((mCtx = ctx).getResources().getColor(android.R.color.black));
    }


    @Override
    public boolean onTouchEvent(MotionEvent evt) {
        return true;
    }

    /**
     * 全屏显示
     **/
    public void show(View view, WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (null != mVTarget) {
            return;
        }
        mVTargetCallback = callback;
        mVTarget = view;
        FrameLayout decor = (FrameLayout) mCtx.getWindow().getDecorView();
        LayoutParams lp =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mVTarget, lp);
        decor.addView(this, lp);
        setStatusBarVisibility(false);

        mCtx.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 隐藏全屏
     */
    public void hide() {
        if (null == mVTarget) {
            return;
        }

        mCtx.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setStatusBarVisibility(true);
        FrameLayout decor = (FrameLayout) mCtx.getWindow().getDecorView();
        decor.removeView(this);
        removeAllViews();
        mVTarget = null;
        mVTargetCallback.onCustomViewHidden();
    }
}
