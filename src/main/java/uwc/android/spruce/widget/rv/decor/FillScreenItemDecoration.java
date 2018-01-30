package uwc.android.spruce.widget.rv.decor;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * created by steven
 */
public class FillScreenItemDecoration extends RecyclerView.ItemDecoration{
    private static final String TAG = FillScreenItemDecoration.class.getSimpleName();

    /**
     * [startPos, endPos) 的高度和 < recyclerView.getHeight(), 补满至 recyclerView.getHeight()
     */
    int startPos = 1;

    public FillScreenItemDecoration(int startPos) {
        this.startPos = startPos;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if(parent.getAdapter() == null)
            return ;

        int endPos = parent.getAdapter().getItemCount();

        int rangeHeight = 0;
        View child = null;
        for(int i=0; i<parent.getChildCount(); i++){
            child = parent.getChildAt(i);
            int adapterPos = parent.getChildAdapterPosition(child);
//            Logger.Holder.obtain().log(TAG, "pos " + i + ": " + " adapterPos " + adapterPos);
            if(adapterPos >= startPos && adapterPos < endPos)
                rangeHeight += child.getHeight();
        }
//        Logger.Holder.obtain().log(TAG, "rangeHeight " + rangeHeight);

        int minHeight = parent.getHeight();
        if(rangeHeight < minHeight){
            if(view == child && parent.getChildAdapterPosition(child) == parent.getAdapter().getItemCount()-1){
                outRect.set(0, 0, 0, minHeight-rangeHeight);
            }
        }
    }
}
