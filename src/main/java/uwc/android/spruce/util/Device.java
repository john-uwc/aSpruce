package uwc.android.spruce.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

import java.util.UUID;

/**
 * Created by steven on 15/05/2017.
 */

public class Device {
    /**
     * 显示相关总汇
     */
    public static class Display {

        // 根据手机的分辨率将dp的单位转成px(像素)
        public static int dip2px(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

        // 将sp值转换为px值
        public static int sp2px(Context context, float spValue) {
            final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
            return (int) (spValue * fontScale + 0.5f);
        }

        // 根据手机的分辨率将px(像素)的单位转成dp
        public static int px2dip(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }

        // 将px值转换为sp值
        public static int px2sp(Context context, float pxValue) {
            final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
            return (int) (pxValue / fontScale + 0.5f);
        }

        public static int getHeight(Context context) {
            WindowManager wm = ((Activity) context).getWindowManager();
            return wm.getDefaultDisplay().getHeight();
        }

        @SuppressWarnings("deprecation")
        public static int getWidth(Context context) {
            WindowManager wm = ((Activity) context).getWindowManager();
            return wm.getDefaultDisplay().getWidth();
        }
    }


    public static String touchId() {
        return "+" + SystemClock.elapsedRealtime();
    }


    public static String getDevicePlatform() {
        return "Android";
    }

    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getDeviceVersion() {
        return Build.VERSION.RELEASE;
    }

    @Nullable
    public static String getDeviceId(Context context) {
        String deviceId = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            String tmDevice;
            if (null != imei && !"".equals(imei)) {
                tmDevice = "^[0]+$";
                if (!imei.matches(tmDevice)) {
                    return imei;
                }
            }

            tmDevice = "" + tm.getDeviceId();
            String tmSerial = "" + tm.getSimSerialNumber();
            String androidId = "" + Settings.Secure.getString(context.getContentResolver(), "android_id");
            deviceId = new UUID((long) androidId.hashCode(), (long) tmDevice.hashCode() << 32 | (long) tmSerial.hashCode()).toString();
        } catch (Exception ignore) {

        }
        return deviceId;
    }
}
