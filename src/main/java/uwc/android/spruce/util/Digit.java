package uwc.android.spruce.util;

/**
 * Created by steven on 23/11/2017.
 */

public class Digit {

    public static String posFormat(long number) {
        if (0 > number) return "nan";
        float scaled = number / 10000f;
        if (1 >= scaled) return "" + number;
        if (scaled <= 10) return String.format("%.1f万", scaled);
        return "10万+";
    }
}
