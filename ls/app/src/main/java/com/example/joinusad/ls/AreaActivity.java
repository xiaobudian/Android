package com.example.joinusad.ls;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.joinusad.utils.GlobalSettings;
import com.example.joinusad.utils.PullProvince;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AreaActivity extends AppCompatActivity {

    private Spinner province,city,area;
    private Map<String, Map<String, List<String>>> data=null;
    /**当前的选择的省份**/
    private	String currentProvince;
    /**当前的选择的城市**/
    private	String currentCity;
    private PullProvince pullProvince;
    //	/**当前的选择的区**/
//	private	String currentArea;
    private Handler mHandler=new Handler(){
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case PullProvince.PARSESUCCWSS://数据解析完毕，加载数据
                    data=(Map<String, Map<String, List<String>>>) msg.obj;
                    initData();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private List<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        province=(Spinner)findViewById(R.id.spinner_p);
        city=(Spinner)findViewById(R.id.spinner_c);
        area=(Spinner)findViewById(R.id.spinner_t);
        pullProvince=new PullProvince(mHandler);
        File file = new File(GlobalSettings.getInstance().extDir,"area.xml");
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        pullProvince.getProvinces(inStream);
    }
    /**
     * 初始化数据
     */
    private void initData(){
        if (data!=null) {
            String[]arrStrings= data.keySet().toArray(new String[0]);
            System.out.println(arrStrings);
            //将省份信息填充到 province Spinner
            province.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_multiple_choice,arrStrings));
            currentProvince=getCurrentProvince();
            bindCityAdapter(currentProvince);
            currentCity=getCurrentCity();
            bindAreaAdapter(currentCity);
            setOnItemSelectedListener();
        }
    }
    private void bindAreaAdapter(String currentCity) {
        // TODO Auto-generated method stub
        List<String> towns = data.get(currentProvince).get(currentCity);
        //根据当前显示的城市将对应的区填充到area Spinner
        area.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, towns));
    }
    private void bindCityAdapter(String currentProvince) {
        // TODO Auto-generated method stub
        //根据当前显示的省份将对应的城市填充到city Spinner
        city.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice,data.
                get(currentProvince).keySet().toArray(new String[0])));
    }
    /**
     * 为Spinner设置监听器
     */
    private void setOnItemSelectedListener() {
        // TODO Auto-generated method stub
        province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                currentProvince=getCurrentProvince();
                bindCityAdapter(currentProvince);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                currentCity=getCurrentCity();
                bindAreaAdapter(currentCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                Toast.makeText(AreaActivity.this, area.getSelectedItem().toString(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**获取当前选择的省份
     * @return String:当前选择的省份
     */
    private String getCurrentProvince() {
        // TODO Auto-generated method stub
        return province.getSelectedItem().toString();
    }
    /**获取当前选择的城市
     * @return String:当前选择的城市
     */
    private String getCurrentCity() {
        // TODO Auto-generated method stub
        return city.getSelectedItem().toString();
    }
}
