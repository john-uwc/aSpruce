package uwc.android.spruce.os;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import uwc.util.DateTime;

/**
 * Created by steven on 2016/9/30.
 */

public class TimedScheduler<T extends TimedScheduler> {
    protected final String TAG = getClass().getSimpleName();

    public interface OnSchedule {
        void onTime(long curTime);
    }

    protected ScheduledExecutorService mTimer; // 定时器
    protected OnSchedule mOnSchedule;

    protected void clear() {
        if (null == mTimer || mTimer.isShutdown())
            return;
        mTimer.shutdown();mTimer = null;
    }

    public T setOnSchedule(OnSchedule onSchedule) {
        mOnSchedule = onSchedule;
        return (T) this;
    }

    public void start() {
        Log.d(TAG, "start");
        clear();
        mTimer = Executors.newSingleThreadScheduledExecutor();
        mTimer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {   // mTimer 在子线程, 应当 post 到 MainThread
                    @Override
                    public void run() {
                        if (mOnSchedule != null)
                            mOnSchedule.onTime(DateTime.now());
                    }
                });
            }
        }, 1000 * 3, 1000 * 3, TimeUnit.MILLISECONDS);
    }

    public void stop(){
        Log.d(TAG, "stop");
        clear();
    }
}
