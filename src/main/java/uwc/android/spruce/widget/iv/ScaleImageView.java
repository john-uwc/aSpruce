package uwc.android.spruce.widget.iv;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import uwc.android.spruce.R;

/**
 * Created by steven on 10/11/2017.
 */

public class ScaleImageView extends ImageView {
    private float mScaleW = 1.0f;
    private float mScaleH = 1.0f;

    public float getScaleW() {
        return mScaleW;
    }

    public void setScaleW(float scaleW) {
        mScaleW = scaleW;
        requestLayout();
    }

    public float getScaleH() {
        return mScaleH;
    }

    public void setScaleH(float scaleH) {
        mScaleH = scaleH;
        requestLayout();
    }

    public ScaleImageView(Context context) {
        this(context, null);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.ScaleImageView, 0, 0);
        if (arr != null) {
            mScaleW = arr.getFloat(R.styleable.ScaleImageView_scaleW, mScaleW);
            mScaleH = arr.getFloat(R.styleable.ScaleImageView_scaleH, mScaleH);
            arr.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((int)((width / mScaleW) * mScaleH), MeasureSpec.EXACTLY));
    }
}
