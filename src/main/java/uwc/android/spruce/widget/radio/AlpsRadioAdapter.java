package uwc.android.spruce.widget.radio;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import uwc.android.spruce.widget.adapter.ViewHolder;
import uwc.android.spruce.widget.adapter.recyclerview.CommonRvAdapter;

/**
 * 单选 adapter for any RecyclerView
 * created by steven
 */
public abstract class AlpsRadioAdapter<T> extends CommonRvAdapter<T> {
    public static final int NO_POS = -1;    // 无内容选中
    private @IdRes int mRadioId;
    private int mCurPos = NO_POS;           // 当前选中的位置
    private View mSubmitBtn;                // 提交按钮

    /**
     * 获取当前选中的位置
     * @return 无则返回 {@link #NO_POS}
     */
    public int getCurPos() {
        return mCurPos;
    }

    public AlpsRadioAdapter(Context context, @LayoutRes int layoutId, List<T> datas, @IdRes int radioId, View submitBtn) {
        super(context, layoutId, datas);
        mRadioId = radioId;
        mSubmitBtn = submitBtn;
    }

    @Override
    protected void convert(final ViewHolder viewHolder, final T item, final int position) {
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ViewGroup rv = (ViewGroup)v.getParent();
                int prePos = mCurPos, curPos = position;
                if(mCurPos == NO_POS){
                    rv.getChildAt(curPos).findViewById(mRadioId).setSelected(true);
                    mCurPos = curPos;           // 初次选择
                }else{
                    if(prePos == curPos){
                        rv.getChildAt(prePos).findViewById(mRadioId).setSelected(false);
                        mCurPos = NO_POS;       // 取消选择
                    }else{
                        rv.getChildAt(prePos).findViewById(mRadioId).setSelected(false);
                        rv.getChildAt(curPos).findViewById(mRadioId).setSelected(true);
                        mCurPos = curPos;       // 切换选择
                    }
                }

                // 如果没选中，不可打赏
                if(mSubmitBtn != null)
                    mSubmitBtn.setEnabled(mCurPos != NO_POS);

                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(viewHolder, mDatas.get(position), position);
                }
            }
        });
    }
}
