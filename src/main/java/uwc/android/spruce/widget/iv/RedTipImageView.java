package uwc.android.spruce.widget.iv;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.ImageView;

import uwc.android.spruce.util.Device;
import uwc.android.spruce.R;

/**
 * 右上角带圆点的ImageView
 * 需要设置 paddingRight 值 !!!
 */
public class RedTipImageView extends ImageView {
    public final static short invisible = 0;
    public final static short visible = 1;
    private int tipVisibility = invisible;

    public RedTipImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(null);
    }

    public RedTipImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(attrs);
    }

    public RedTipImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        if(attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.RedTip);
            tipVisibility = array.getInt(R.styleable.RedTip_visibility, invisible);
            array.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if(tipVisibility == visible) {
            int width = getWidth();
            int redBotMarginRight = Device.Display.dip2px(getContext(), 5);
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.red_dot));
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setStyle(Style.FILL_AND_STROKE);
            canvas.drawCircle(width-redBotMarginRight, redBotMarginRight, ((float)redBotMarginRight)*0.8f, paint);
        }
    }

    public void setDotVisibility(short visibility) {
        tipVisibility = visibility;
        invalidate();
    }
}
