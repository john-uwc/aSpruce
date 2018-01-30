package uwc.android.spruce.widget.refresh;

import android.view.View;

import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrHandler;

/**
 * Created by steven on 17/5/16.
 */

public class PtrDisableHandler implements PtrHandler {
    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return false;
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {

    }
}
