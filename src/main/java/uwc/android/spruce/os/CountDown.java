package uwc.android.spruce.os;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by steven on 2016/9/30.
 */

public class CountDown extends TimedScheduler<CountDown> {

    private int mInitialDelay = 3 * 1000;
    private int mPeriod = 1000;

    private int mCurTime = 3 * 1000;

    /**
     * 倒计时起始时间 (默认3秒) [单位 毫秒]
     * @param initialDelay
     * @return
     */
    public CountDown setInitialDelay(int initialDelay) {
        mInitialDelay = initialDelay;
        return this;
    }

    /**
     * 计时间隔 (默认1s) [单位 毫秒]
     * @param period
     * @return
     */
    public CountDown setPeriod(int period) {
        mPeriod = period;
        return this;
    }

    public CountDown(int initialDelay) {
        mInitialDelay = initialDelay;
    }

    @Override
    public void start() {
        Log.d(TAG, "start");
        clear();
        mCurTime = mInitialDelay;
        mTimer = Executors.newSingleThreadScheduledExecutor();
        mTimer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                final int tmpCurTime = mCurTime;    // 重要 !!! (以下为闭包, 直接用 mCurTime 有问题)
                new Handler(Looper.getMainLooper()).post(new Runnable() {   // mTimer 在子线程, 应当 post 到 MainThread
                    @Override
                    public void run() {
                        if (mOnSchedule != null)
                            mOnSchedule.onTime(tmpCurTime);
                    }
                });


                if((mCurTime -= mPeriod) < 0){
                    clear();
                }
            }
        }, 0, mPeriod, TimeUnit.MILLISECONDS);
    }
}
