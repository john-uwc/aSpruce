package uwc.android.spruce.widget.rv;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import uwc.android.spruce.R;

/**
 * 普通列表
 * @param <T>
 */
public class AlpsListLayout<T> extends LinearLayout {
    protected final List<T> mDataList = new ArrayList<>();
    protected RecyclerView mRecycleView;
    protected RecyclerView.Adapter mAdapter;

    public List<T> getDataList() {
        return mDataList;
    }

    public RecyclerView getRecycleView() {
        return mRecycleView;
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    private Lifecycle<T> mLifecycle;

    public AlpsListLayout<T> setLifecycle(Lifecycle<T> lifecycle) {
        mLifecycle = lifecycle;
        initView();
        return this;
    }

    /**
     * 对外的生命周期接口。initView -> loadData -> notifyDataSetChanged
     * @param <T>   列表数据类型
     */
    public static abstract class Lifecycle<T> {
        protected AlpsListLayout<T> rootView;

        public Lifecycle(AlpsListLayout<T> rootView) {
            this.rootView = rootView;
        }

        /**
         * 拉取列表数据
         */
        protected void loadData() {}

        protected void initView() {
            rootView.mRecycleView =(RecyclerView)rootView.findViewById(R.id.listView);
            rootView.mRecycleView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
            rootView.mAdapter = getAdapter();
            if(rootView.mAdapter != null) {
                rootView.mRecycleView.setAdapter(rootView.mAdapter);
            }
        }

        protected abstract RecyclerView.Adapter getAdapter();

        protected void notifyDataSetChanged(List<T> dataList) {
            List<T> dataCopy = new ArrayList<>(dataList);
            rootView.getDataList().clear();
            rootView.getDataList().addAll(dataCopy);
            rootView.mRecycleView.getAdapter().notifyDataSetChanged();
        }
    }

    public AlpsListLayout(Context context) {
        this(context, null);
    }

    public AlpsListLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlpsListLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.g_list, this);
        mLifecycle = new Lifecycle<T>(this){
            @Override
            protected RecyclerView.Adapter getAdapter() {
                return null;
            }
        };
        initView();
    }

    /**
     * 拉取列表数据
     */
    public final void loadData() {
        mLifecycle.loadData();
    }

    public final void initView() {
        mLifecycle.initView();
    }

    public final void notifyDataSetChanged(List<T> dataList) {
        mLifecycle.notifyDataSetChanged(dataList);
    }
}
