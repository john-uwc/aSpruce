package uwc.android.spruce.widget.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.ILoadMoreViewFactory;
import uwc.android.spruce.R;

import java.lang.reflect.Field;
import java.util.List;


/**
 * <a href="https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh/issues/282">解决下拉刷新与水平滑动的冲突</a>
 * Created by xujunhe on 16-12-19.
 *
 * disableWhenHorizontalMove() 默认设为 true
 */

public class MPtrClassicFrameLayout extends PtrClassicFrameLayout {
    private int mScrollableViewId = View.NO_ID;

    public MPtrClassicFrameLayout(Context context) {
        this(context, null);
    }

    public MPtrClassicFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MPtrClassicFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        disableWhenHorizontalMove(true);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledPagingTouchSlop();

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.MPtrClassicFrameLayout, 0, 0);
        if (arr != null) {
            mScrollableViewId = arr.getResourceId(R.styleable.MPtrClassicFrameLayout_ptr_scrollable_view_id, mScrollableViewId);
            arr.recycle();
        }

        PtrRefreshHeader refreshHeader = new PtrRefreshHeader(getContext());
        setHeaderView(refreshHeader);
        addPtrUIHandler(refreshHeader);
        setFooterView(new PtrLoadMoreFooter.DefaultFactory(getContext()));
    }

    private float startY;
    private float startX;
    // 记录viewPager是否拖拽的标记
    private boolean mIsHorizontalMove;
    // 记录事件是否已被分发
    private boolean isDeal;
    private boolean needHorizontalMove;
    private int mTouchSlop;

    @Override
    public void disableWhenHorizontalMove(boolean disable) {
        super.disableWhenHorizontalMove(disable);
        this.needHorizontalMove = disable;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!needHorizontalMove)  return super.dispatchTouchEvent(ev);
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 记录手指按下的位置
                startY = ev.getY();
                startX = ev.getX();
                // 初始化标记
                mIsHorizontalMove = false;
                isDeal = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // 如果已经判断出是否由横向还是纵向处理，则跳出
                if (isDeal) {
                    break;
                }
                /**拦截禁止交给Ptr的 dispatchTouchEvent处理**/
                mIsHorizontalMove = true;
                // 获取当前手指位置
                float endY = ev.getY();
                float endX = ev.getX();
                float distanceX = Math.abs(endX - startX);
                float distanceY = Math.abs(endY - startY);
//                Log.d("MPtrClassicFrameLayout", "distanceX:" + distanceX + ", distanceY: " + distanceY + ", mTouchSlop: " + mTouchSlop);
                if (distanceX != distanceY) {
                    // 如果X轴位移大于Y轴位移，那么将事件交给右滑控件处理。
                    if (distanceX > mTouchSlop && distanceX > distanceY) {
                        mIsHorizontalMove = true;
                        isDeal = true;
                    } else if (distanceY > mTouchSlop) {
                        mIsHorizontalMove = false;
                        isDeal = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //下拉刷新状态时如果滚动了右滑控件 此时mIsHorizontalMove为true 会导致PtrFrameLayout无法恢复原位
                // 初始化标记,
                mIsHorizontalMove = false;
                isDeal = false;
                break;
        }

//        Log.d("MPtrClassicFrameLayout", "ev.getAction():" + ev.getAction() + ", mIsHorizontalMove:" + mIsHorizontalMove + ", isDeal: " + isDeal);
        if (mIsHorizontalMove) {
            return dispatchTouchEventSupper(ev);
        }
        return super.dispatchTouchEvent(ev);
    }


    boolean whileSetLoadMoreEnable = false;

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        whileSetLoadMoreEnable = true;
        super.setLoadMoreEnable(loadMoreEnable);
        whileSetLoadMoreEnable = false;
    }

    @Override
    public View getContentView() {
        if(mScrollableViewId != NO_ID && findViewById(mScrollableViewId) != null) {
            if (whileSetLoadMoreEnable) {
                return findViewById(mScrollableViewId);
            }
        }
        return super.getContentView();
    }

    /**
     * 根据 dataList 的长度, 决定是否有更多页面
     *
     * @param dataList
     */
    public void loadMoreComplete(List<?> dataList){
        boolean hasMore = dataList != null && !dataList.isEmpty();
        loadMoreComplete(hasMore);
    }

    /**
     * 根据 dataList 的长度, 决定是否有更多页面
     *
     * @param dataList
     */
    public void loadMoreComplete(Object[] dataList){
        boolean hasMore = dataList != null && dataList.length != 0;
        loadMoreComplete(hasMore);
    }

    public void loadFail(int height) {
        ILoadMoreViewFactory.ILoadMoreView footerView = null;
        try {
            Field feild = PtrFrameLayout.class.getDeclaredField("isLoadingMore");
            feild.setAccessible(true);
            feild.setBoolean(this, false);

            feild = PtrFrameLayout.class.getDeclaredField("isLoadMoreEnable");
            feild.setAccessible(true);
            feild.setBoolean(this, false);

            feild = PtrFrameLayout.class.getDeclaredField("mLoadMoreView");
            feild.setAccessible(true);
            footerView = (ILoadMoreViewFactory.ILoadMoreView) feild.get(this);
            if(footerView != null) {
                View footer = (View)footerView;
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        height
                );
                footer.setLayoutParams(lp);

                footerView.showFail(null);
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
