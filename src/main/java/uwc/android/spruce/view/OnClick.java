package uwc.android.spruce.view;

import android.view.View;

/**
 * Created by steven on 17/5/27.
 *
 * 全局统一的 OnClick, 防止连点
 */

public abstract class OnClick implements View.OnClickListener {
    static boolean enabled = true;

    private static final Runnable ENABLE_AGAIN = new Runnable() {
        @Override public void run() {
            enabled = true;
        }
    };

    @Override public final void onClick(View v) {
        if (enabled) {
            enabled = false;
            v.post(ENABLE_AGAIN);
            doClick(v);
        }
    }

    public abstract void doClick(View v);
}
