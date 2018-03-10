package uwc.android.spruce.app;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import uwc.core.p.Redis;

/**
 * Created by steven on 17/4/26.
 * 桥接模式
 * <p>
 * AActivity 和 AFragment 中, 关于 Loading 和 Toast 的统一实现.
 */
public class UIAgent {
    /**
     * 防止应用跟随系统字体大小改变
     */
    public static void fixFontScale(Context context) {
        Configuration mCurConfig = context.getResources().getConfiguration();
        mCurConfig.fontScale = 1f;
        context.getResources().updateConfiguration(mCurConfig, context.getResources().getDisplayMetrics());
    }
    
    // 是否是小米手机
    private static boolean isXiaomi() {
        return "Xiaomi".equals(Build.MANUFACTURER);
    }

    // 设置小米状态栏
    private static void setXiaomiStatusBar(Window window, boolean isTranslucent) {
        Class<? extends Window> clazz = window.getClass();
        try {
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, isTranslucent ? 0 : darkModeFlag, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 是否是魅族手机
    private static boolean isMeizu() {
        try {
            Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (NoSuchMethodException e) {
        }
        return false;
    }

    // 设置魅族状态栏
    private static void setMeizuStatusBar(Window window, boolean isTranslucent) {
        WindowManager.LayoutParams params = window.getAttributes();
        try {
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(params);
            if (isTranslucent) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(params, value);
            window.setAttributes(params);
            darkFlag.setAccessible(false);
            meizuFlags.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static Map<String, AAwait> sProgressCache = new HashMap<>();

    /**
     * activity 生命周期结束后(onDestroy), 清除其 AAwait 的缓存.
     *
     * @param activity
     */
    public static void clearAwaitCache(Activity activity) {
        if (sProgressCache.containsKey(activity.toString())) {
            AAwait dialog = fetchAwait(activity);

            if (dialog.isShowing())
                dialog.dismiss();

            sProgressCache.remove(activity.toString());
        }
    }


    private static AAwait fetchAwait(Activity activity) {
        AAwait dialog = sProgressCache.get(activity.toString());
        if (dialog == null) {
            dialog = new AAwait.Builder(activity).build();
            sProgressCache.put(activity.toString(), dialog);
        }

        return dialog;
    }

    /**
     * 显示 Loading
     *
     * @param activity
     */
    public static void showProgress(Activity activity) {
        if (activity == null || activity.isFinishing())
            return;

        AAwait dialog = fetchAwait(activity);

        if (!dialog.isShowing())
            dialog.show();
    }

    /**
     * 取消 Loading
     *
     * @param activity
     */
    public static void dismissProgress(Activity activity) {
        if (activity == null || activity.isFinishing())
            return;

        AAwait dialog = fetchAwait(activity);

        if (dialog.isShowing())
            dialog.dismiss();
    }


    private static HashMap<String, Long> sToastCache = new HashMap<>();

    /**
     * 根据 text 字符串, 显示 Toast
     *
     * @param activity
     * @param text
     */
    public static void showToast(final Activity activity, final String text) {
        if (activity == null)
            return;

        long currTime = SystemClock.elapsedRealtime();
        if (sToastCache.containsKey(text)
                && 3000L >= (currTime - sToastCache.get(text)))
            return;
        sToastCache.put(text, currTime);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 根据 textRes 字符串资源, 显示 Toast
     *
     * @param activity
     * @param textRes
     */
    public static void showToast(Activity activity, @StringRes int textRes) {
        showToast(activity, (String) Redis.query(Context.class).getResources().getText(textRes));
    }


    /**
     * 收起软键盘
     */
    public static void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null == imm)
            return;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 弹出软键盘
     */
    public static void showSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null == imm)
            return;
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
}
