package com.chinaway.android.library.locationtracker.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间处理工具类.
 */
public class TimeUtils {

    /**
     * 格式化消息详情的时间为年-月-日 小时:分钟:秒数.
     *
     * @param timestamp 时间戳
     * @return 返回格式化后的时间
     */
    public static String formatLocationTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    /**
     * 去除时间字符串中的多余字符.
     *
     * @param time 时间字符串
     * @return 返回去除多余字符的字符串
     */
    public static String time(String time) {
        if (time == null) {
            return null;
        } else {
            if (time.contains(" ")) {
                time = time.replace(" ", "");
            }
            if (time.contains(":")) {
                time = time.replace(":", "").trim();
            }
            if (time.contains("-")) {
                time = time.replace("-", "").trim();
            }

        }
        return time;
    }
}
