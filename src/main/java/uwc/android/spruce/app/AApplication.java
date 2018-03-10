package uwc.android.spruce.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;

import uwc.core.util.Logger;

/**
 * Created by Arthur on 17/4/24.
 */
public class AApplication extends Application {

    private static Application sApplication = null;

    static {
        Application me = null;
        try {
            me = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null);
            if (me == null)
                throw new IllegalStateException("Static initialization of Applications must be on main thread.");
        } catch (final Exception e) {
            Logger.Holder.obtain().log(Logger.class.getSimpleName(), "Failed to get current application from AppGlobals." + e.getMessage());
            try {
                me = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null);
            } catch (final Exception ex) {
                Logger.Holder.obtain().log(Logger.class.getSimpleName(), "Failed to get current application from ActivityThread." + e.getMessage());
            }
        } finally {
            sApplication = me;
        }
    }

    public static String getVersionName(Context context) {
        String versionName = "";

        try {
            PackageManager e = context.getPackageManager();
            PackageInfo pi = e.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Logger.Holder.obtain().log("VersionInfo", e.getMessage());
        }

        return versionName;
    }

    public static int getVersionCode(Context context) {
        int versionCode = 0;

        try {
            PackageManager e = context.getPackageManager();
            PackageInfo pi = e.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (Exception e) {
            Logger.Holder.obtain().log("VersionInfo", e.getMessage());
        }

        return versionCode;
    }

    public static Application getInstance() {
        return sApplication;
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(sApplication = this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
