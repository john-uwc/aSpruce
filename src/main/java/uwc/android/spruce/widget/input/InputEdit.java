package uwc.android.spruce.widget.input;

/**
 * Created by Administrator on 2017/5/4.
 */

public interface InputEdit {
    /**
     * Created by Administrator on 2017/5/4.
     */

    interface OnInputListener {
        void onSucceed(boolean isSucceed);
    }

    String getContent();

    boolean checkContentIfPass();

    void setInputListener(OnInputListener onInputListener);

    /**
     * Created by steven on 14/11/2017.
     */
    interface Host {

        void addView(InputEdit inputEdit);
    }
}
