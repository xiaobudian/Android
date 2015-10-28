package com.example.joinusad.ls;

import android.content.Context;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.os.CountDownTimer;
import android.widget.Toast;
import com.example.joinusad.utils.NetHelper;
import com.example.joinusad.utils.Weather;
import com.example.joinusad.utils.WeatherService;

public class HomeActivity extends AppCompatActivity {

    private CountDownTimer countDownTime = null;
    private TextView textView = null;
    private TimePicker timePicker = null;
    private TextToSpeech mTextToSpeech = null;
    private Button btnGetWeather = null;
    private Context context = null;
    private Timer timer = null;

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

        btnGetWeather = (Button) findViewById(R.id.btnGetWeather);
        context = this.getApplicationContext();
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
                                    SendMessage(1, "当前网络不可用！");
                                    return;
                                }
                                SendMessage(1, "正在获取数据 。。。");
                                String weatherInfo = WeatherService.getWeather();
                                if (weatherInfo == null){
                                    SendMessage(1, "获取数据失败 。。。");
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

    private void SendMessage(int what, Object obj){
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
