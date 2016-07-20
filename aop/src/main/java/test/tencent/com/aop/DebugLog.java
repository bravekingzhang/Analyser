package test.tencent.com.aop;

import android.util.Log;

/**
 * Wrapper around {@link android.util.Log}
 * Created by hoollyzhang on 16/7/20.
 * Description :
 */
public class DebugLog {

    private DebugLog() {}

    /**
     * Send a debug log message
     *
     * @param tag Source of a log message.
     * @param message The message you would like logged.
     */
    public static void log(String tag, String message) {
        Log.e(tag, message);
    }
}