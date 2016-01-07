package com.chinaway.android.library.locationtracker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Set;

public class PrefUtils {

    private static final String TAG = "PrefUtils";

    public static void setStringPreferences(Context context, String name,
            String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                name, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringPreferences(Context context, String name,
            String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                name, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void setStringSetPreferences(Context context, String name,
            String key, Set<String> value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                name, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public static Set<String> getStringSetPreferences(Context context, String name,
            String key, Set<String> defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                name, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(key, defaultValue);
    }

    public static void setBooleanPreferences(Context context, String name,
            String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                name, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBooleanPreferences(Context context, String name,
            String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                name, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static void setLongPreferences(Context context, String name,
            String key, long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                name, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getLongPreferences(Context context, String name,
            String key, long defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                name, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, defaultValue);
    }

    public static void setIntPreferences(Context context, String name,
            String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                name, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getIntPreferences(Context context, String name,
            String key, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                name, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    /**
     * 清空应用数据
     *
     * @param context
     * @param name SharedPreferences文件名
     */
    public static void clearData(Context context, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public static void remove(Context context, String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.remove(key)
                .commit();
    }


    public static void saveFile(String str, String fileName) {
        String filePath = null;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + fileName +".txt";
        } else
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + fileName +".txt";

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file, true);
            outStream.write(str.getBytes());
            String enter = "\r\n";
            outStream.write(enter.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
