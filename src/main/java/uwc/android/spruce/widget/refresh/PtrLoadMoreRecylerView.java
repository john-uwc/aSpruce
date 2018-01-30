package uwc.android.spruce.widget.refresh;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;

/**
 * Created by steven on 17/5/16.
 */

public class PtrLoadMoreRecylerView extends RecyclerView {

    public PtrLoadMoreRecylerView(Context context) {
        super(context);
    }

    public PtrLoadMoreRecylerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PtrLoadMoreRecylerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(new RecyclerAdapterWithHF(adapter));
    }
}
