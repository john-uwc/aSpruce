package uwc.android.spruce.util;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import uwc.util.DateTime;
import uwc.android.spruce.R;

/***
 * 此类描述的是:日期时间工具类
 */
public class DateHelper extends DateTime {

    /**
     * 时间转化为显示字符串
     *
     * @param timeStamp 单位为秒
     */
    public static String formatTime(long timeStamp, boolean simple, Context context) {
        if (timeStamp == 0) return "";

        Calendar input = Calendar.getInstance();
        input.setTimeInMillis(timeStamp);
        String time = (new SimpleDateFormat("HH:mm:ss")).format(input.getTime());
        String date = (new SimpleDateFormat(
                "yyyy" + context.getResources().getString(R.string.time_year)
                        + "MM" + context.getResources().getString(R.string.time_month)
                        + "dd" + context.getResources().getString(R.string.time_day))).format(input.getTime());
        Calendar calendar = Calendar.getInstance();
        //当前时间在输入时间之前
        if (!calendar.after(input)) {
            return date;
        }

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(input)) {
            return String.format("%s %s", context.getResources().getString(R.string.time_today), simple ? "" : time);
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(input)) {
            return String.format("%s %s", context.getResources().getString(R.string.time_yesterday), simple ? "" : time);
        }

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        if (!simple && calendar.before(input)) {
            date = (new SimpleDateFormat(
                    "MM" + context.getResources().getString(R.string.time_month)
                            + "dd" + context.getResources().getString(R.string.time_day))).format(input.getTime());
        }
        return String.format("%s %s", date, simple ? "" : time);
    }

    /**
     * 时间转化为显示字符串, 小数点分割, 如 2017.11.20 18:00
     *
     * @param timeStamp 单位为秒
     */
    public static String formatTimeByDot(long timeStamp) {
        if (timeStamp == 0) return "";

        Calendar input = Calendar.getInstance();
        input.setTimeInMillis(timeStamp);
        String time = new SimpleDateFormat("HH:mm").format(input.getTime());
        String date = new SimpleDateFormat("yyyy.MM.dd").format(input.getTime());
        return String.format("%s %s", date, time);
    }

    /**
     * 时间转化为显示字符串
     *
     * @param timeStamp 单位为秒
     */
    public static String formatTimeNoMillisecond(long timeStamp, Context context) {
        if (timeStamp == 0) return "";

        Calendar input = Calendar.getInstance();
        input.setTimeInMillis(timeStamp);
        String date = (new SimpleDateFormat(
                "yyyy" + context.getResources().getString(R.string.time_year)
                        + "MM" + context.getResources().getString(R.string.time_month)
                        + "dd" + context.getResources().getString(R.string.time_day))).format(input.getTime());
        return date;
    }

    /**
     * 判断当前日期是星期几-1转换为一
     */
    public static String intForString(String pTime) {
        if (pTime == null) {
            return "";
        }
        int i = dayForWeek(pTime);
        String week = "";
        switch (i) {
            case 1:
                week = "一";
                break;
            case 2:
                week = "二";
                break;
            case 3:
                week = "三";
                break;
            case 4:
                week = "四";
                break;
            case 5:
                week = "五";
                break;
            case 6:
                week = "六";
                break;
            case 7:
                week = "日";
                break;
            default:
                break;
        }
        return week;
    }
}
