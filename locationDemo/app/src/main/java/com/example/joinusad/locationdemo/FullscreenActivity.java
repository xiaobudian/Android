package com.example.joinusad.locationdemo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private View mContentView;
    private View mControlsView;
    private boolean mVisible;
    private LocationManager locationManager;
    private String locationProvider;
    private TextView postionView;
    private Context context;
    private TextView cityTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_fullscreen);
        cityTextView = (TextView)findViewById(R.id.textView2);
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        postionView = (TextView)findViewById(R.id.textView);
        findViewById(R.id.button).setOnClickListener(onBtnClick);
    }

    private final View.OnClickListener onBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            postionView.setText("");
            //获取地理位置管理器
            locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
            List<String> providers = locationManager.getAllProviders();
            //获取所有可用的位置提供器
            if(providers.contains(LocationManager.GPS_PROVIDER)
                    &&locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                //如果是GPS
                locationProvider = LocationManager.GPS_PROVIDER;
            }else if(providers.contains(LocationManager.NETWORK_PROVIDER)
                    &&locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                //如果是Network
                locationProvider = LocationManager.NETWORK_PROVIDER;
            }else{
                Toast.makeText(FullscreenActivity.this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
                return ;
            }
            //获取Location
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if(location!=null){
                //不为空,显示地理位置经纬度
                showLocation(location);
            }
            //监视地理位置变化
            locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
        }
    };
    /**
     * 显示地理位置经度和纬度信息
     * @param location
     */
    private void showLocation(Location location){
        String locationStr = "维度：" + location.getLatitude() +"\n"
                + "经度：" + location.getLongitude();
        postionView.setText(locationStr);
    }

    /**
     * LocationListern监听器
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */

    LocationListener locationListener =  new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            if (provider.contentEquals(LocationManager.GPS_PROVIDER))
                Toast.makeText(FullscreenActivity.this, "GPS信号已丢失。。。", Toast.LENGTH_SHORT);
            if (provider == LocationManager.NETWORK_PROVIDER)
                Toast.makeText(FullscreenActivity.this, "网络信号已丢失。。。", Toast.LENGTH_SHORT);
        }

        @Override
        public void onLocationChanged(Location location) {
            //如果位置发生变化,重新显示
            showLocation(location);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                // 更具地理环境来确定编码
                Geocoder gc = new Geocoder(context, Locale.getDefault());
                try {
                    // 取得地址相关的一些信息\经度、纬度
                    List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);

                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);

                        String cityName = address.getLocality();
                        cityTextView.setText(cityName);
                    }
                } catch (IOException e) {
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationManager!=null){
            //移除监听器
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case 0:
                    String position = (String) msg.obj;
                    postionView.setText(position);
                    break;
                default:
                    break;
            }
        }
    };
}
