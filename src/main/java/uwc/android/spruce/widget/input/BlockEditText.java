package uwc.android.spruce.widget.input;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import uwc.android.spruce.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Arron on 2016/11/21 0021.
 * 分块输入框，每块一个字符. 可用于固定长度的验证码或密码输入.<br/>
 * copy from <a href="https://github.com/EoniJJ/PasswordView">PasswordView</a><br/>
 * <p>
 * 使用:
 * <span>
 * <BlockEditText
 *      android:layout_width="wrap_content"
 *      android:layout_height="wrap_content"
 *      app:cipherEnable="false"
 *      app:length="6"
 *      app:cursorColor="@android:color/black"
 *      app:hint="hint content"/>
 * </span>
 */

public class BlockEditText extends View {

    /**
     * 输入监听者
     */
    public interface InputWatcher {
        /**
         * 确认键后的回调
         *
         * @param inputs   输入内容
         * @param isComplete 是否达到要求位数
         */
        void keyEnterPress(String inputs, boolean isComplete);

    }


    public enum Appearance {
        /**
         * 下划线样式
         */
        UNDERLINE(0),

        /**
         * 边框样式
         */
        RECT(1);

        static Appearance fromMode(int mode) {
            for (Appearance m : values()) {
                if (mode == m.mode) {
                    return m;
                }
            }
            throw new IllegalArgumentException();
        }

        private int mode;

        Appearance(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return this.mode;
        }
    }

    private class InternalKeyListener implements OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            int action = event.getAction();
            if (KeyEvent.ACTION_DOWN != action || keyCode == KeyEvent.KEYCODE_BACK) {
                return false;
            }

