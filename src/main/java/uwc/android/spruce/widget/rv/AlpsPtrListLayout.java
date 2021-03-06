package uwc.android.spruce.widget.rv;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import uwc.android.spruce.app.ErrorManager;
import uwc.android.spruce.R;
import uwc.android.spruce.widget.refresh.MPtrClassicFrameLayout;
import uwc.android.spruce.widget.refresh.PtrRvHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 下拉刷新 + 上拉加载列表
 * @param <T>
 */
public class AlpsPtrListLayout<T> extends LinearLayout {
    protected final List<T> mDataList = new ArrayList<>();
    protected MPtrClassicFrameLayout mPtrLoadMore;
    protected RecyclerView mRecycleView;
    protected RecyclerView.Adapter mAdapter;
    protected ErrorManager mErrorManager;

    protected int mPageIndex = 1;   // 分页 index

    public List<T> getDataList() {
        return mDataList;
    }

    public MPtrClassicFrameLayout getPtrLoadMore() {
        return mPtrLoadMore;
    }

    public RecyclerView getRecycleView() {
        return mRecycleView;
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    public ErrorManager getErrorManager() {
        return mErrorManager;
    }

    public int getPageIndex() {
        return mPageIndex;
    }

    private Lifecycle<T> mLifecycle;

    public AlpsPtrListLayout<T> setLifecycle(Lifecycle<T> lifecycle) {
        mLifecycle = lifecycle;
        initView();
        return this;
    }

    /**
     * 对外的生命周期接口。initView -> loadData -> notifyDataSetChanged
     * @param <T>   列表数据类型
     */
    public static abstract class Lifecycle<T> {
        protected AlpsPtrListLayout<T> rootView;

        public Lifecycle(AlpsPtrListLayout<T> rootView) {
            this.rootView = rootView;
        }

        /**
         * 拉取列表数据, 成功后调用 {@link #addPageIndex()}
         * @param isLoadMore 是: loadmore, 否: pullToRefresh
         * @param lastItem  loadmore? (lastItem in mDataList) : null
         * @param pageIndex 从 0 开始
         */
        protected void loadData(boolean isLoadMore, T lastItem, int pageIndex) {
            rootView.mPageIndex = pageIndex;
        }

        /**
         * 回调成功，增加 mPageIndex
         */
        protected void addPageIndex(){
            rootView.mPageIndex ++;
        }

        protected void initView() {
            rootView.mRecycleView =(RecyclerView)rootView.findViewById(R.id.listView);
            rootView.mPtrLoadMore = (MPtrClassicFrameLayout)rootView.findViewById(R.id.ptr_load_more);
            if(rootView.mErrorManager == null)
                rootView.mErrorManager = new ErrorManager((View)rootView.mRecycleView.getParent());

            rootView.mRecycleView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
            rootView.mAdapter = getAdapter();
            if(rootView.mAdapter != null) {
                rootView.mRecycleView.setAdapter(rootView.mAdapter);
            }else{
                // mPtrLoadMore.setLoadMoreEnable() 依赖于 mAdapter, 为空必须 return
                return ;
            }

            rootView.mPtrLoadMore.setPtrHandler(new PtrRvHandler(rootView.mRecycleView) {
                @Override
                public void onRefreshBegin(PtrFrameLayout frame) {
                    rootView.loadData();
                }
            });
            rootView.mPtrLoadMore.setLoadMoreEnable(true);
            rootView.mPtrLoadMore.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void loadMore() {
                    if (rootView.mDataList.size() > 0)
                        rootView.loadData(true, rootView.mDataList.get(rootView.mDataList.size()-1), rootView.mPageIndex);
                }
            });
        }

        protected abstract RecyclerView.Adapter getAdapter();

        protected void notifyDataSetChanged(List<T> dataList) {
            List<T> dataCopy = new ArrayList<>(dataList);
            rootView.getDataList().clear();
            rootView.getDataList().addAll(dataCopy);
            rootView.mRecycleView.getAdapter().notifyDataSetChanged();
        }
    }

    public AlpsPtrListLayout(Context context) {
        this(context, null);
    }

    public AlpsPtrListLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlpsPtrListLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.g_ptr_list, this);
        mLifecycle = new Lifecycle<T>(this){
            @Override
            protected RecyclerView.Adapter getAdapter() {
                return null;
            }
        };
        initView();
    }

    /**
     * 拉取最新数据（首次加载/下拉刷新）
     */
    public final void loadData() {
        mLifecycle.loadData(false, null, 0);
    }

    public final void loadData(boolean isLoadMore, T lastItem, int pageIndex) {
        mLifecycle.loadData(isLoadMore, lastItem, pageIndex);
    }

    public final void initView() {
        mLifecycle.initView();
    }

    public final void notifyDataSetChanged(List<T> dataList) {
        mLifecycle.notifyDataSetChanged(dataList);
    }
}
