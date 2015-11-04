package com.example.joinusad.utils;

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

    public static String getWeather() throws JSONException {
        JSONObject obj = null;
        JSONArray array = null;
        String response = null;
        Integer count = GlobalSettings.requestCount;
        while (response == null && count > 0) {
            response = HttpRequest.sendGet(GlobalSettings.weatherAPIurl);
            count -= 1;
        }
        obj = new JSONObject(response);
        array = obj.getJSONArray("weather");
        obj = array.getJSONObject(0);
        return obj.toString();
    }

    public static String readFileData() throws IOException {

        String content = "";
        File file = new File(GlobalSettings.extDir,
                GlobalSettings.weatherFilePath);
        if (file.isFile() && file.exists()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), GlobalSettings.encoding));
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

    public static void writeFileData(String text) {

        if (text == null || text.length() == 0)
            return;
        try {
            File file = new File(GlobalSettings.extDir.getPath() + "/" +
                        GlobalSettings.weatherFilePath);
            FileOutputStream fout = new FileOutputStream(file, false);
            OutputStreamWriter osw = new OutputStreamWriter(fout,
                    GlobalSettings.encoding);
            BufferedWriter writer = new BufferedWriter(osw);
            writer.write(text);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
