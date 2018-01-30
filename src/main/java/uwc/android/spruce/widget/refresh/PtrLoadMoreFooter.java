package uwc.android.spruce.widget.refresh;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.chanven.lib.cptr.loadmore.ILoadMoreViewFactory;
import uwc.android.spruce.R;

/**
 * Created by steven on 17/5/16.
 */

public class PtrLoadMoreFooter extends FrameLayout implements ILoadMoreViewFactory.ILoadMoreView {

    @LayoutRes int mLayoutRes;
    View mViewNormal = new View(getContext()),
            mViewLoading = new View(getContext()),
            mViewFail = new View(getContext()),
            mViewNoMore = new View(getContext());

    final View[] mViews = {mViewNormal, mViewLoading, mViewFail, mViewNoMore};

    public PtrLoadMoreFooter setViewNormal(@IdRes int idRes) {
        View item = findViewById(idRes);
        if(item!= null) {
            mViewNormal = item;
            mViews[0] = item;
        }
        return this;
    }

    public PtrLoadMoreFooter setViewLoading(@IdRes int idRes) {
        View item = findViewById(idRes);
        if(item!= null) {
            mViewLoading = item;
            mViews[1] = item;
        }
        return this;
    }

    public PtrLoadMoreFooter setViewFail(@IdRes int idRes) {
        View item = findViewById(idRes);
        if(item!= null) {
            mViewFail = item;
            mViews[2] = item;
        }
        return this;
    }

    public PtrLoadMoreFooter setViewNoMore(@IdRes int idRes) {
        View item = findViewById(idRes);
        if(item!= null) {
            mViewNoMore = item;
            mViews[3] = item;
        }
        return this;
    }

    public PtrLoadMoreFooter(Context context, int layoutRes) {
        super(context);
        mLayoutRes = layoutRes;
        initView();
    }

    protected PtrLoadMoreFooter(Context context) {
        super(context);
        initView();
    }

    protected PtrLoadMoreFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    protected PtrLoadMoreFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), mLayoutRes, this);
    }

    @Override
    public void init(ILoadMoreViewFactory.FootViewAdder footViewHolder, OnClickListener onClickLoadMoreListener) {
        footViewHolder.addFootView(this);
    }

    /**
     * 空闲状态(初始或加载完成)
     *
     * 由 {@link MPtrClassicFrameLayout#loadMoreComplete(boolean hasMore)} 触发.
     * 且参数 hasMore = true. 即有更多页面
     */
    @Override
    public void showNormal() {
        show(mViewNormal);
    }

    /**
     * 加载状态
     *
     * 由手势动作触发
     */
    @Override
    public void showLoading() {
        show(mViewLoading);
    }

    /**
     * 加载失败
     *
     * 需要自行调用.
     * @param e
     */
    @Override
    public void showFail(Exception e) {
        show(mViewFail);
    }

    /**
     * 没有更多页面的状态
     *
     * 由 {@link MPtrClassicFrameLayout#loadMoreComplete(boolean hasMore)} 触发.
     * 且参数 hasMore = false. 即有没有更多了
     */
    @Override
    public void showNomore() {
        show(mViewNoMore);
    }

    private void show(View targetView){
        for (View view : mViews) {
            view.setVisibility(view == targetView? VISIBLE: GONE);
        }
    }

    @Override
    public void setFooterVisibility(boolean isVisible) {
        this.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * 默认 LoaderMoreFooter 工厂
     */
    public static class DefaultFactory implements ILoadMoreViewFactory{

        Context mContext;

        public DefaultFactory(Context context) {
            mContext = context;
        }

        @Override
        public ILoadMoreView madeLoadMoreView() {
            return new PtrLoadMoreFooter(mContext, R.layout.widget_ptr_load_more)
                    .setViewNormal(R.id.layout_normal)
                    .setViewLoading(R.id.layout_loading)
                    .setViewFail(R.id.layout_fail)
                    .setViewNoMore(R.id.layout_no_more);
        }
    }
}
