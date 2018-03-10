package uwc.android.spruce.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import uwc.android.spruce.R;
import uwc.android.spruce.webkit.HybridWebChromeClient;
import uwc.android.spruce.webkit.HybridWebView;
import uwc.android.spruce.webkit.FullScreenHolder;

/**
 * Created by steven on 17/4/27.
 *
 * WebActivity 的一个默认实现, 绑定了具体 UI. 不可被继承
 */
public class AWebActivity extends AActivity {
    public static final String PARAMS_URL = "PARAMS_URL";

    private ViewGroup mLayoutWebContainer;
    private HybridWebView mWebView;

    protected HybridWebView getWebView(){
        return mWebView;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.g_activity_web, R.mipmap.g_icon_nav_back, "", "");

        mLayoutWebContainer = (ViewGroup) findViewById(R.id.layout_web_container);
        mLayoutWebContainer.addView(
                mWebView = new HybridWebView(this),
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        mWebView.setWebChromeClient(new HybridWebChromeClient(this, mWebView)
                .setTitle((TextView) mTitleBar.getCenterTextView())
                .setProgressBar((ProgressBar) findViewById(R.id.pb)));

        initWebView(mWebView);
    }

    public void initWebView(HybridWebView webview) {}

    private HybridWebView lazyGetWebView() {
        if (null == mWebView) {
            mWebView = getWebView();
        }
        return mWebView;
    }

    public void loadUrl(String url) {
        if (url != null) {
            lazyGetWebView().loadUrl(url);
            return;
        }
        Log.e(TAG,"loadUrl(): url is null");
    }

    public void loadData(String data) {
        if (data != null) {
            lazyGetWebView().loadData(data, "text/html", "utf-8");
            return;
        }
        Log.e(TAG,"loadData(): data is null");
    }

    public void reload() {
        lazyGetWebView().reload();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lazyGetWebView().onResume();
        String url = getIntent().getStringExtra(PARAMS_URL);
        try {
            url = getIntent().getData().toString().substring("alps-".length());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        loadUrl(url);
    }

    @Override
    protected void onPause() {
        super.onPause();
        lazyGetWebView().onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lazyGetWebView().onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        lazyGetWebView().onActivityResult(requestCode, resultCode, data);
    }

    //设置回退
//    @Override
//    public void onBackPressed() {
//        if (lazyGetWebView().canGoBack()) {
//            lazyGetWebView().goBack();
//            return;
//        }
//        super.onBackPressed();
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (lazyGetWebView().canGoBack()) {
                lazyGetWebView().goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
