package com.apkstory.com.vichild.file;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wei on 13-7-24.
 */
public class FileExplorer {
    public static String SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String[] fileName;
    public static String[] filePath;
    public static String[] fileUpdateTime;
    public static String[] isDir;
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static Date date = new Date();

    public static void getFilesPathList(String filepath) {
        try {
            if (!filepath.endsWith("/")) {
                filepath = filepath + "/";
            }
            File file = new File(filepath);
            File[] filesList = compare(filepath);
            Log.i("ViChildInfo", filesList.length + "");
            filePath = new String[filesList.length + 1];
            fileName = new String[filesList.length + 1];
            fileUpdateTime = new String[filesList.length + 1];
            isDir = new String[filesList.length + 1];
            filePath[0] = file.getParent();
            if (filepath.equals("/")) {
                fileName[0] = "Root";
            } else {
                fileName[0] = file.getParentFile().getName();
            }
            isDir[0] = "Dir";
            date.setTime(file.lastModified());
            fileUpdateTime[0] = df.format(date);
            if (filesList.length > 0) {
                for (int filesNum = 0; filesNum < filesList.length; filesNum++) {
                    filePath[filesNum + 1] = filesList[filesNum].getAbsolutePath();
                    fileName[filesNum + 1] = filesList[filesNum].getName();
                    isDir[filesNum + 1] = filesList[filesNum].getAbsoluteFile().isDirectory() ? "Dir" : "File";
                    date.setTime(filesList[filesNum].lastModified());
                    fileUpdateTime[filesNum + 1] = df.format(date);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File[] compare(String path) {
        File[] f1 = new File(path).listFiles();
        List<File> files = Arrays.asList(f1);
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        File[] fs = new File[f1.length];
        for (int i = 0; i < files.size(); i++) {
            fs[i] = files.get(i);
        }
        return fs;
    }
}
