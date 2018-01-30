package uwc.android.spruce.widget.input;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import uwc.android.spruce.R;

/**
 * 字数限制的EditText
 * Created by zhengcheng on 2017/5/24.
 */
public class CountEditText extends FrameLayout {

    private boolean isLimitEnable = true;
    private int maxLimit = 0;
    private EditText mEditText;
    private TextView mTextView;

    public CountEditText(Context context) {
        this(context, null);
    }

    public CountEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View holder = View.inflate(getContext(), R.layout.g_input_edit_with_count, this);
        mEditText = (EditText) holder.findViewById(R.id.et_content);
        mTextView = (TextView) holder.findViewById(R.id.tv_count);
        mEditText.addTextChangedListener(new EditContentChangedListener());
    }

    public void setLimitCount(int count) {
        maxLimit = count;
        mTextView.setText("0/" + count);
    }

    public void setLimitEnable(boolean isLimitEnable) {
        this.isLimitEnable = isLimitEnable;
        mTextView.setVisibility(isLimitEnable? View.VISIBLE: View.GONE);
    }

    public void setHeight(int dp) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = dp;
        setLayoutParams(params);
    }

    public EditText getEditText() {
        return mEditText;
    }

    public TextView getTextView() {
        return mTextView;
    }

    private class EditContentChangedListener implements TextWatcher {
        private int editStart;//光标开始位置
        private int editEnd;//光标结束位置
        private CharSequence tempContent;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            tempContent = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mTextView.setText((s.length()) + "/" + maxLimit);
            tempContent = s;
        }

        @Override
        public void afterTextChanged(Editable s) {
            /** 得到光标开始和结束位置 ,超过最大数后记录刚超出的数字索引进行控制 */
            editStart = mEditText.getSelectionStart();
            editEnd = mEditText.getSelectionEnd();
            if (isLimitEnable && tempContent.length() > maxLimit) {
                s.delete(editStart - 1, editEnd);
            }
        }
    }
}
