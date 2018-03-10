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
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import uwc.core.util.Validator;
import uwc.android.spruce.R;

/**
 * Created by Administrator on 2017/5/4.
 */

public class PhoneNumberEditText extends FrameLayout implements InputEdit, TextWatcher, View.OnClickListener {
    private final String phoneNumberRule = "0123456789";

    private ImageView vAction;
    private EditText etInput;
    private OnInputListener mListener;

    private String mHint;

    public PhoneNumberEditText(@NonNull Context context) {
        this(context, null);
    }

    public PhoneNumberEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhoneNumberEditText(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(null != attrs){
            TypedArray typed = context.obtainStyledAttributes(attrs
                    , R.styleable.PhoneNumberInputEdit);
            mHint = typed.getString(R.styleable.PhoneNumberInputEdit_hint);
            typed.recycle();
        }
        addView(createView(context));
    }

    private View createView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.g_input_edit, null);
        vAction = (ImageView) view.findViewById(R.id.iv_action);
        vAction.setImageResource(R.mipmap.icon_cross);
        vAction.setOnClickListener(this);
        vAction.setVisibility(View.INVISIBLE);
        etInput = (EditText) view.findViewById(R.id.et_input);
        etInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        etInput.setKeyListener(DigitsKeyListener.getInstance(phoneNumberRule));
        etInput.setFilters(new InputFilter[] {new InputFilter.LengthFilter(11)});
        etInput.setMaxLines(1);
        etInput.setHint(mHint);
        etInput.addTextChangedListener(this);
        return view;
    }

    @Override
    public boolean checkContentIfPass() {
        return Validator.checkPhoneNumber(etInput.getText().toString());
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
        if(null != mListener)
            mListener.onSucceed(checkContentIfPass());

        vAction.setVisibility(s.length()>0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        etInput.setText("");
    }

    public View setInputHit(String hint) {
        etInput.setHint(mHint = hint);
        return this;
    }
}
