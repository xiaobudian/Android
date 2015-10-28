package com.example.joinusad.utils;

import android.os.Environment;
import java.io.File;

/**
 * Created by Administrator on 2015/10/28.
 */
public class GlobalSettings {

    public String weatherFilePath = "weatherInfo.txt";
    public String weatherCanReadFilePath = "weatherCanRead.txt";
    public File extDir = Environment.getExternalStorageDirectory();
    public String weatherAPIurl = "https://api.thinkpage.cn/v2/weather/all.json?" +
            "city=beijing&key=3TZQULQE1G";
    public String encoding = "utf-8";
    public Integer requestCount = 4;
    private static GlobalSettings instance;

    public static GlobalSettings getInstance() {
        if (instance == null) {
            synchronized (GlobalSettings.class) {
                if (instance == null) {
                    instance = new GlobalSettings();
                }
            }
        }
        return instance;
    }
}
