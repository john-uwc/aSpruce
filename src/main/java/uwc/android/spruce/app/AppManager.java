package uwc.android.spruce.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

/**
 * Created by Administrator on 2017/5/5.
 */

public class AppManager {
    private static AppManager sInstance = null;

    public static AppManager getInstance() {
        synchronized (AppManager.class) {
            if (sInstance == null) {
                sInstance = new AppManager();
            }
            return sInstance;
        }
    }

    private AppManager() {
    }

    private Stack<Activity> mActivityStack = new Stack<>();

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        mActivityStack.push(activity);
    }

    /**
     * 添加Activity到堆栈
     */
    public void removeActivity(Activity activity) {
        mActivityStack.remove(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        try {
            return mActivityStack.peek();
        } catch (EmptyStackException e) {
            return null;
        }
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishCurrent() {
        finishActivity(currentActivity());
    }

    /**
     * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void exit(Context context) {
        try {
            finishAllActivity();
            ActivityManager manager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            manager.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }
    }
    public void finishActivity(Class act){
        for(Activity activity:mActivityStack){
            if(activity.getClass()==act){
                finishActivity(activity);
            }
        }
    }
    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        try {
            while (true) mActivityStack.pop().finish();
        }catch (Exception e){
        }
    }


    public void open(Context context) {
        try {
            // 这个activity的信息是我们的launcher
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

            List<ActivityManager.RecentTaskInfo> recentTasks = am.getRecentTasks(21, 0x0002);
            for (int i = 0; i < recentTasks.size(); i++) {
                final ActivityManager.RecentTaskInfo info = recentTasks.get(i);
                Intent intent = new Intent(info.baseIntent);
                if (info.origActivity != null) {
                    intent.setComponent(info.origActivity);
                }
                // 设置intent的启动方式为 创建新task()【并不一定会创建】
                intent.setFlags((intent.getFlags() & ~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                /**
                 * 如果找到是launcher，直接continue，后面的appInfos.add操作就不会发生了
                 */
                if (context.getPackageName().equals(intent.getComponent().getPackageName())) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
                    context.startActivity(intent);
                    break;
                }
            }
            recentTasks.clear();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
