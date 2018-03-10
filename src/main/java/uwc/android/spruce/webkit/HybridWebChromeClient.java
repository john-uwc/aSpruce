package uwc.android.spruce.webkit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import uwc.android.spruce.app.UIAgent;
import uwc.android.spruce.media.Loader;
import uwc.android.spruce.util.FileMan;

import java.io.IOException;

/**
 * Created by steven on 17/4/27.
 *
 * 默认的 WebChromeClient
 */

public class HybridWebChromeClient extends WebChromeClient implements View.OnLongClickListener {
    protected HybridWebView mWebView;
    protected Activity mActivity;

    private ProgressBar mProgressBar;
    private TextView mTvTitle;
    private FullScreenHolder mFullScreenHolder;

    public HybridWebChromeClient setProgressBar(ProgressBar progressBar) {
        mProgressBar = progressBar;
        return this;
    }

    public HybridWebChromeClient setTitle(TextView tvTitle) {
        mTvTitle = tvTitle;
        return this;
    }

    public HybridWebChromeClient(Activity activity, HybridWebView webView) {
        mActivity = activity;
        mFullScreenHolder = new FullScreenHolder(mActivity);
        mWebView = webView;
        mWebView.setOnLongClickListener(this);
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

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (mTvTitle == null)
            return;

        mTvTitle.setText(title);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (mProgressBar == null)
            return;

        if (newProgress < 100) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        if (newProgress >= 100) {
            mProgressBar.setVisibility(View.GONE);
        }

        mProgressBar.setProgress(newProgress);
        mProgressBar.postInvalidate();
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        mWebView.setVisibility(View.INVISIBLE);
        mFullScreenHolder.show(view, callback);
    }

    @Override
    public void onHideCustomView() {
        mFullScreenHolder.hide();
        mWebView.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onLongClick(View v) {
        final WebView.HitTestResult hitTestResult = ((WebView) v).getHitTestResult();
        // 如果是图片类型或者是带有图片链接的类型
        if (!(WebView.HitTestResult.IMAGE_TYPE == hitTestResult.getType() ||
                WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE == hitTestResult.getType())) {
            return false;//保持长按可以复制文字
        }

        // 弹出保存图片的对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("提示");
        builder.setMessage("保存图片到本地");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            // 自动dismiss
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //获取图片链接
                Loader.loadImage(mActivity, hitTestResult.getExtra(), new Loader.Callback() {
                    @Override
                    protected void onLoadingComplete(String url, View imageView, Bitmap bitmap) {

                        try {
                            FileMan.doSave(mActivity
                                    , bitmap, FileMan.Type.jpg
                                    , Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }

                        UIAgent.showToast(mActivity, "成功保存");
                    }
                });
            }
        });
        builder.create().show();

        return true;
    }
}