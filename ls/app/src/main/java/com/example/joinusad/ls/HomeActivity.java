package com.example.joinusad.ls;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.os.CountDownTimer;
import android.widget.Toast;
import com.example.joinusad.utils.NetHelper;
import com.example.joinusad.utils.Weather;
import com.example.joinusad.utils.WeatherService;

public class HomeActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private String locationProvider;
    private Button btnLocation;
    private CountDownTimer countDownTime = null;
    private TextView textView = null;
    private TimePicker timePicker = null;
    private TextToSpeech mTextToSpeech = null;
    private Button btnGetWeather = null;
    private Context context = null;
    private Timer timer = null;
    private TextView cityTextView = null;

    private TimerTask timerTask = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 0;
            handler.sendMessage(message);
        }
    };

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    textView.setText(sdf.format(new Date()));
                    break;
                case 1:
                    Toast.makeText(HomeActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this.getApplicationContext();

        timePicker = (TimePicker)findViewById(R.id.timePicker2);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(
                new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        if (countDownTime != null) {
                            countDownTime.cancel();
                        }
                        Date now = new Date();
                        Date time_point = new Date();
                        time_point.setHours(hourOfDay);
                        time_point.setMinutes(minute);
                        time_point.setSeconds(0);
                        long time = time_point.getTime() - now.getTime();
                        if (time > 0) {
                            countDownTime = new CountDownTimer(time, 1000) {
                                public void onTick(long millisUntilFinished) {
                                }

                                public void onFinish() {
                                    btnGetWeather.callOnClick();
                                }
                            }.start();
                        }
                    }
                });
        textView = (TextView) findViewById(R.id.textView);
        timer = new Timer();
        timer.schedule(timerTask, 1000, 1000); //延时1000ms后执行，1000ms执行一次
        cityTextView = (TextView)findViewById(R.id.cityTextView);
        btnLocation = (Button) findViewById(R.id.btnLocation);
        getLocation();
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
        btnGetWeather = (Button) findViewById(R.id.btnGetWeather);

        btnGetWeather.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {

                        try {
                            String localWeatherInfo = WeatherService.readFileData();
                            Weather  weather = new Weather();
                            if (weather.expired(localWeatherInfo)) {
                                if (!NetHelper.isNetworkConnected(context)) {
                                    sendMessage(1, "当前网络不可用！");
                                    return;
                                }
                                sendMessage(1, "正在获取数据 。。。");
                                String weatherInfo = WeatherService.getWeather();
                                if (weatherInfo == null){
                                    sendMessage(1, "获取数据失败 。。。");
                                    return;
                                }
                                WeatherService.writeFileData(weatherInfo);
                                weather.setData(weatherInfo);
                            }else{
                                weather.setData(localWeatherInfo);
                            }
                            mTextToSpeech.speak(weather.toString(), TextToSpeech.QUEUE_FLUSH, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        //实例并初始化TTS对象
        mTextToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if (status == TextToSpeech.SUCCESS) {
                    //设置朗读语言
                    int supported = mTextToSpeech.setLanguage(Locale.US);
                    if ((supported != TextToSpeech.LANG_AVAILABLE)
                            && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
                        //displayToast("不支持当前语言！");
                    }
                }
            }

        });
    }

    private void getLocation() {

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
            Toast.makeText(HomeActivity.this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return ;
        }
        //获取Location
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if(location!=null){
            //不为空,显示地理位置经纬度
           // showLocation(location);
            showCityName(location);
        }
        //监视地理位置变化
        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
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
                Toast.makeText(HomeActivity.this, "GPS信号已丢失。。。", Toast.LENGTH_SHORT);
            if (provider == LocationManager.NETWORK_PROVIDER)
                Toast.makeText(HomeActivity.this, "网络信号已丢失。。。", Toast.LENGTH_SHORT);
        }

        @Override
        public void onLocationChanged(Location location) {
            //如果位置发生变化,重新显示
            //showLocation(location);
           showCityName(location);
        }
    };

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

                    String cityName = address.getLocality();
                    cityTextView.setText(cityName);
                }else{
                    sendMessage(1, "定位失败。。。");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationManager!=null){
            //移除监听器
            locationManager.removeUpdates(locationListener);
        }
    }

    private void sendMessage(int what, Object obj){
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        handler.sendMessage(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
