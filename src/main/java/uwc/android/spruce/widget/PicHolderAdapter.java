package uwc.android.spruce.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import uwc.android.spruce.util.Compressor;
import uwc.android.spruce.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fml on 2015/12/3 0003.
 */
public class PicHolderAdapter extends RecyclerView.Adapter<PicHolderAdapter.ViewHolder> {
    public interface OnItemListener {
        void onItemClick(View v, int position);

        void onItemLongClick(View v, int position);

        void onRemove(View view, int position);
    }

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private int mSize;
    private int mCol;
    private int mAddRes;
    private Map<String, Bitmap> mOnLoads = new HashMap<>();
    private OnItemListener mOnItemListener;
    private List<String> mDatas = new ArrayList<>();

    public void setOnItemListener(OnItemListener listener) {
        this.mOnItemListener = listener;
    }

    public void setData(List<String> data) {
        if (null == data)
            data = new ArrayList<>();
        mDatas = data;
        for (Map.Entry<String, Bitmap> entry : mOnLoads.entrySet()) {
            if (null == entry.getValue() || entry.getValue().isRecycled())
                continue;
            entry.getValue().recycle();
        }
    }

    public PicHolderAdapter(Context context, int size, int col, int resAdd) {
        this.mContext = context;
        this.mSize = Math.max(1, size);
        this.mCol = Math.max(1, Math.min(col, this.mSize));
        this.mAddRes = resAdd;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.widget_choser_picture_item, parent, false);

        //设置的item宽度是屏幕的1/mCol
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        lp.width = (parent.getMeasuredWidth() - (parent.getPaddingLeft() + parent.getPaddingRight())) / mCol;
        lp.height = lp.width;
        v.setLayoutParams(lp);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position > mDatas.size()) {
            holder.itemView.setVisibility(View.GONE);
            return;
        }

        Bitmap cell = BitmapFactory.decodeResource(mContext.getResources(), mAddRes);
        if (position < mDatas.size()) {
            if (!mOnLoads.containsKey(mDatas.get(position))) {
                if (null != (cell =
                        Compressor.compressBySize(mDatas.get(position), holder.itemView.getLayoutParams().width, holder.itemView.getLayoutParams().height)))
                    mOnLoads.put(mDatas.get(position), cell);
            }
            cell = mOnLoads.get(mDatas.get(position));
        }

        holder.ivCell.setImageBitmap(cell);
        holder.lActionCell.setVisibility(position < mDatas.size() ? View.VISIBLE : View.INVISIBLE);
        holder.itemView.setVisibility(View.VISIBLE);
        setListener(holder);
    }

    @Override
    public int getItemCount() {
        return mSize;
    }


    private void setListener(final ViewHolder holder) {
        if (null == mOnItemListener) {
            return;
        }
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int layoutPosition = holder.getPosition();
                mOnItemListener.onRemove(holder.itemView, layoutPosition);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int layoutPosition = holder.getPosition();
                mOnItemListener.onItemClick(holder.itemView, layoutPosition);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int layoutPosition = holder.getPosition();
                mOnItemListener.onItemLongClick(holder.itemView, layoutPosition);
                return true;
            }
        });
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout lActionCell;
        ImageView ivCell;
        Button btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            btnDelete = (Button) itemView.findViewById(R.id.item_text);
            ivCell = (ImageView) itemView.findViewById(R.id.iv_cell);
            lActionCell = (RelativeLayout) itemView.findViewById(R.id.rl_actions);
        }
    }
}
