package com.example.joinusad.utils;

import android.os.Environment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Administrator on 2015/10/27.
 */
public class WeatherService {
    private static String fileName = "weather.txt";;
    private static String encoding = "utf-8";
    private static File extDir = Environment.getExternalStorageDirectory();
    private static String weatherAPIurl = "https://api.thinkpage.cn/v2/weather/all.json?" +
            "city=beijing&key=3TZQULQE1G";

    private static String getWeather() throws JSONException {
        JSONObject obj = null;
        JSONArray array = null;
        String response = null;
        Integer count = 4;
        while (response == null && count > 0) {
            response = HttpRequest.sendGet(weatherAPIurl);
            count -= 1;
        }
        obj = new JSONObject(response);
        array = obj.getJSONArray("weather");
        obj = array.getJSONObject(0);
        return obj.toString();
    }

    private static String readFileData() throws IOException {

        String content = "";
        File file = new File(extDir, fileName);
        if (file.isFile() && file.exists()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), encoding));
            String line = null;
            while ((line = reader.readLine()) != null) {
                content += line + "\n";
            }
            reader.close();
        } else {

            file.createNewFile();
        }
        return content;
    }

    private static void writeFileData(String text) {

        if (text == null || text.length() == 0)
            return;
        try {
            File file = new File(extDir.getPath() + "/" + fileName);
            FileOutputStream fout = new FileOutputStream(file, false);
            OutputStreamWriter osw = new OutputStreamWriter(fout, encoding);
            BufferedWriter writer = new BufferedWriter(osw);
            writer.write(text);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Weather getWeatherInfo() throws  Exception{
            String  weatherInfo = readFileData();
            Weather  weather = null;
            if (weather.expired(weatherInfo))
                weatherInfo = "";
            if (weatherInfo.length() == 0) {
                weatherInfo = getWeather();
                if (weatherInfo == null)
                    return null;
                writeFileData(weatherInfo);
            }
            weather = new Weather();
            weather.setData(weatherInfo);

        return weather;
    }
}
