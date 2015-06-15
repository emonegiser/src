package com.arcgis.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.dao.XCSBDao;
import com.arcgis.entity.XCSBEntity;
import com.arcgis.gpsservice.GPSService;
import com.arcgis.gpsservice.GPSStartThread;
import com.arcgis.gpsservice.GPSStopThread;
import com.arcgis.gpsservice.Gps;
import com.arcgis.httputil.App;
import com.arcgis.httputil.DateUtil;
import com.arcgis.httputil.GPSUtil;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class XunChaSBActivity extends Activity implements View.OnClickListener{

    private  String type=Gps.Type;
    private Switch switchXC;
    private Spinner gapspinner;

    private TextView xcbackBtn;
    ArrayAdapter<String> spinnerXCJGAdapter;
    List<String> XCJG_List=new ArrayList<>();

    //调用webservice
    private KsoapValidateHttp ksoap;
    private Timer timer;
    private TimerTask timerTask;
    Intent intentGPS=null;
    boolean isOpen=false;
    private App MyApp;
    private String yhmStr=null;
    SimpleDateFormat formatter =null;
    private boolean isNetwork=false;
    private XCSBDao xcsbDao=null;
    private XCSBEntity xcsbEntity=null;
    private Handler handler=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_xczf);
        App.getInstance().addActivity(this);
        xcsbDao=new XCSBDao(this);
        gapspinner= (Spinner) findViewById(R.id.gapspinner);

        MyApp=(App) this.getApplication();
        formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        SharedPreferences LOGIN_INFO = getSharedPreferences("LOGIN_INFO",MODE_PRIVATE);
        if(LOGIN_INFO.getString("PID",null)!=null){
            yhmStr=LOGIN_INFO.getString("PID",null);
        }

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(0==msg.arg1){
                    ToastUtil.show(XunChaSBActivity.this,"当前网络异常,位置信息存入本地");
                }
                if(1==msg.arg1){
                    ToastUtil.show(XunChaSBActivity.this,"发送至服务器");
                }
            }
        };

        switchXC= (Switch) findViewById(R.id.switchXC);
        switchXC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isOpen=true;
                    gapspinner.setClickable(true);

                    if(!GPSUtil.isOpen(XunChaSBActivity.this)){
                        GPSUtil.openGPS(XunChaSBActivity.this);
                    }

                    intentGPS = new Intent(XunChaSBActivity.this, GPSService.class);
                    XunChaSBActivity.this.startService(intentGPS);

                    GPSStartThread gpsStartThread=new GPSStartThread(XunChaSBActivity.this);
                    gpsStartThread.start();

                    SharedPreferences XCSB_INFO = XunChaSBActivity.this.getSharedPreferences("XCSB_INFO", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = XCSB_INFO.edit();
                    editor.putString("ISON","true");
                    editor.commit();
                }else{

                    if(GPSUtil.isOpen(XunChaSBActivity.this)){
                        GPSUtil.closeGPS(XunChaSBActivity.this);
                    }

                    GPSStopThread gpsStopThread =new GPSStopThread(XunChaSBActivity.this);
                    gpsStopThread.start();

                    SharedPreferences XCSB_INFO = XunChaSBActivity.this.getSharedPreferences("XCSB_INFO", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = XCSB_INFO.edit();
                    editor.putString("ISON","false");
                    editor.commit();

                    if(timerTask!=null ){
                        ToastUtil.show(XunChaSBActivity.this,"上报取消");
                        timerTask.cancel();
                    }

                    isOpen=false;
                    gapspinner.setClickable(false);

                }
            }
        });

        gapspinner= (Spinner) findViewById(R.id.gapspinner);
        XCJG_List.add("5分钟");
        XCJG_List.add("1分钟");
        XCJG_List.add("3分钟");
        XCJG_List.add("15分钟");
        spinnerXCJGAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,XCJG_List );
        spinnerXCJGAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gapspinner.setAdapter(spinnerXCJGAdapter);

        gapspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences XCSB_INFO = XunChaSBActivity.this.getSharedPreferences("XCSB_INFO", Context.MODE_PRIVATE);
                String isOn=XCSB_INFO.getString("ISON",null);

                if(timerTask!=null){
                    timerTask.cancel();
                }

                //定时器上报坐标
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        isNetwork= NetUtils.isNetworkAvailable(XunChaSBActivity.this);
                        if(isNetwork){
                           // if( MyApp.getmGPSLon()!=null &&  MyApp.getmGPSLat()!=null){
                                GetXYDataSync getXYDataSync = new GetXYDataSync();
                                getXYDataSync.execute();
                            //}
                        }else{
                            //存入本地
                            Message msg=new Message();
                            msg.arg1=0;
                            handler.sendMessage(msg);

                            xcsbEntity=new XCSBEntity();
                            xcsbEntity.setYhm(yhmStr);
                            xcsbEntity.setX(MyApp.getmGPSLon());
                            xcsbEntity.setY(MyApp.getmGPSLat());
                            xcsbEntity.setAccu(MyApp.getmGPSAccu());
                            xcsbEntity.setTime(DateUtil.GetLongDate(new Date()));
                            xcsbDao.add(xcsbEntity);
                        }
                    }
                };

                if (isOn!=null && isOn.equals("true")) {
                    switch (position) {
                        case 0:
                            timer.scheduleAtFixedRate(timerTask,500, 5 * 60 * 1000);
                            break;
                        case 1:
                            timer.scheduleAtFixedRate(timerTask,500, 60 * 1000);
                            break;
                        case 2:
                            timer.scheduleAtFixedRate(timerTask,500, 3 * 60 * 1000);
                            break;
                        case 3:
                            timer.scheduleAtFixedRate(timerTask,500, 15 * 60 * 1000);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        xcbackBtn= (TextView) findViewById(R.id.xcbackBtn);
        xcbackBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences XCSB_INFO = XunChaSBActivity.this.getSharedPreferences("XCSB_INFO", Context.MODE_PRIVATE);
        String isOn=XCSB_INFO.getString("ISON",null);

        if (isOn!=null && isOn.equals("true")) {
            switchXC.setChecked(true);
        }else{
            switchXC.setChecked(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(intentGPS!=null){
            XunChaSBActivity.this.stopService(intentGPS);
        }

        GPSUtil.closeGPS(XunChaSBActivity.this);

    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        switch(viewId){
            case R.id.xcbackBtn:
                XunChaSBActivity.this.finish();
                break;
            default:
                break;
        }
    }


    //获取任务状态
    public class GetXYDataSync extends AsyncTask<String, Integer, String> {
        /**
         * String userid,String x,String y,String remark
         */

        public GetXYDataSync() {
            ksoap=new KsoapValidateHttp(XunChaSBActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.AddTB_YDXC(yhmStr, MyApp.getmGPSLon(), MyApp.getmGPSLat(), "精度:" + MyApp.getmGPSAccu(),type);
                Message msg=new Message();
                msg.arg1=1;
                handler.sendMessage(msg);
                //String AddRslt=ksoap.AddTB_YDXC(yhmStr,105.29583922+"",27.29284622+"",formatter.format(new Date()));
                if(AddRslt!=null){
                    return AddRslt;
                }else{
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}