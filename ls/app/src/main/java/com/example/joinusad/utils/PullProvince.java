package com.example.joinusad.utils;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;

/**
 * 解析省、市、区xml
 * @author jph
 * Date：2014.09.25
 */
public class PullProvince {
    public static final int PARSESUCCWSS=0x2001;
    private Handler handler;
    public PullProvince(Handler handler) {
        // TODO Auto-generated constructor stub
        this.handler=handler;
    }
    /**
     * 获取所有省份城市以及区
     * @author jph
     * Date:2014.09.25
     */
    public void getProvinces(final InputStream inStream) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    XmlPullParser pullParser = Xml.newPullParser();
                    pullParser.setInput(inStream, "UTF-8");
                    int event;
                    event = pullParser.getEventType();
                    Map<String, Map<String, List<String>>> provinces = new HashMap<String,
                            Map<String, List<String>>>();// 省份
                    Map<String, List<String>> cities=null;// 城市
                    ArrayList<String> areaAll = null;
                    String pName = "";// 省份名称
                    String cName = "";// 城市名称
                    String aName = "";// 地区名
                    String targetName = "";// 当前节点的名称
                    while (event != XmlPullParser.END_DOCUMENT) {
                        targetName = pullParser.getName();
                        switch (event) {
                            case XmlPullParser.START_TAG:
                                if ("province".equals(targetName)) {// 处理省份节点
                                    pName = pullParser.getAttributeValue(1);// 当前省份名
                                    cities=new HashMap<String, List<String>>();
                                } else if ("city".equals(targetName)) {// 处理城市节点
                                    cName = pullParser.getAttributeValue(1);
                                    areaAll = new ArrayList<String>();// 地区
                                } else if ("town".equals(targetName)) {// 处理地区节点
                                    aName = pullParser.getAttributeValue(1);
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                if (targetName.equals("town")) {
                                    areaAll.add(aName);
                                } else if (targetName.equals("city")) {
                                    cities.put(cName, areaAll);
                                } else if (targetName.equals("province")) {
                                    provinces.put(pName, cities);
                                }
                                break;
                        }
                        event = pullParser.next();
                    }
                    Message message=new Message();
                    message.obj=provinces;//将解析出的数据放到message中传给主线程
                    message.what=PARSESUCCWSS;
                    handler.sendMessage(message);//通知主线程数据解析完毕
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }
}