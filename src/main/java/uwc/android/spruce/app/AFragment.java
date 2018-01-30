package uwc.android.spruce.app;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;

public abstract class AFragment extends Fragment implements ProgressManager, ToastManager,
        InputManager {
    protected String TAG = getClass().getName();

    private View mRootView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }


    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mRootView == null) {
            mRootView = initRootView(inflater);
        }
        initView();
        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        UIAgent.clearAwaitCache(getActivity());
    }


    public abstract void initView();

    protected abstract View initRootView(LayoutInflater inflater);

    // -------------------- UI 部分: loading 和 toast -------------------
    /**
     * 显示 Loading
     */
    @Override
    public void showProgress() {
        UIAgent.showProgress(getActivity());
    }

    /**
     * 取消 Loading
     */
    @Override
    public void dismissProgress() {
        UIAgent.dismissProgress(getActivity());
    }

    /**
     * 根据 text 字符串, 显示 Toast
     * @param text
     */
    @Override
    public void showToast(String text) {
        UIAgent.showToast(getActivity(), text);
    }

    /**
     * 根据 textRes 字符串资源, 显示 Toast
     * @param textRes
     */
    @Override
    public void showToast(@StringRes int textRes) {
        UIAgent.showToast(getActivity(), textRes);
    }
    // -----------------------------------------------------------------

    /**
     * 收起软键盘
     */
    @Override
    public void hideSoftInput() {
        UIAgent.hideSoftInput(getActivity().getWindow().getDecorView());
    }


    // -------------------- EventBus -------------------
    /**
     * 写一个空的 onEvent, 防止崩溃
     * @param o
     */

    public void onEvent(Object o) {

    }
    // -------------------------------------------------
}

