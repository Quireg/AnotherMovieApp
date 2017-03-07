package ua.in.quireg.anothermovieapp.common;

import android.util.Log;

public class MLog {

    public static void d(String tag, String msg) {
        if (Constants.LOGGING) {
            Log.d(tag, StringUtils.emptyIfNull(msg));
        }
    }

    public static void i(String tag, String msg) {
        if (Constants.LOGGING) {
            Log.i(tag, StringUtils.emptyIfNull(msg));
        }
    }

    public static void e(String tag, String msg) {
        if (Constants.LOGGING) {
            Log.e(tag, StringUtils.emptyIfNull(msg));
        }
    }

    public static void e(String tag, Throwable e) {
        if (Constants.LOGGING) {
            Log.e(tag, e.toString());
        }
    }

    public static void w(String tag, String msg) {
        if (Constants.LOGGING) {
            Log.w(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (Constants.LOGGING) {
            Log.v(tag, msg);
        }
    }

}
