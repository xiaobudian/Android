package com.example.joinusad.ls;

import android.os.Bundle;
import android.os.Environment;
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

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.os.CountDownTimer;

public class MyActivity extends AppCompatActivity {

    private CountDownTimer countDownTime = null;
    private TextView textView = null;
    private TimePicker timePicker = null;
    private TextToSpeech mTextToSpeech = null;
    private Button btnGetWeather = null;
    private String fileName = null;
    private String encoding = "utf-8";
    private File extDir = Environment.getExternalStorageDirectory();
    private String weatherAPIurl = "https://api.thinkpage.cn/v2/weather/all.json?" +
            "city=beijing&key=3TZQULQE1G";
    Timer timer = new Timer( );

    TimerTask timerTask = new TimerTask( ) {
        public void run ( ) {
            Message message = new Message( );
            handler.sendMessage(message);
        }
    };

    final Handler handler = new Handler( ) {

        public void handleMessage(Message msg) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            textView.setText(sdf.format(new Date()));
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
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if (countDownTime != null) {
                    countDownTime.cancel();
                    //textView.setText("done!");
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
                            //textView.setText("seconds remaining: " + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            //textView.setText("done!");

                            btnGetWeather.callOnClick();
                        }
                    }.start();
                }
            }
        });
        textView = (TextView)findViewById(R.id.textView);

        timer.schedule(timerTask,1000, 1000); //延时1000ms后执行，1000ms执行一次
        fileName = "weather.txt";
        btnGetWeather = (Button) findViewById(R.id.btnGetWeather);

        btnGetWeather.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        String weatherInfo = "";
                        Weather weather = new Weather();
                        try {
                            weatherInfo = readFileData();
                            if (weather.expired(weatherInfo))
                                weatherInfo = "";
                            if (weatherInfo.length() == 0) {
                                weatherInfo = getWeatherInfo();
                                writeFileData(weatherInfo);
                            }
                            weather = new Weather();
                            weather.setData(weatherInfo);

                            mTextToSpeech.speak(weather.toString(), TextToSpeech.QUEUE_FLUSH, null);
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                    if ((supported != TextToSpeech.LANG_AVAILABLE) && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
                        //displayToast("不支持当前语言！");
                    }
                }
            }

        });
    }



    private String getWeatherInfo() throws JSONException {
        JSONObject obj = null;
        JSONArray array = null;
        String response = HttpRequest.sendGet(weatherAPIurl);
        obj = new JSONObject(response);
        array = obj.getJSONArray("weather");
        obj = array.getJSONObject(0);
        return obj.toString();
    }

    private String readFileData() throws IOException {

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

    private void writeFileData(String text) {

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
