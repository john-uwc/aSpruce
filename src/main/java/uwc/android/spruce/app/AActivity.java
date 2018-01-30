package uwc.android.spruce.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.AnimRes;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;

import java.io.File;
import java.util.List;

import de.greenrobot.event.EventBus;
import uwc.android.spruce.content.Permission;


/**
 * Created by admin on 2017/4/22.
 */
public abstract class AActivity extends AppCompatActivity implements TitleCallBack, ProgressManager, ToastManager,
        InputManager, Permission.Host {

    protected String TAG = getClass().getSimpleName();

    protected ATitleBar mTitleBar;

    protected static final int TAKE_CAPTURE = 91;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        AppManager.getInstance().addActivity(this);
        UIAgent.fixFontScale(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setStatusBar(android.R.color.transparent, true, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftInput();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        UIAgent.clearAwaitCache(this);
        ImmersionBar.with(this).destroy();
        AppManager.getInstance().removeActivity(this);
    }

//    @Override
//    public void onBackPressed() {
//        hideSoftInput();
//        super.onBackPressed();
//    }

    /**
     * 设置状态栏
     * @param statusBarColor    状态栏颜色。默认透明
     * @param isDarkFont        6.0以上可设置状态栏字体为黑色。默认 true
     * @param fitsSystemWindows true: 填充一段 statusBar 高度, false: 不填充（statusBar 与 titleBar重叠）。默认 true
     */
    public void setStatusBar(@ColorRes int statusBarColor, boolean isDarkFont, boolean fitsSystemWindows) {
        setStatusBar(statusBarColor, isDarkFont, fitsSystemWindows, false);
    }

    /**
     * 设置状态栏
     * @param statusBarColor    状态栏颜色。默认透明
     * @param isDarkFont        6.0以上可设置状态栏字体为黑色。默认 true
     * @param fitsSystemWindows true: 填充一段 statusBar 高度, false: 不填充（statusBar 与 titleBar重叠）。默认 true
     * @param hideBar           true: 隐藏状态栏, false: 不隐藏。默认 false
     */
    public void setStatusBar(@ColorRes int statusBarColor, boolean isDarkFont, boolean fitsSystemWindows, boolean hideBar) {
        ImmersionBar immersionBar = ImmersionBar.with(this)
                .statusBarColor(statusBarColor)
                .fitsSystemWindows(fitsSystemWindows)
                .hideBar(hideBar ? BarHide.FLAG_HIDE_BAR : BarHide.FLAG_SHOW_BAR);
        if(!hideBar && isDarkFont) {
            immersionBar.statusBarDarkFont(isDarkFont, 0.2f);
        }else{
            immersionBar.statusBarDarkFont(false, 0f);
        }
        immersionBar.init();
    }

    /**
     * @param id      布局资源
     * @param oleft   title 左边内容
     * @param ocenter title中间内容
     * @param oright  title 右边内容
     */
    public void setContentView(int id, Object oleft, Object ocenter, Object oright) {
        mTitleBar = new ATitleBar(this)
                .setCallback(this);
        mTitleBar.resetWith(oleft, ocenter, oright);
        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(mTitleBar);
        try {
            LayoutInflater inflater = LayoutInflater.from(this);
            ViewGroup parent = (ViewGroup) this.findViewById(android.R.id.content);
            View view = inflater.inflate(id, parent, false);
            layout.addView(view);
        } catch (Exception e) {
        }
        setContentView(layout);
    }


    /**
     * 替换fragement
     *
     * @param id_content
     * @param fragment
     */
    public void replaceFragment(int id_content, Fragment fragment) {
        replaceFragment(id_content, 0, 0, fragment);
    }

    /**
     * 替换fragement 并添加动画
     *
     * @param id_content
     * @param anim_in
     * @param anim_out
     * @param fragment
     */
    public void replaceFragment(@IdRes int id_content, @AnimRes int anim_in, @AnimRes int anim_out, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(anim_in, anim_out);
        transaction.replace(id_content, fragment);
        transaction.commit();
    }

    // -------------------- UI 部分: loading 和 toast -------------------

    /**
     * 显示 Loading
     */
    @Override
    public void showProgress() {
        UIAgent.showProgress(this);
    }

    /**
     * 取消 Loading
     */
    @Override
    public void dismissProgress() {
        UIAgent.dismissProgress(this);
    }

    /**
     * 根据 text 字符串, 显示 Toast
     *
     * @param text
     */
    @Override
    public void showToast(String text) {
        UIAgent.showToast(this, text);
    }

    /**
     * 根据 textRes 字符串资源, 显示 Toast
     *
     * @param textRes
     */
    @Override
    public void showToast(@StringRes int textRes) {
        UIAgent.showToast(this, textRes);
    }
    // -----------------------------------------------------------------

    protected boolean takeCapture(File sn) {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (null == sn || null == captureIntent.resolveActivity(getPackageManager())) {
            return false;
        }
        Uri uri = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                FileProvider.getUriForFile(this, "uwc.android.spruce.file", sn) : Uri.fromFile(sn);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(captureIntent, TAKE_CAPTURE);
        return true;
    }

    protected void onCapture(boolean success) {

    }

    /**
     * 收起软键盘
     */
    @Override
    public void hideSoftInput() {
        UIAgent.hideSoftInput(this.getWindow().getDecorView());
    }

    //------------------------------titlebar-------------------------------------

    /**
     * titlebar左侧点击
     */
    @Override
    public void onLeftClick() {
        onBackPressed();
        hideSoftInput();
    }

    /**
     * titlebar 右侧控件点击
     */
    @Override
    public void onRightClick() {

    }

    /**
     * 右边为字体时设置背景
     *
     * @param ab_tv_right
     */
    @Override
    public void setRightTextBackground(TextView ab_tv_right) {

    }

    @Override
    public void setTitleBarBackground(View view) {

    }

    @Override
    public void setCenterTextSize(TextView view) {

    }

    @Override
    public void setCenterTextColor(TextView textview) {

    }

    @Override
    public void setCenterTextContent(TextView view) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_CAPTURE:
                onCapture(RESULT_OK == resultCode);
                break;
            default:
                break;
        }
    }

    // ------------------------------ 6.0权限检查 -----------------------------
    @Override
    public final void onPermissionsGranted(int requestCode, List<String> perms) {
        int[] r = new int[perms.size()];
        for (int i = 0; i < r.length; i++) r[i] = PackageManager.PERMISSION_GRANTED;
        Permission.onResult(this, requestCode, perms.toArray(new String[0]), r);
    }

    @Override
    public final void onPermissionsDenied(int requestCode, List<String> perms) {
        int[] r = new int[perms.size()];
        for (int i = 0; i < r.length; i++) r[i] = PackageManager.PERMISSION_DENIED;
        Permission.onResult(this, requestCode, perms.toArray(new String[0]), r);
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Permission.onResult(this, requestCode, permissions, grantResults);
    }
    // -------------------------------------------------------------------------


    // -------------------- EventBus -------------------

    /**
     * @param o
     */
    public void onEvent(Object o) {

    }
    // -------------------------------------------------
}
