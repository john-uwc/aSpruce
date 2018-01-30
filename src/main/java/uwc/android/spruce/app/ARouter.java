package uwc.android.spruce.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Created by steven on 30/10/2017.
 */

public class ARouter {
    private final static String TAG = ARouter.class.getSimpleName();

    public static void handle(Context context, String action) {
        try {
            Uri uri = Uri.parse(action);
            if (uri.getScheme().startsWith("http")) {
                uri = Uri.parse("alps-" + uri.toString());
            }
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}
