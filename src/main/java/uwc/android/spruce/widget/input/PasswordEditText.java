package uwc.android.spruce.widget.input;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import uwc.android.spruce.R;

/**
 * Created by Administrator on 2017/5/4.
 */

public class PasswordEditText extends FrameLayout implements InputEdit, TextWatcher, View.OnFocusChangeListener, View.OnClickListener {

    private ImageView vAction;
    private EditText etInput;
    private OnInputListener mListener;

    private String mHint = "";
    private Boolean mCipherDisable = false;

    public PasswordEditText(@NonNull Context context) {
        this(context, null);
    }

    public PasswordEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordEditText(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (null != attrs) {
            TypedArray typed = context.obtainStyledAttributes(attrs
                    , R.styleable.PasswordInputEdit);
            mCipherDisable = typed.getBoolean(R.styleable.PasswordInputEdit_cipherDisable, false);
            mHint = typed.getString(R.styleable.PasswordInputEdit_hint);
            typed.recycle();
        }
        addView(createView(context));
    }

    private View createView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.g_input_edit, null);
        vAction = (ImageView) view.findViewById(R.id.iv_action);
        vAction.setImageResource(!mCipherDisable ? R.mipmap.icon_eye_off : R.mipmap.icon_cross);
        vAction.setOnClickListener(this);
        vAction.setVisibility(View.INVISIBLE);
        vAction.setSelected(false);
        etInput = (EditText) view.findViewById(R.id.et_input);
        etInput.addTextChangedListener(this);
        etInput.setInputType(InputType.TYPE_CLASS_TEXT);
        etInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        etInput.setMaxLines(1);
        etInput.setHint(mHint);
        etInput.setOnFocusChangeListener(this);
        return view;
    }

    @Override
    public boolean checkContentIfPass() {
        return etInput.getText().toString().length() >= 6 && etInput.getText().toString().length() <= 20;
    }

    public void setInputListener(OnInputListener onInputListener) {
        this.mListener = onInputListener;
    }

    @Override
    public String getContent() {
        return etInput.getText().toString();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (null != mListener)
            mListener.onSucceed(checkContentIfPass());

        vAction.setVisibility(s.length() > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public void onClick(View v) {
        vAction.setSelected(mCipherDisable ? v.isSelected() : !v.isSelected());
        etInput.setText(mCipherDisable ? "" : etInput.getText());
        setCipherDisable(mCipherDisable);
    }

    public View setInputHit(String hint) {
        etInput.setHint(mHint = hint);
        return this;
    }

    public View setCipherDisable(boolean disable) {
        mCipherDisable = disable;
        etInput.setTransformationMethod(!mCipherDisable ? (vAction.isSelected() ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance())
                : PasswordTransformationMethod.getInstance());
        vAction.setImageResource(!mCipherDisable ? (vAction.isSelected() ? R.mipmap.icon_eye_on : R.mipmap.icon_eye_off)
                : R.mipmap.icon_cross);
        etInput.setSelection(etInput.getText().length());
        return this;
    }
}
