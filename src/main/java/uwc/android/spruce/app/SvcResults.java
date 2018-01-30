package uwc.android.spruce.app;

import android.util.LruCache;

import java.util.HashMap;


public class SvcResults extends HashMap<Object, Integer> {
    private static SvcResults sInstance = null;

    public static SvcResults instance() {
        synchronized (SvcResults.class) {
            if (null == sInstance)
                sInstance = new SvcResults();
            return sInstance;
        }
    }

    private SvcResults() {
    }

    private static LruCache<Object, String> sResults = new LruCache<>(6);

    public static Object rKey(String svc, int err) {
        return svc + "@" + err;
    }

    public String translate(Integer rSvcRlt, String relyMsg) {
        try {
            return AApplication.getContext().getResources().getString(rSvcRlt);
        } catch (Exception e) {
            return relyMsg;
        }
    }

    public String translate(String svc, int err, String msg) {
        synchronized (sResults) {
            if (null == sResults.get(rKey(svc, err)))
                sResults.put(rKey(svc, err), translate(get(rKey(svc, err)), msg));
            return sResults.get(rKey(svc, err));
        }
    }

    public Integer get(Object key) {
        if (containsKey(key)) return super.get(key);
        return -1;
    }

    public Integer put(Object key, Integer value) {
        if (null == value) value = -1;
        return super.put(key, value);
    }

    public Integer remove(Object key) {
        synchronized (sResults) {
            sResults.remove(key);
        }
        return super.remove(key);
    }

    public void clear() {
        synchronized (sResults) {
            sResults.evictAll();
        }
        super.clear();
    }
}
