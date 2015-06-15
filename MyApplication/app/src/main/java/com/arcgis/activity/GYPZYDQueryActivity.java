package com.arcgis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.entity.GYPZYDEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GYPZYDQueryActivity extends Activity implements View.OnClickListener{

    //供应年份
    Spinner spinnergynf=null;
    //供应地址
    Spinner spinnergydz= null;
    //项目名称
    EditText EditTextxmmc=null;

    private Button queryBtn=null;

    private TextView backtextview;
    private TextView titletextview;

    //供应年份
    ArrayAdapter<String> spinnergynfAdapter;
    //供应地址
    ArrayAdapter<String> spinnergydzAdapter;


    private GYPZYDEntity mGYPZYDEntity;
    private List<GYPZYDEntity> GYPZYD_list=new ArrayList<GYPZYDEntity>();

    //全局变量存储位置
    private App MyApp;

    //调用webservice
    private KsoapValidateHttp ksoap;


    ///批准年份
    String bpnf=null;
    //地址
    String ssc=null;
    //土地征收名称
    String tdzsmc=null;

    private boolean isNetwork=false;
    List<String> bpnf_List=new ArrayList<String>();
    SimpleDateFormat format=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_querygypzyd);
        isNetwork= NetUtils.isNetworkAvailable(this);
        App.getInstance().addActivity(this);
        MyApp=(App) this.getApplication();

        queryBtn= (Button) findViewById(R.id.queryBtn);
        queryBtn.setOnClickListener(this);

        backtextview=(TextView)findViewById(R.id.backtextview);
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.titletextview);
        titletextview.setText("查询供应用地");


        spinnergynf=(Spinner)findViewById(R.id.spinnergynf);
        spinnergydz=(Spinner)findViewById(R.id.spinnergydz);
        EditTextxmmc= (EditText) findViewById(R.id.EditTextxmmc);

        if(MyApp.getCun_list()!=null && MyApp.getCun_list().size()>0){
            spinnergydzAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, MyApp.getCun_list());
        }
        spinnergydzAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnergydz.setAdapter(spinnergydzAdapter);


        format = new SimpleDateFormat("yyyy");
        String dataStr = format.format(new Date());
        int dataInt=Integer.parseInt(dataStr);
        bpnf_List.clear();
        for(int i=0;i<15;i++){
            bpnf_List.add((dataInt-i)+"");
        }
        spinnergynfAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, bpnf_List);
        spinnergynfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnergynf.setAdapter(spinnergynfAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        switch(viewId){
            case R.id.queryBtn:
                if(!isNetwork){
                    ToastUtil.show(this,"请检查网络连接");
                    return;
                }
                if(GYPZYD_list!=null && GYPZYD_list.size()>0){
                    GYPZYD_list.clear();
                }

                String bpnf=spinnergynf.getSelectedItem().toString();
                if(bpnf.equals("全部")){
                    bpnf="";
                }

                String ssc=spinnergydz.getSelectedItem().toString();
                if(ssc.equals("全部")){
                    ssc="";
                }
                tdzsmc=EditTextxmmc.getText().toString();




                GetDZDataSync getDZDataSync=new GetDZDataSync(bpnf,tdzsmc,ssc);
                try {
                    String dataRslt=getDZDataSync.execute().get(5000, TimeUnit.MILLISECONDS);
                    if(dataRslt!=null && dataRslt.contains("")){
                        JSONArray jsonArray = new JSONArray(dataRslt);
                        for(int k=0;k<jsonArray.length();k++){
                            JSONObject o = (JSONObject) jsonArray.get(k);
                            mGYPZYDEntity=new GYPZYDEntity();
                            if(o.has("PMJ") && o.get("PMJ")!=null){
                                mGYPZYDEntity.setBMJ(o.get("PMJ").toString());
                            }
                            if(o.has("BMJ") && o.get("BMJ")!=null){
                                mGYPZYDEntity.setBMJ(o.get("BMJ").toString());
                            }
                            if(o.has("BID") && o.get("BID")!=null){
                                mGYPZYDEntity.setBID(o.get("BID").toString());
                            }
                            if(o.has("PID") && o.get("PID")!=null){
                                mGYPZYDEntity.setPID(o.get("PID").toString());
                            }
                            if(o.has("JGTDHYQK") && o.get("JGTDHYQK")!=null){
                                mGYPZYDEntity.setJGTDHYQK(o.get("JGTDHYQK").toString());
                            }
                            if(o.has("JGTDHYSJ") && o.get("JGTDHYSJ")!=null){
                                if(o.get("JGTDHYSJ").toString().contains("Date")){
                                    int len=o.get("JGTDHYSJ").toString().length();
                                    String d=o.get("JGTDHYSJ").toString().substring(6,len-2);
                                    Date dat=new Date(Long.parseLong(d));
                                    GregorianCalendar gc = new GregorianCalendar();
                                    gc.setTime(dat);
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    String sb=format.format(gc.getTime());
                                    mGYPZYDEntity.setJGTDHYSJ(sb);
                                }else{
                                    mGYPZYDEntity.setJGTDHYSJ("无数据");
                                }
                            }
                            if(o.has("SJJGSJ") && o.get("SJJGSJ")!=null){
                                if(o.get("SJJGSJ").toString().contains("Date")){
                                    int len=o.get("SJJGSJ").toString().length();
                                    String d=o.get("SJJGSJ").toString().substring(6,len-2);
                                    Date dat=new Date(Long.parseLong(d));
                                    GregorianCalendar gc = new GregorianCalendar();
                                    gc.setTime(dat);
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    String sb=format.format(gc.getTime());
                                    mGYPZYDEntity.setSJJGSJ(sb);
                                }else{
                                    mGYPZYDEntity.setSJJGSJ("无数据");
                                }
                            }
                            if(o.has("SJKGSJ") && o.get("SJKGSJ")!=null){
                                if(o.get("SJKGSJ").toString().contains("Date")){
                                    int len=o.get("SJKGSJ").toString().length();
                                    String d=o.get("SJKGSJ").toString().substring(6,len-2);
                                    Date dat=new Date(Long.parseLong(d));
                                    GregorianCalendar gc = new GregorianCalendar();
                                    gc.setTime(dat);
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    String sb=format.format(gc.getTime());
                                    mGYPZYDEntity.setSJKGSJ(sb);
                                }else{
                                    mGYPZYDEntity.setSJKGSJ("无数据");
                                }
                            }
                            if(o.has("SQKGSJ") && o.get("SQKGSJ")!=null){
                                if(o.get("SQKGSJ").toString().contains("Date")){
                                    int len=o.get("SQKGSJ").toString().length();
                                    String d=o.get("SQKGSJ").toString().substring(6,len-2);
                                    Date dat=new Date(Long.parseLong(d));
                                    GregorianCalendar gc = new GregorianCalendar();
                                    gc.setTime(dat);
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    String sb=format.format(gc.getTime());
                                    mGYPZYDEntity.setSQKGSJ(sb);
                                }else{
                                    mGYPZYDEntity.setSQKGSJ("无数据");
                                }
                            }
                            if(o.has("SJJDSJ") && o.get("SJJDSJ")!=null){
                                if(o.get("SJJDSJ").toString().contains("Date")){
                                    int len=o.get("SJJDSJ").toString().length();
                                    String d=o.get("SJJDSJ").toString().substring(6,len-2);
                                    Date dat=new Date(Long.parseLong(d));
                                    GregorianCalendar gc = new GregorianCalendar();
                                    gc.setTime(dat);
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    String sb=format.format(gc.getTime());
                                    mGYPZYDEntity.setSJJDSJ(sb);
                                }else{
                                    mGYPZYDEntity.setSJJDSJ("无数据");
                                }
                            }
                            if(o.has("HTYDJGSJ") && o.get("HTYDJGSJ")!=null){
                                if(o.get("HTYDJGSJ").toString().contains("Date")){
                                    int len=o.get("HTYDJGSJ").toString().length();
                                    String d=o.get("HTYDJGSJ").toString().substring(6,len-2);
                                    Date dat=new Date(Long.parseLong(d));
                                    GregorianCalendar gc = new GregorianCalendar();
                                    gc.setTime(dat);
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    String sb=format.format(gc.getTime());
                                    mGYPZYDEntity.setHTYDJGSJ(sb);
                                }else{
                                    mGYPZYDEntity.setHTYDJGSJ("无数据");
                                }
                            }
                            if(o.has("HTYDDGSJ") && o.get("HTYDDGSJ")!=null){
                                if(o.get("HTYDDGSJ").toString().contains("Date")){
                                    int len=o.get("HTYDDGSJ").toString().length();
                                    String d=o.get("HTYDDGSJ").toString().substring(6,len-2);
                                    Date dat=new Date(Long.parseLong(d));
                                    GregorianCalendar gc = new GregorianCalendar();
                                    gc.setTime(dat);
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    String sb=format.format(gc.getTime());
                                    mGYPZYDEntity.setHTYDDGSJ(sb);
                                }else{
                                    mGYPZYDEntity.setHTYDDGSJ("无数据");
                                }
                            }
                            if(o.has("HTYDJDSJ") && o.get("HTYDJDSJ")!=null){
                                if(o.get("HTYDJDSJ").toString().contains("Date")){
                                    int len=o.get("HTYDJDSJ").toString().length();
                                    String d=o.get("HTYDJDSJ").toString().substring(6,len-2);
                                    Date dat=new Date(Long.parseLong(d));
                                    GregorianCalendar gc = new GregorianCalendar();
                                    gc.setTime(dat);
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    String sb=format.format(gc.getTime());
                                    mGYPZYDEntity.setHTYDJDSJ(sb);
                                }else{
                                    mGYPZYDEntity.setHTYDJDSJ("无数据");
                                }
                            }
                            if(o.has("HTBH") && o.get("HTBH")!=null){
                                mGYPZYDEntity.setHTBH(o.get("HTBH").toString());
                            }
                            if(o.has("CJJK") && o.get("CJJK")!=null){
                                mGYPZYDEntity.setCJJK(o.get("CJJK").toString());
                            }
                            if(o.has("YDXMMC") && o.get("YDXMMC")!=null){
                                mGYPZYDEntity.setYDXMMC(o.get("YDXMMC").toString());
                            }
                            if(o.has("YDDWMC") && o.get("YDDWMC")!=null){
                                mGYPZYDEntity.setYDDWMC(o.get("YDDWMC").toString());
                            }
                            if(o.has("GDFS") && o.get("GDFS")!=null){
                                mGYPZYDEntity.setGDFS(o.get("GDFS").toString());
                            }
                            if(o.has("LHBL") && o.get("LHBL")!=null){
                                mGYPZYDEntity.setLHBL(o.get("LHBL").toString());
                            }
                            if(o.has("JZMD") && o.get("JZMD")!=null){
                                mGYPZYDEntity.setJZMD(o.get("JZMD").toString());
                            }
                            if(o.has("RJL") && o.get("RJL")!=null){
                                mGYPZYDEntity.setRJL(o.get("RJL").toString());
                            }
                            if(o.has("TDYT") && o.get("TDYT")!=null){
                                mGYPZYDEntity.setTDYT(o.get("TDYT").toString());
                            }

                            if(o.has("GDPFSJ") && o.get("GDPFSJ")!=null){
                                if(o.get("GDPFSJ").toString().contains("Date")){
                                    int len=o.get("GDPFSJ").toString().length();
                                    String d=o.get("GDPFSJ").toString().substring(6,len-2);
                                    Date dat=new Date(Long.parseLong(d));
                                    GregorianCalendar gc = new GregorianCalendar();
                                    gc.setTime(dat);
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    String sb=format.format(gc.getTime());
                                    mGYPZYDEntity.setGDPFSJ(sb);
                                }else{
                                    mGYPZYDEntity.setGDPFSJ("无数据");
                                }
                            }

                            if(o.has("GDPFWH") && o.get("GDPFWH")!=null){
                                mGYPZYDEntity.setGDPFWH(o.get("GDPFWH").toString());
                            }
                            if(o.has("GDPFMC") && o.get("GDPFMC")!=null){
                                mGYPZYDEntity.setGDPFMC(o.get("GDPFMC").toString());
                            }
                            if(o.has("GYMJ") && o.get("GYMJ")!=null){
                                mGYPZYDEntity.setGYMJ(o.get("GYMJ").toString());
                            }
                            if(o.has("FFID") && o.get("FFID")!=null){
                                mGYPZYDEntity.setFFID(o.get("FFID").toString());
                            }
                            if(o.has("PZWH") && o.get("PZWH")!=null){
                                mGYPZYDEntity.setPZWH(o.get("PZWH").toString());
                            }
                            if(o.has("GYYDBH") && o.get("GYYDBH")!=null){
                                mGYPZYDEntity.setGYYDBH(o.get("GYYDBH").toString());
                            }
                            if(o.has("X") && o.get("X")!=null){
                                mGYPZYDEntity.setX(o.get("X").toString());
                            }
                            if(o.has("Y") && o.get("Y")!=null){
                                mGYPZYDEntity.setY(o.get("Y").toString());
                            }
                            if(o.has("OBJECTID")  && o.get("OBJECTID")!=null ){
                                mGYPZYDEntity.setOBJECTID(o.get("OBJECTID").toString());
                            }

                            if(o.has("CUN") && o.get("CUN")!=null){
                                mGYPZYDEntity.setCUN(o.get("CUN").toString());
                            }
                            if(o.has("XZ") && o.get("XZ")!=null){
                                mGYPZYDEntity.setXZ(o.get("XZ").toString());
                            }
                            if(o.has("DTTF") && o.get("DTTF")!=null){
                                mGYPZYDEntity.setDTTF(o.get("DTTF").toString());
                            }
                            if(o.has("Shape") && o.get("Shape")!=null){
                                mGYPZYDEntity.setShape(o.get("Shape").toString());
                            }



                            GYPZYD_list.add(mGYPZYDEntity);
                        }
                    }else{
                        ToastUtil.show(GYPZYDQueryActivity.this,"服务器返回值为空");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                    ToastUtil.show(GYPZYDQueryActivity.this,"连接服务器超时");
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.show(GYPZYDQueryActivity.this,"解析JSON错误");
                }

                if(GYPZYD_list!=null && GYPZYD_list.size()>0){
                    MyApp.setGYPZYD_QUERY_list(null);
                    MyApp.setGYPZYD_QUERY_list(GYPZYD_list);
                    Intent toListShowIntent=new Intent();
                    toListShowIntent.setClass(this,GYPZYDListShowActivity.class);
                    startActivity(toListShowIntent);
                }else{
                    ToastUtil.show(this,"没有该用地数据");
                }
                break;
            case R.id.backtextview:
                GYPZYDQueryActivity.this.finish();
                break;
            default:
                break;
        }
    }

    public class GetDZDataSync extends AsyncTask<String, Integer, String> {

        String gynf=null;
        String ssc=null;
        String xmmc=null;

        public GetDZDataSync( String gynf, String ssc, String xmmc) {

            this.gynf = gynf;
            this.xmmc = xmmc;
            this.ssc = ssc;
            ksoap=new KsoapValidateHttp(GYPZYDQueryActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetLandProvision(gynf, xmmc, ssc);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            GYPZYDQueryActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


}