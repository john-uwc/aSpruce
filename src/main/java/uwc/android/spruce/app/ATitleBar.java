package uwc.android.spruce.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import uwc.android.spruce.R;
import uwc.android.spruce.view.OnClick;

/**
 * Created by fengdongfei on 17/4/25.
 */
public class ATitleBar extends FrameLayout {
    private TitleCallBack callback = TitleCallBack.EMPTY;
    private Object oleft;
    private Object ocenter;
    private Object oright;

    private ImageView leftImageView, centerImageView, rightImageView;
    private TextView leftTextView, centerTextView, rightTextView;
    private View underline;
    private View vRoot;

    public View getRoot() {
        return vRoot;
    }

    /**
     * 修改属性
     */
    public void resetWith(Object oleft, Object ocenter, Object oright) {
        this.oleft = oleft;
        this.ocenter = ocenter;
        this.oright = oright;
        assignView();
    }

    /**
     * 设置下划线是否显示
     *
     * @param isShow
     */
    public void setUnderlineVisibility(boolean isShow) {
        underline.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 获取title左边的文本控件
     *
     * @return
     */
    public TextView getLeftTextView() {
        return leftTextView;
    }

    /**
     * 获取title左边的图片控件
     *
     * @return
     */
    public ImageView getLeftImageView() {
        return leftImageView;
    }

    /**
     * 获取title中间的文字控件
     *
     * @return
     */
    public TextView getCenterTextView() {
        return centerTextView;
    }

    /**
     * 获取title中间的图片控件
     *
     * @return
     */
    public ImageView getCenterImageView() {
        return centerImageView;
    }

    /**
     * 获取title右边的文本控件
     *
     * @return
     */
    public TextView getRightTextView() {
        return rightTextView;
    }

    /**
     * 获取title右边的图片控件
     *
     * @return
     */
    public ImageView getRightImageView() {
        return rightImageView;
    }

    public void setLeftImageView(ImageView leftImageView) {
        replace(this.leftImageView, leftImageView);
        this.leftImageView = leftImageView;
    }

    public void setCenterImageView(ImageView centerImageView) {
        replace(this.centerImageView, centerImageView);
        this.centerImageView = centerImageView;
    }

    public void setRightImageView(ImageView rightImageView) {
        replace(this.rightImageView, rightImageView);
        this.rightImageView = rightImageView;
    }

    public void setLeftTextView(TextView leftTextView) {
        replace(this.leftTextView, leftTextView);
        this.leftTextView = leftTextView;
    }

    public void setCenterTextView(TextView centerTextView) {
        replace(this.centerTextView, centerTextView);
        this.centerTextView = centerTextView;
    }

    public void setRightTextView(TextView rightTextView) {
        replace(this.rightTextView, rightTextView);
        this.rightTextView = rightTextView;
    }

    private void replace(View oldChild, View newChild) {
        ViewGroup parent = ((ViewGroup) oldChild.getParent());
        int index = parent.indexOfChild(oldChild);
        parent.removeViewAt(index);
        newChild.setPadding(
                oldChild.getPaddingLeft(),
                oldChild.getPaddingTop(),
                oldChild.getPaddingRight(),
                oldChild.getPaddingBottom()
        );
        parent.addView(newChild, index);
    }

    public ATitleBar setCallback(TitleCallBack callback) {
        this.callback = callback;
        return this;
    }

    public ATitleBar(@NonNull Context context) {
        this(context, null);
    }

    public ATitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ATitleBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        vRoot = LayoutInflater.from(getContext()).inflate(R.layout.g_activity_titlebar, this, true);
        leftTextView = (TextView) vRoot.findViewById(R.id.ab_tv_left);
        leftImageView = (ImageView) vRoot.findViewById(R.id.ab_iv_left);
        centerImageView = (ImageView) vRoot.findViewById(R.id.ab_iv_center);
        centerTextView = (TextView) vRoot.findViewById(R.id.ab_tv_center);
        rightImageView = (ImageView) vRoot.findViewById(R.id.ab_iv_right);
        rightTextView = (TextView) vRoot.findViewById(R.id.ab_tv_right);
        underline = findViewById(R.id.underline);
        assignView();
    }

    private void assignView() {
        if (!(oleft != null || oright != null || ocenter != null)) {
            vRoot.setVisibility(GONE);
            return;
        }

        vRoot.setVisibility(VISIBLE);
        leftTextView.setVisibility(View.GONE);
        leftImageView.setVisibility(View.GONE);
        rightTextView.setVisibility(View.GONE);
        rightImageView.setVisibility(View.GONE);
        centerTextView.setVisibility(View.GONE);
        centerImageView.setVisibility(View.GONE);

        vRoot.setBackgroundResource(R.color.g_background_title);

        callback.setTitleBarBackground(vRoot);
        callback.setCenterTextSize(centerTextView);
        callback.setCenterTextColor(centerTextView);
        callback.setCenterTextContent(centerTextView);
        // 左
        if (oleft instanceof String) {
            leftTextView.setText(oleft.toString());
            leftTextView.setVisibility(View.VISIBLE);
            onLeftClick(leftTextView);
        }
        if (oleft instanceof Integer) {
            leftImageView.setImageResource((Integer) oleft);
            leftImageView.setVisibility(View.VISIBLE);
            onLeftClick(leftImageView);
        }

        // 右
        if (oright instanceof String) {
            rightTextView.setText(oright.toString());
            rightTextView.setVisibility(View.VISIBLE);
            callback.setRightTextBackground(rightTextView);
            onRightClick(rightTextView);
        }
        if (oright instanceof Integer) {
            rightImageView.setImageResource((Integer) oright);
            rightImageView.setVisibility(View.VISIBLE);
            onRightClick(rightImageView);
        }

        // 中
        if (ocenter instanceof String) {
            centerTextView.setText(ocenter.toString());
            centerTextView.setVisibility(View.VISIBLE);
        }
        if (ocenter instanceof Integer) {
            centerImageView.setVisibility(View.VISIBLE);
            centerImageView.setImageResource((Integer) ocenter);
        }
    }

    /**
     * 左边的监听
     */
    private void onLeftClick(View view) {
        view.setOnClickListener(new OnClick() {
            @Override
            public void doClick(View v) {
                if (null == callback)
                    return;
                callback.onLeftClick();
            }
        });
    }

    /**
     * 右边的监听
     */
    private void onRightClick(View view) {
        view.setOnClickListener(new OnClick() {
            @Override
            public void doClick(View v) {
                if (null == callback)
                    return;
                callback.onRightClick();
            }
        });
    }
}
