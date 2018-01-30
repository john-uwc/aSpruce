package uwc.android.spruce.widget.sticky;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import uwc.android.spruce.widget.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;
import uwc.android.spruce.widget.refresh.PtrLoadMoreRecylerView;

/**
 * Created by steven on 17/5/12.
 */

public class StickyRecyclerView extends PtrLoadMoreRecylerView {
    HeaderProvider mHeaderProvider;

    View mHeaderView, mStickyViewInHeader, mStickyViewFloating;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;

    public void setHeaderProvider(HeaderProvider headerProvider) {
        mHeaderProvider = headerProvider;
        mHeaderView = headerProvider.getHeaderView((ViewGroup)this.getParent());
        mStickyViewInHeader = headerProvider.getStickyView((ViewGroup)this.getParent());
        mStickyViewFloating = headerProvider.getStickyView((ViewGroup)this.getParent());
        if(mStickyViewFloating != null)
            mStickyViewFloating.setVisibility(View.GONE);

        if(this.getParent() instanceof FrameLayout){
            if(mStickyViewFloating != null)
                ((FrameLayout)this.getParent()).addView(mStickyViewFloating);
        }

        trackHeader();
    }

    private void trackHeader() {
        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                updateFloatingView();
            }
        });
    }

    void updateFloatingView() {
        if(mStickyViewInHeader == null || mStickyViewFloating == null || getAdapter() == null || !(getLayoutManager() instanceof LinearLayoutManager)){
            return ;
        }

        LinearLayoutManager layoutManager = (LinearLayoutManager)getLayoutManager();
        int targetTop = mStickyViewInHeader.getTop();

        mStickyViewFloating.setVisibility(targetTop < 0? View.VISIBLE: View.GONE);

        int firstPos = layoutManager.findFirstVisibleItemPosition();
        if (firstPos > (mHeaderView!=null? 1: 0)) {
            mStickyViewFloating.setVisibility(View.VISIBLE);
        }
    }

    public StickyRecyclerView(Context context) {
        this(context, null, 0);
    }

    public StickyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if(mHeaderProvider != null) {
            if (mHeaderAndFooterWrapper == null) {
                mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(adapter);
                if(mHeaderView != null)
                    mHeaderAndFooterWrapper.addHeaderView(mHeaderView);
                if(mStickyViewInHeader != null)
                    mHeaderAndFooterWrapper.addHeaderView(mStickyViewInHeader);
            } else {
                mHeaderAndFooterWrapper.setInnerAdapter(adapter);
            }
            super.setAdapter(mHeaderAndFooterWrapper);

        }else{
            super.setAdapter(adapter);
        }
    }

    public static abstract class HeaderProvider{
        protected View getHeaderView(ViewGroup parent){
            return null;
        }

        protected View getStickyView(ViewGroup parent){
            return null;
        }
    }
}
