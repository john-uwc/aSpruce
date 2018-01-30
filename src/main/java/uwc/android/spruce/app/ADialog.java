package uwc.android.spruce.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import uwc.android.spruce.R;

/**
 * Created by Chexiangjia-MAC on 17/5/2.
 */

public class ADialog extends Dialog implements TitleCallBack{
    protected final String TAG = getClass().getSimpleName();

    protected Context context;
    private int mGravity = Gravity.CENTER;
    private int defaultPadwidth = 0;
    private int defaultPadHeight = 0;
    private boolean islockScreen = false;

    protected ATitleBar mTitleBar;

    public ADialog(Context context) {
        this(context, R.style.DialogNoAnimalPopup);
        setOwnerActivity((Activity) context);
        this.context = context;
    }
    public ADialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    /**
     * @param id      布局资源
     * @param oleft   title 左边内容
     * @param ocenter title中间内容
     * @param oright  title 右边内容
     */
    public void setContentView(int id, Object oleft, Object ocenter, Object oright) {
        final LinearLayout frame = new LinearLayout(context);
        frame.setOrientation(LinearLayout.VERTICAL);
        ATitleBar titleBar = new ATitleBar(context).setCallback(this);
        titleBar.resetWith(oleft, ocenter, oright);
        frame.addView(mTitleBar = titleBar);
        try {
            frame.addView(LayoutInflater.from(context).inflate(id, null, false));
        } catch (Exception e) {
        }
        setContentView(frame);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIAgent.fixFontScale(context);
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                WindowManager windowManager = ((Activity) context).getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.width = (int) (display.getWidth()) - setPadWidth(defaultPadwidth); //设置宽度
//                lp.height = (int) (display.getHeight()) - setPadHeight(defaultPadHeight);
                getWindow().setAttributes(lp);
                getWindow().setGravity(setGravity(mGravity));
                setCancelable(isCancelable(true));
            }
        });
    }

    /**
     * dialog按返回键和空白处是否可以消失
     *
     * @param b
     * @return
     */
    public boolean isCancelable(boolean b){
        return true;
    }

    /**
     * 设置dialog的Gravity属性
     * 最终停留的位置
     * @param gravity
     * @return
     */
    public int setGravity(int gravity){
        return Gravity.BOTTOM;
    }

    /**
     * 设置dialog横向距离边缘的长度
     *
     * @param width
     * @return
     */
    public int setPadWidth(float width){
        return 0;
    }

    /**
     * 设置dialog纵向距离边沿的长度
     *
     * @param height
     * @return
     */
    public int setPadHeight(float height){
        return 0;
    }


    public void onLeftClick() {
    }

    public void onRightClick() {
    }

    public void setRightTextBackground(TextView ab_tv_right) {

    }

    public void setTitleBarBackground(View view) {

    }

    public void setCenterTextSize(TextView view) {

    }

    public void setCenterTextColor(TextView textview) {

    }

    public void setCenterTextContent(TextView view) {

    }
}
