package com.example.joinusad.ls;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

public class HttpRequest {
    public static String sendGet(String uri) {
        try {
            URL url = new URL(uri);
            InputStream is = url.openStream();
            int len = -1;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            String response = bos.toString("utf-8");
            bos.close();
            is.close();
            return response;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}