package org.fbb.balkna.model.utils;

/**
 *
 * @author jvanek
 */
public class TimeUtils {

    public static String secondsToHours(int s) {
        int h = s / 3600;
        int q = s % 3600;
        int m = q / 60;
        int ss = q % 60;
        return align(h) + ":" + align(m) + ":" + align(ss);
    }

    public static String secondsToMinutes(int s) {
        int m = s / 60;
        int ss = s % 60;
        return align(m) + ":" + align(ss);
    }

    private static String align(int i) {
        String s = String.valueOf(i);
        if (s.length() == 0) {
            return "00" + s;
        }
        if (s.length() == 1) {
            return "0" + s;
        }
        return s;
    }

}
