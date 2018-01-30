package uwc.android.spruce.webkit;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import uwc.android.spruce.content.Permission;
import uwc.android.spruce.R;
import uwc.android.spruce.app.UIAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by steven on 17/2/22.
 * <p>
 * 通用的 WebView 的一些配置.
 * <p>
 * <a href="http://stackoverflow.com/questions/3130654/memory-leak-in-webview">使用 getApplicationContext() 防止内存泄漏</a>
 * a. 使用时, 不要在 xml 布局中静态注册, 而是动态 new TZWebView(activity).
 * <p>
 * b. 使用时注意调用如下生命周期:
 * {@link #onResume()}
 * {@link #onPause()}
 * {@link #onDestroy()}
 * {@link #onActivityResult(int, int, Intent)}
 */
public class HybridWebView extends WebView {
    protected final String TAG = getClass().getSimpleName();

    public static interface UAHolder {
        public String query();
    }

    public static UAHolder sUAHolder = new UAHolder() {
        @Override
        public String query() {
            return "";
        }
    };

    private Activity mActivity;     // Activity
    private Context mAppContext;    // Application Context
    private String oldUrl = "";

    public Activity getActivity() {
        return mActivity;
    }

    public HybridWebView(Context context) {
        super(context.getApplicationContext());     // 使用 getApplicationContext() 初始化 WebView, 防止内存泄漏
        initWebView((Activity) context);
    }

    public HybridWebView(Context context, AttributeSet attributeSet) {
        super(context.getApplicationContext(), attributeSet);
        initWebView((Activity) context);
    }

    public HybridWebView(Context context, AttributeSet attributeSet, int i) {
        super(context.getApplicationContext(), attributeSet, i);
        initWebView((Activity) context);
    }

    private void initWebView(Activity activity) {
        mActivity = activity;
        mAppContext = getContext();

        WebSettings webSettings = this.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //设置webview LocalStorage
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabasePath(mAppContext.getDir("database", Context.MODE_PRIVATE).getPath());

        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setGeolocationDatabasePath(mAppContext.getFilesDir().getPath());

        webSettings.setDefaultFixedFontSize(16); // v3.3 不设默认字体大小，当手机设置极小字体时候，部分H5页面显示不正常

        webSettings.setUserAgentString(sUAHolder.query());

        // v4.0 支持Https与Http混合的页面内容加载
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // v4.0支持跨域打点增加
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }

        this.setWebChromeClient(new SimpleChromeClient(getActivity()));
        this.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                Log.d(TAG, url);
                // 打电话
                if (url.startsWith("tel:")) {
                    Permission.with((Permission.Host)getActivity()).request(String.format(getResources().getString(R.string.rationale), "电话"), new String[]{Manifest.permission.CALL_PHONE}, new Permission.Callback() {
                        @Override
                        public void onAfterAllPermissionGranted(int requestCode, List<String> perms) {
                            getActivity().startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                        }

                        @Override
                        public void onPermissionsDenied(int requestCode, List<String> perms) {
                            UIAgent.showToast(getActivity(), "权限被拒绝");
                        }
                    });

                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        initHybrid();
    }

    // TODO: 接入 Hybrid
    private void initHybrid() {

    }

    @Override
    public void onResume() {
        super.onResume();
        getSettings().setUserAgentString(sUAHolder.query());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        ViewGroup parent = ((ViewGroup) this.getParent());
        if (parent != null) {
            parent.removeView(this);
            this.clearCache(true); // 清理缓存
            this.removeAllViews();
            this.destroy();
            this.mActivity = null;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getSettings().setUserAgentString(sUAHolder.query());
        // TODO: 接入 Hybrid
    }

    @Override
    public void loadUrl(String url) {
        if (!url.equals(oldUrl)) {
            getSettings().setUserAgentString(sUAHolder.query());
            oldUrl = url;
            super.loadUrl(url);
        } else {

        }

    }

    @Override
    public void loadData(String data, String mimeType, String encoding) {
        loadUrl("data:" + mimeType + ";charset=" + encoding + "," + data);
    }

    /**
     * 拼接请求参数
     */
    public static String paddingParms(String mTargetUrl, HashMap<String, String> parms) {
        StringBuilder url = new StringBuilder(mTargetUrl);
        if (parms != null && parms.size() > 0) {
            int idx = 0;
            int count = parms.size();
            url.append("?");
            String key;
            String val;
            for (Object o : parms.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                key = (String) entry.getKey();
                url.append(key);
                url.append("=");
                val = (String) entry.getValue();
                url.append(val);
                if (idx < count - 1) {
                    url.append("&");
                    idx++;
                }
            }
        }
        return url.toString();
    }

    /**
     * 默认的 WebChromeClient
     */
    public static class SimpleChromeClient extends WebChromeClient {
        protected Activity mActivity;

        public SimpleChromeClient(Activity activity) {
            mActivity = activity;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            if (consoleMessage != null) {
                String jsMessage = consoleMessage.message();
                if (!TextUtils.isEmpty(jsMessage)) {
                    Log.i("js_info", jsMessage);
                }
            }

            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            DialogInterface.OnClickListener dialogButtonOnClickListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int clickedButton) {
                    if (DialogInterface.BUTTON_POSITIVE == clickedButton) {
                        callback.invoke(origin, true, true);
                    } else if (DialogInterface.BUTTON_NEGATIVE == clickedButton) {
                        callback.invoke(origin, false, false);
                    }
                }
            };

            new AlertDialog.Builder(mActivity)
                    .setMessage("是否允许获取您的位置信息?")
                    .setPositiveButton("允许", dialogButtonOnClickListener)
                    .setNegativeButton("拒绝", dialogButtonOnClickListener)
                    .show();
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }
}
