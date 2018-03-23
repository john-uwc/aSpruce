package uwc.android.spruce.content;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import uwc.p.Redis;
import uwc.p.Source;

/**
 * Created by steven on 23/01/2018.
 */

public class SPSource implements Source {

    protected final String TAG = getClass().getSimpleName();

    /**
     * SP文件名
     */
    public static final String SP_NAME = "_sp";

    private SharedPreferences sp = null;

    private SharedPreferences core() {
        if (null == sp && null != Redis.query(Context.class))
            sp = Redis.query(Context.class).getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp;
    }

    /**
     * 从share preferences删除
     */
    public void erase(String key) {
        if (key == null) {
            return;
        }
        Log.d(TAG, "erase from sp tag with" + key);
        SharedPreferences.Editor editor = core().edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 保存字符串
     *
     * @param key
     * @param value
     */
    @Override
    public void putString(String key, String value) {
        SharedPreferences.Editor editor = core().edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 获取字符串
     *
     * @param key
     * @return
     */
    @Override
    public String getString(String key) {
        return core().getString(key, "");
    }

    /**
     * 保存整数值
     *
     * @param key
     * @param value
     */
    @Override
    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = core().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 获取整数值
     *
     * @param key
     * @return
     */
    @Override
    public int getInt(String key) {
        return core().getInt(key, 0);
    }

    /**
     * 保存长整形
     *
     * @param key
     * @param value
     */
    @Override
    public void putLong(String key, long value) {
        SharedPreferences.Editor editor = core().edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 获取长整形
     *
     * @param key
     * @return
     */
    @Override
    public long getLong(String key, long def) {
        return core().getLong(key, def);
    }

    /**
     * 保存boolean值
     *
     * @param key
     * @param value
     */
    @Override
    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = core().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 获取boolean值
     *
     * @param key
     * @return
     */
    @Override
    public boolean getBoolean(String key, boolean def) {
        return core().getBoolean(key, def);
    }
}
