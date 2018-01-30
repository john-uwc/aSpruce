package uwc.android.spruce.widget.refresh;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;

/**
 * Created by steven on 17/5/16.
 */

public abstract class PtrRvHandler extends PtrDefaultHandler {
    RecyclerView mRecyclerView;

    public PtrRvHandler(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return mRecyclerView.computeVerticalScrollOffset() == 0;
    }
}
