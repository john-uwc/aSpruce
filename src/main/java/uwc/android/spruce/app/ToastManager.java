package uwc.android.spruce.app;

import android.support.annotation.StringRes;

/**
 * Toast 管理
 */
public interface ToastManager {
    /**
     * 根据 text 字符串, 显示 Toast
     *
     * @param text
     */
    void showToast(String text);

    /**
     * 根据 textRes 字符串资源, 显示 Toast
     *
     * @param textRes
     */
    void showToast(@StringRes int textRes);
}
