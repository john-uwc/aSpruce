package uwc.android.spruce.widget.rv;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * created by steven
 */
public class RvUtils {

    /**
     * 水平的 recyclerview 滑动不触发 外层的 ViewPager 切换页面
     * https://stackoverflow.com/questions/38466413/disable-viewpager-paging-when-child-recyclerview-scrolls-to-last-item
     */
    public static void disableParentHorizontalScroll(RecyclerView recyclerView){
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            float mTouchSlop = 16;
            float preX, preY;

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        preX = e.getX();
                        preY = e.getY();
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float curX = e.getX();
                        float curY = e.getY();
                        float dx = Math.abs(curX - preX);
                        float dy = Math.abs(curY - preY);
                        if(dx > mTouchSlop || dy > mTouchSlop) {
                            rv.getParent().requestDisallowInterceptTouchEvent(dx > dy);
                        }
                        preX = curX;
                        preY = curY;
                        break;

                    case MotionEvent.ACTION_UP:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    default:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
    }
}
