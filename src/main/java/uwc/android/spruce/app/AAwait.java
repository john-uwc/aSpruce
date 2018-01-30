package uwc.android.spruce.app;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import uwc.android.spruce.R;

/**
 * Created by steven on 17/4/26.
 */
class AAwait extends Dialog {
    public View itemView;

    public AAwait(Context context, int dialog) {
        super(context, dialog);
    }

    public static class Builder {
        private Context mContext;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public AAwait build() {
            View itemView = View.inflate(mContext, R.layout.dialog_progress_await, null);
            final AAwait dialog = new AAwait(mContext, R.style.Dialog);
            dialog.itemView = itemView;
            dialog.addContentView(itemView, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            );

            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0f;
            window.setAttributes(params);

            dialog.setCanceledOnTouchOutside(false);

            return dialog;
        }
    }

    //旋转动画
    private void startRotateAnimation(final View view) {
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(-1);
        LinearInterpolator lin = new LinearInterpolator();
        rotateAnimation.setInterpolator(lin);
        view.startAnimation(rotateAnimation);
    }

    @Override
    public void show() {
        super.show();
        startRotateAnimation(itemView.findViewById(R.id.iv_loading));
    }
}
