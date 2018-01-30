package uwc.android.spruce.app;

import android.view.View;

/**
 * Created by steven on 17/6/9.
 *
 * 错误页面管理
 */

public class ErrorManager {

    private ErrorPanel mErrorPanel;

    public ErrorManager(View targetView) {
        mErrorPanel = new ErrorPanel(targetView.getContext()).wrap(targetView);
    }

    public ErrorManager(View targetView, int height) {
        mErrorPanel = new ErrorPanel(targetView.getContext()).wrap(targetView);
        mErrorPanel.resetHeightOfEmpty(height);
    }

    public void showEmpty(boolean isShow){
        mErrorPanel.showEmpty(isShow);
    }

}
