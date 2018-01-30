package uwc.android.spruce.app;

import android.view.View;
import android.widget.TextView;

/**
 * baseactivity 必须初始化的方法集合
 * Created by admin on 2017/4/22.
 */

public interface TitleCallBack {
    void onLeftClick();

    void onRightClick();

    void setRightTextBackground(TextView ab_tv_right);

    void setTitleBarBackground(View view);

    void setCenterTextSize(TextView view);

    void setCenterTextColor(TextView textview);

    void setCenterTextContent(TextView view);

    TitleCallBack EMPTY = new TitleCallBack() {
        @Override
        public void onLeftClick() {}

        @Override
        public void onRightClick() {}

        @Override
        public void setRightTextBackground(TextView ab_tv_right) {}

        @Override
        public void setTitleBarBackground(View view) {}

        @Override
        public void setCenterTextSize(TextView view) {}

        @Override
        public void setCenterTextColor(TextView textview) {}

        @Override
        public void setCenterTextContent(TextView view) {}
    };
}
