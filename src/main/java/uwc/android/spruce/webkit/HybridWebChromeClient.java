package uwc.android.spruce.webkit;

import android.app.Activity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by steven on 17/4/27.
 *
 * 默认的 WebChromeClient
 */

public class HybridWebChromeClient extends HybridWebView.SimpleChromeClient {

    TextView mTvTitle;
    private ProgressBar mProgressBar;
    private WebVideoManager mTZWebVideoManager;

    public HybridWebChromeClient setTitle(TextView tvTitle) {
        mTvTitle = tvTitle;
        return this;
    }

    public HybridWebChromeClient setProgressBar(ProgressBar progressBar) {
        mProgressBar = progressBar;
        return this;
    }

    public HybridWebChromeClient setVideoManager(WebVideoManager TZWebVideoManager) {
        mTZWebVideoManager = TZWebVideoManager;
        return this;
    }

    public HybridWebChromeClient(Activity activity) {
        super(activity);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if(mTvTitle == null)
            return ;

        mTvTitle.setText(title);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if(mProgressBar == null)
            return ;

        if (newProgress < 100) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        if (newProgress >= 100) {
            mProgressBar.setVisibility(View.GONE);
        }

        mProgressBar.setProgress(newProgress);
        mProgressBar.postInvalidate();
    }

    //---------------视频处理逻辑 [s] ----------------------
    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if(mTZWebVideoManager != null)
            mTZWebVideoManager.showCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        if(mTZWebVideoManager != null)
            mTZWebVideoManager.hideCustomView();
    }
    //---------------视频处理逻辑 [e] ----------------------
}