            // 确认键
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if (mInputWatcher != null) {
                    mInputWatcher.keyEnterPress(getInputs(), mIsInputComplete);
                }
            }
            // 删除操作
            else if (keyCode == KeyEvent.KEYCODE_DEL && !TextUtils.isEmpty(mInputs[0])) {
                delete();
                if (mInputWatcher != null) {
                    mInputWatcher.keyEnterPress(getInputs(), mIsInputComplete);
                }
                postInvalidate();
            }
            //只支持数字
            else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9 && !mIsInputComplete) {
                add((keyCode - KeyEvent.KEYCODE_0) + "");
                if (mInputWatcher != null) {
                    mInputWatcher.keyEnterPress(getInputs(), mIsInputComplete);
                }
                postInvalidate();
            }

            return true;
        }
    }

    private static final String CIPHER_TEXT = "*"; //密文符号

    private Appearance appearance; //样式模式
    private int inputHintColor;//内容提示颜色
    private int inputSize;//内容符号大小
    private int inputColor;//输入内容颜色
    private int length;//输入内容字符总数
    private int padding;//每个字符间的间隔
    private int borderColor;//边框颜色
    private int borderWidth;//下划线粗细
    private int cursorWidth;//光标粗细
    private int cursorHeight;//光标长度
    private int cursorColor;//光标颜色
    private boolean isCursorShowing;//光标是否正在显示
    private boolean isCursorEnable;//是否开启光标
    private long cursorFlashTime;//光标闪动间隔时间
    private boolean cipherEnable;//是否开启密文

    private String hint;

    private InputMethodManager mInputManager;
    private InputWatcher mInputWatcher;
    private String[] mInputs;//输入数组
    private int mBlkSize;//单个字符大小
    private int cursorPosition;//光标位置
    private boolean mIsInputComplete;//是否输入完毕
    private Timer mTimer;

    private Paint mEditPaint;

    public BlockEditText(Context context) {
        this(context, null);
    }

    /**
     * 当前只支持从xml中构建该控件
     *
     * @param context
     * @param attrs
     */
    public BlockEditText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 当前只支持从xml中构建该控件
     *
     * @param context
     * @param attrs
     */
    public BlockEditText(Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttribute(context, attrs);
        init();
    }

    private void readAttribute(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BlockInputEdit);
            appearance = Appearance.fromMode(typedArray.getInteger(R.styleable.BlockInputEdit_appearance, Appearance.UNDERLINE.getMode()));
            //如果为边框样式，则padding 默认置为0
            inputSize = typedArray.getDimensionPixelSize(R.styleable.BlockInputEdit_textSize, context.getResources().getDimensionPixelOffset(R.dimen.ts_15));
            inputColor = typedArray.getColor(R.styleable.BlockInputEdit_textColor, context.getResources().getColor(R.color.g_foreground_text));
            inputHintColor = typedArray.getColor(R.styleable.BlockInputEdit_textColorHint, context.getResources().getColor(R.color.g_foreground_text_secondary));
            hint = typedArray.getString(R.styleable.BlockInputEdit_hint);
            padding = typedArray.getDimensionPixelSize(R.styleable.BlockInputEdit_padding, (appearance == Appearance.UNDERLINE? dp2px(8) : 0));
            length = typedArray.getInteger(R.styleable.BlockInputEdit_length, 4);
            cursorFlashTime = typedArray.getInteger(R.styleable.BlockInputEdit_cursorFlashTime, 500);
            borderWidth = typedArray.getDimensionPixelSize(R.styleable.BlockInputEdit_borderWidth, dp2px(1));
            borderColor = typedArray.getColor(R.styleable.BlockInputEdit_borderColor, context.getResources().getColor(R.color.g_divider));
            cursorColor = typedArray.getColor(R.styleable.BlockInputEdit_cursorColor, context.getResources().getColor(R.color.g_cursor));
            isCursorEnable = typedArray.getBoolean(R.styleable.BlockInputEdit_isCursorEnable, true);

            cipherEnable = typedArray.getBoolean(R.styleable.BlockInputEdit_cipherEnable, true);

            typedArray.recycle();
        }
        mInputs = new String[length];

    }

    private void init() {
        mInputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        mEditPaint = new Paint();
        mEditPaint.setAntiAlias(true);

        mTimer = new Timer();

        setOnKeyListener(new InternalKeyListener());
        setFocusableInTouchMode(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                //没有指定大小
                //单个密码框大小 = 输入字体大小 + 左右边距
                mBlkSize = inputSize + dp2px(6) * 2;
                //宽度 = 单个密码框大小 * 密码位数 + 密码框间距 *（密码位数 - 1）
                width = mBlkSize * length + padding * (length - 1);
                break;
            case MeasureSpec.EXACTLY:
                //指定大小
                mBlkSize = inputSize + dp2px(6) * 2;
                //密码框大小等于 (宽度 - 密码框间距 *(密码位数 - 1)) / 密码位数
                padding = (width - mBlkSize * length) / (length - 1);
                break;
        }
        //光标长度
        cursorHeight = inputSize;
        //光标宽度
        cursorWidth = dp2px(2);
        setMeasuredDimension(width, mBlkSize);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawAppearance(canvas, mEditPaint);
        //绘制提示
        drawHint(canvas, mEditPaint);
        //绘制输入文本
        drawInputs(canvas, mEditPaint);
        //绘制光标
        drawCursor(canvas, mEditPaint);
    }

    private void drawAppearance(Canvas canvas, Paint paint) {
        if (appearance == Appearance.RECT) {
            drawRect(canvas, paint); //绘制方框
            return;
        }
        //绘制下划线
        drawUnderLine(canvas, paint);
    }

    /**
     * 删除
     */
    private String delete() {
        String deleteText = null;
        if (cursorPosition > 0) {
            deleteText = mInputs[cursorPosition - 1];
            mInputs[cursorPosition - 1] = null;
            cursorPosition--;
        } else if (cursorPosition == 0) {
            deleteText = mInputs[cursorPosition];
            mInputs[cursorPosition] = null;
        }
        mIsInputComplete = false;
        return deleteText;
    }

    /**
     * 增加
     */
    private String add(String c) {
        String addText = null;
        if (cursorPosition < length) {
            addText = c;
            mInputs[cursorPosition] = c;
            cursorPosition++;
            if (cursorPosition == length) {
                mIsInputComplete = true;
            }
        }
        return addText;
    }

    /**
     * 获取密码
     */
    private String getInputs() {
        StringBuffer stringBuffer = new StringBuffer();
        for (String c : mInputs) {
            if (TextUtils.isEmpty(c)) {
                continue;
            }
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }


    /**
     * 绘制输入，密码状态下，使用替代符
     *
     * @param canvas
     * @param paint
     */
    private void drawInputs(Canvas canvas, Paint paint) {
        //画笔初始化
        paint.setColor(inputColor);
        paint.setTextSize(inputSize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
        //文字居中的处理
        Rect r = new Rect();
        canvas.getClipBounds(r);
        int cHeight = r.height();
        paint.getTextBounds(CIPHER_TEXT, 0, CIPHER_TEXT.length(), r);
        float y = cHeight / 2f + r.height() / 2f - r.bottom;

        //根据输入的密码位数，进行for循环绘制
        for (int i = 0; i < mInputs.length; i++) {
            if (!TextUtils.isEmpty(mInputs[i])) {
                // x = paddingLeft + 单个密码框大小/2 + ( 密码框大小 + 密码框间距 ) * i
                // y = paddingTop + 文字居中所需偏移量
                if (cipherEnable) {
                    //没有开启明文显示，绘制密码密文
                    canvas.drawText(CIPHER_TEXT,
                            (getPaddingLeft() + mBlkSize / 2) + (mBlkSize + padding) * i,
                            getPaddingTop() + y, paint);
                } else {
                    //明文显示，直接绘制密码
                    canvas.drawText(mInputs[i],
                            (getPaddingLeft() + mBlkSize / 2) + (mBlkSize + padding) * i,
                            getPaddingTop() + y, paint);
                }
            }
        }
    }

    /**
     * 绘制输入提示
     *
     * @param canvas
     * @param paint
     */
    private void drawHint(Canvas canvas, Paint paint) {
        if(isFocused() || 0 < cursorPosition || TextUtils.isEmpty(hint))
            return;
        //画笔初始化
        paint.setColor(inputHintColor);
        paint.setTextSize(inputSize);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStyle(Paint.Style.FILL);
        //文字居中的处理
        Rect r = new Rect();canvas.getClipBounds(r);
        int cHeight = r.height();
        paint.getTextBounds(hint, 0, hint.length(), r);
        float y = cHeight / 2f + r.height() / 2f - r.bottom;

        canvas.drawText(hint, getPaddingLeft(), getPaddingTop() + y, paint);
    }

    /**
     * 绘制光标
     *
     * @param canvas
     * @param paint
     */
    private void drawCursor(Canvas canvas, Paint paint) {
        //画笔初始化
        paint.setColor(cursorColor);
        paint.setStrokeWidth(cursorWidth);
        paint.setStyle(Paint.Style.FILL);
        //光标未显示 && 开启光标 && 输入位数未满 && 获得焦点
        if (!isCursorShowing && isCursorEnable && !mIsInputComplete && hasFocus()) {
            // 起始点x = paddingLeft + 单个密码框大小 / 2 + (单个密码框大小 + 密码框间距) * 光标下标
            // 起始点y = paddingTop + (单个密码框大小 - 光标大小) / 2
            // 终止点x = 起始点x
            // 终止点y = 起始点y + 光标高度
            canvas.drawLine((getPaddingLeft() + mBlkSize / 2) + (mBlkSize + padding) * cursorPosition,
                    getPaddingTop() + (mBlkSize - cursorHeight) / 2,
                    (getPaddingLeft() + mBlkSize / 2) + (mBlkSize + padding) * cursorPosition,
                    getPaddingTop() + (mBlkSize + cursorHeight) / 2,
                    paint);
        }
    }

    /**
     * 绘制密码框下划线
     *
     * @param canvas
     * @param paint
     */
    private void drawUnderLine(Canvas canvas, Paint paint) {
        //画笔初始化
        paint.setColor(borderColor);
        paint.setStrokeWidth(borderWidth);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < length; i++) {
            //根据密码位数for循环绘制直线
            // 起始点x为paddingLeft + (单个密码框大小 + 密码框边距) * i , 起始点y为paddingTop + 单个密码框大小
            // 终止点x为 起始点x + 单个密码框大小 , 终止点y与起始点一样不变
            canvas.drawLine(getPaddingLeft() + (mBlkSize + padding) * i, getPaddingTop() + mBlkSize,
                    getPaddingLeft() + (mBlkSize + padding) * i + mBlkSize, getPaddingTop() + mBlkSize,
                    paint);
        }
    }

    private void drawRect(Canvas canvas, Paint paint) {
        paint.setColor(borderColor);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.STROKE);
        Rect rect;
        for (int i = 0; i < length; i++) {
            int startX = getPaddingLeft() + (mBlkSize + padding) * i;
            int startY = getPaddingTop();
            int stopX = getPaddingLeft() + (mBlkSize + padding) * i + mBlkSize;
            int stopY = getPaddingTop() + mBlkSize;
            rect = new Rect(startX, startY, stopX, stopY);
            canvas.drawRect(rect, paint);
        }
    }

    private int dp2px(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private int sp2px(float spValue) {
        float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            requestFocus();
            mInputManager.showSoftInput(this, InputMethodManager.SHOW_FORCED);
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus)
            return;
        mInputManager.hideSoftInputFromWindow(this.getWindowToken(), 0);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER; //输入类型为数字
        return super.onCreateInputConnection(outAttrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //cursorFlashTime为光标闪动的间隔时间
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                isCursorShowing = !isCursorShowing;postInvalidate();
            }
        }, 0, cursorFlashTime);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTimer.cancel();
    }



    public Appearance getAppearance() {
        return appearance;
    }

    public void setAppearance(Appearance appearance) {
        this.appearance = appearance;
        postInvalidate();
    }

    public void setInputWatcher(InputWatcher inputWatcher) {
        this.mInputWatcher = inputWatcher;
    }

    public void setLength(int Length) {
        this.length = Length;
        postInvalidate();
    }

    public void setCursorColor(int cursorColor) {
        this.cursorColor = cursorColor;
        postInvalidate();
    }

    public void setCursorEnable(boolean cursorEnable) {
        isCursorEnable = cursorEnable;
        postInvalidate();
    }

    public void setCipherEnable(boolean cipherEnable) {
        this.cipherEnable = cipherEnable;
        postInvalidate();
    }



    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putStringArray("inputs", mInputs);
        bundle.putInt("cursorPosition", cursorPosition);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mInputs = bundle.getStringArray("inputs");
            cursorPosition = bundle.getInt("cursorPosition");
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }
}

