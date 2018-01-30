package uwc.android.spruce.app;

/**
 * Loading 管理
 */
public interface ProgressManager {
    /**
     * 显示 Loading
     */
    void showProgress();

    /**
     * 取消 Loading
     */
    void dismissProgress();
}
