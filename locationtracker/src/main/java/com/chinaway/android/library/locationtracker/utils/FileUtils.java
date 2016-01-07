package com.chinaway.android.library.locationtracker.utils;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by Huoyunren on 2016/1/7.
 */
public class FileUtils {

    public static void dumpToFile(String str, String fileName) {
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
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(str + "\r\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
