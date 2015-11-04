package com.example.joinusad.ls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.joinusad.utils.GlobalSettings;
import com.example.joinusad.utils.LocationHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private Context context = null;
    private String locationProvider;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        context = this.getApplicationContext();
        getLocation();
    }

    private void getLocation() {
        try {
            //获取地理位置管理器
            LocationManager locationManager =
                    (LocationManager)context.getSystemService(LOCATION_SERVICE);
            locationProvider = LocationHelper.getLocationProvider(locationManager);
            if (locationProvider == null) {
                sendMessage(1, "没有可用的位置提供器");
                return;
            }
            //获取Location
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location != null) {
                showCityName(location);
            }
            //监视地理位置变化
            locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void showCityName(Location location){
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            try {
                // 更具地理环境来确定编码
                Geocoder gc = new Geocoder(context, Locale.getDefault());
                // 取得地址相关的一些信息\经度、纬度
                List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);

                if (addresses.size() > 0) {
                    Address address = addresses.get(0);

                    String town = address.getSubLocality();
                    sendMessage(1, town);
//                    Toast.makeText(FullscreenActivity.this, cityName, Toast.LENGTH_SHORT);
                    //cityTextView.setText(cityName);
                }else{
                    sendMessage(1, "定位失败。。。");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    LocationListener locationListener =  new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {
            sendMessage(GlobalSettings.Message_Toast, provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (provider.contentEquals(LocationManager.GPS_PROVIDER))
                sendMessage(GlobalSettings.Message_Toast, "GPS信号已恢复。。。");
                //Toast.makeText(FullscreenActivity.this, "GPS信号已丢失。。。", Toast.LENGTH_SHORT);
            if (provider == LocationManager.NETWORK_PROVIDER)
                sendMessage(GlobalSettings.Message_Toast, "网络信号已恢复。。。");
                //Toast.makeText(FullscreenActivity.this, "网络信号已恢复。。。", Toast.LENGTH_SHORT);
        }

        @Override
        public void onProviderDisabled(String provider) {
            if (provider.contentEquals(LocationManager.GPS_PROVIDER))
                sendMessage(GlobalSettings.Message_Toast, "GPS信号已丢失。。。");
                //Toast.makeText(FullscreenActivity.this, "GPS信号已丢失。。。", Toast.LENGTH_SHORT);
            if (provider == LocationManager.NETWORK_PROVIDER)
                sendMessage(GlobalSettings.Message_Toast, "网络信号已丢失。。。");
                //Toast.makeText(FullscreenActivity.this, "网络信号已丢失。。。", Toast.LENGTH_SHORT);
        }

        @Override
        public void onLocationChanged(Location location) {
            //如果位置发生变化,重新显示
            //showLocation(location);
            showCityName(location);
        }
    };

    private void sendMessage(int what, Object obj){
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        handler.sendMessage(message);
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
//                case 0:
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    //textView.setText(sdf.format(new Date()));
//                    break;
                case GlobalSettings.Message_Toast:
                    Toast.makeText(FullscreenActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
