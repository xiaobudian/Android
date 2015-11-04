package com.example.joinusad.utils;

import android.os.Environment;
import java.io.File;

/**
 * Created by Administrator on 2015/10/28.
 */
public class GlobalSettings {

    public static String weatherFilePath = "weatherInfo.txt";
    public static String weatherCanReadFilePath = "weatherCanRead.txt";
    public static File extDir = Environment.getExternalStorageDirectory();
    public static String weatherAPIurl = "https://api.thinkpage.cn/v2/weather/all.json?" +
            "city=beijing&key=3TZQULQE1G";
    public static String encoding = "utf-8";
    public static Integer requestCount = 4;
    public static final int Message_Toast = 1;

}
