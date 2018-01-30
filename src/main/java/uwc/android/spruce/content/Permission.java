package uwc.android.spruce.content;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by steven on 17/7/12.
 * <p>
 * AActivity 和 AFragment 中, 关于 权限 的统一实现.
 * 封装的 EasyPermissions
 */
public class Permission {
    private static final String TAG = Permission.class.getSimpleName();

    public interface Host extends EasyPermissions.PermissionCallbacks {

    }

    private Host mHost = null;

    public static Permission with(Host host) {
        return new Permission(host);
    }

    private Permission(Host host) {
        this.mHost = host;
    }

    /**
     * 权限授予情况的回调, 使用时注意:
     * <p>
     * 若有 onActivityResult() 回调, 则需要指定 {@link Callback#rid}
     */
    public static abstract class Callback {

        /**
         * 所有权限都被授予, 共回调一次
         */
        public void onAfterAllPermissionGranted(int requestCode, List<String> perms) {
        }

        /**
         * 每一个权限被拒绝时, 都会触发一次
         *
         * @param requestCode
         * @param perms
         */
        public void onPermissionsGranted(int requestCode, List<String> perms) {
        }

        /**
         * 每一个权限被拒绝时, 都会触发一次
         *
         * @param requestCode
         * @param perms
         */
        public void onPermissionsDenied(int requestCode, List<String> perms) {

        }

        /**
         * 以下接口内部封装
         */
        final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            List<String> granted = new ArrayList<>();
            List<String> denied = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                String perm = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    granted.add(perm);
                } else {
                    denied.add(perm);
                }
            }
            if (!denied.isEmpty()) onPermissionsDenied(requestCode, denied);
            if (!granted.isEmpty()) onPermissionsGranted(requestCode, granted);
            if (!denied.isEmpty()) return;
            onAfterAllPermissionGranted(requestCode, Arrays.asList(permissions));
        }

        public Callback(int rid) {
            this.rid = rid;
        }

        public Callback() {
            this(-0xffffffff);
        }

        /**
         * onActivityResult() 里边使用, 以区分请求.
         * 其他情况可省略该字段
         */
        int rid = -0xffffffff;
    }

    private static Map<String, Callback> mCallbacks = new HashMap();

    /**
     * 权限授予结果回调
     *
     * @param rid
     * @param permissions
     * @param grantResults
     */
    public static void onResult(Host host, int rid, String[] permissions, int[] grantResults) {
        String key = rid + "@" + host;
        Permission.Callback callback = mCallbacks.get(key);
        if (null == callback)
            return;
        mCallbacks.remove(key);
        callback.onRequestPermissionsResult(rid, permissions, grantResults);
    }

    public void request(String rationale, String[] perms, Callback callback) {
        String key = callback.rid + "@" + mHost;
        if (hasGranted(perms)) {
            // Have permission, do the thing!
            callback.onAfterAllPermissionGranted(callback.rid, Arrays.asList(perms));
            return;
        }
        // Ask for permission
        mCallbacks.put(callback.rid + "@" + mHost, callback);
        EasyPermissions.requestPermissions((Activity) mHost, rationale, callback.rid, perms);
    }

    /**
     * 检查是否有权限
     *
     * @param perms 请求权限列表
     * @return
     */
    public boolean hasGranted(@NonNull String... perms) {
        return EasyPermissions.hasPermissions((Context) mHost, perms);
    }
}
