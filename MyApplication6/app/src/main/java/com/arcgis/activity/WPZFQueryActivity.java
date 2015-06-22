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
import android.widget.Spinner;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.dao.WPZFDao;
import com.arcgis.entity.WPZFEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;
import com.arcgis.selectdao.DZSYSDICEntityDao;
import com.arcgis.selectentity.DZSYSDICEntity;

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

//卫片任务
public class WPZFQueryActivity extends Activity implements View.OnClickListener{

    //任务状态
    Spinner spinnerrwzt= null;
    //任务年份
    Spinner spinnerrwnf=null;
    //任务乡镇
    Spinner spinnerrwxz=null;

    private Button queryBtn=null;

    private TextView backtextview;
    private TextView titletextview;

    //任务状态
    ArrayAdapter<String> spinnerrwztAdapter;
    //任务年份
    ArrayAdapter<String> spinnerrwnfAdapter;
    //任务乡镇
    ArrayAdapter<String> spinnerrwxzAdapter;

    private WPZFEntity mWPZFEntity;
    private List<WPZFEntity> WPZF_list=new ArrayList<>();

    //全局变量存储位置
    private App MyApp;

    //调用webservice
    private KsoapValidateHttp ksoap;

    private boolean isNetwork=false;
    List<String> bpnf_List=new ArrayList<>();
    List<String> rwzt_List=new ArrayList<>();

    SimpleDateFormat format=null;
    DZSYSDICEntityDao dzsysdicEntityDao=null;
    WPZFDao wpzfDao=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_querywpzf);
        isNetwork= NetUtils.isNetworkAvailable(this);
        App.getInstance().addActivity(this);

        MyApp=(App) this.getApplication();
        dzsysdicEntityDao=new DZSYSDICEntityDao(this);
        wpzfDao=new WPZFDao(this);

        queryBtn= (Button) findViewById(R.id.queryBtn);
        queryBtn.setOnClickListener(this);

        backtextview=(TextView)findViewById(R.id.backtextview);
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.titletextview);
        titletextview.setText("卫片任务");

        spinnerrwzt=(Spinner)findViewById(R.id.spinnerrwzt);
        spinnerrwnf=(Spinner)findViewById(R.id.spinnerrwnf);
        spinnerrwxz=(Spinner)findViewById(R.id.spinnerrwxz);

        if(isNetwork){
            DZSYSDICEntity dzsysdicEntity=new DZSYSDICEntity();
            GetRWZTDataSync getRWZTDataSync=new GetRWZTDataSync();
            try {
                String dataRslt=getRWZTDataSync.execute().get(25, TimeUnit.SECONDS);
                if(dataRslt!=null && dataRslt.contains("")){
                    JSONArray jsonArray = new JSONArray(dataRslt);
                    rwzt_List.clear();
                    for(int k=0;k<jsonArray.length();k++){
                        JSONObject o = (JSONObject) jsonArray.get(k);
                        mWPZFEntity=new WPZFEntity();
                        if(o.has("state") && o.get("state")!=null){
                            rwzt_List.add(o.get("state").toString());
                            dzsysdicEntity.setName(o.get("state").toString());
                            dzsysdicEntity.setType("WPZFSTATE");

                            if(!dzsysdicEntityDao.isEntityExist(o.get("state").toString())){
                                dzsysdicEntityDao.add(dzsysdicEntity);
                            }
                        }
                    }
                }else{
                    ToastUtil.show(WPZFQueryActivity.this,"服务器返回值为空");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                ToastUtil.show(WPZFQueryActivity.this,"连接服务器超时");
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtil.show(WPZFQueryActivity.this,"解析JSON错误");
            }
        }else{
            //没有网络，从数据库中读取
            rwzt_List.clear();
            List<DZSYSDICEntity> XCRWSTATEList =dzsysdicEntityDao.queryByType("WPZFSTATE");
            for(DZSYSDICEntity dz:XCRWSTATEList){
                rwzt_List.add(dz.getName());
            }
        }

        //任务状态
        if(rwzt_List!=null && rwzt_List.size()>0){
            spinnerrwztAdapter= new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,rwzt_List);
            spinnerrwztAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        spinnerrwzt.setAdapter(spinnerrwztAdapter);

        //乡镇
        spinnerrwxzAdapter= new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,MyApp.getXiangzhen_list());
        spinnerrwxzAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerrwxz.setAdapter(spinnerrwxzAdapter);

        format = new SimpleDateFormat("yyyy");
        String dataStr = format.format(new Date());
        int dataInt=Integer.parseInt(dataStr);
        bpnf_List.clear();
        for(int i=0;i<15;i++){
            bpnf_List.add((dataInt-i)+"");
        }
        spinnerrwnfAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, bpnf_List);
        spinnerrwnfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerrwnf.setAdapter(spinnerrwnfAdapter);
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
                isNetwork= NetUtils.isNetworkAvailable(this);

                //巡查年份
                String pznf=spinnerrwnf.getSelectedItem().toString();

                //任务乡镇
                String rwxz=spinnerrwxz.getSelectedItem().toString();
                if(rwxz.equals("全部")){
                    rwxz="";
                }

                //任务状态
                String rwzt=spinnerrwzt.getSelectedItem().toString();

                WPZF_list.clear();
                /**
                 * [{"ID":5,"SENDERID":"2323","SENDERNAME":"李华","RECIVERID":"hf1111,zj,2323","RECIVERNAME":"华峰,zj,李华",
                 * "WPURLID":"1,5,4","XZ":"阿市乡,对坡镇,观音桥","STATE":"已完成","TASKREMARK":"阿萨德发送到",
                 * "TASKDATE":"\/Date(1424016000000)\/","RESULTDATE":"\/Date(1423238400000)\/","RESULTREMARK":"adfad",
                 * "RESULTFILES":"ArcGIS API for JS开发教程.pdf"}]
                 */

                if(isNetwork){

                    GetXCRWDataSync getXCRWDataSync=new GetXCRWDataSync(pznf,rwxz,rwzt);
                    try {
                        String dataRslt=getXCRWDataSync.execute().get(20, TimeUnit.SECONDS);
                        if(dataRslt!=null && dataRslt.contains("")){
                            JSONArray jsonArray = new JSONArray(dataRslt);
                            for(int k=0;k<jsonArray.length();k++){
                                JSONObject o = (JSONObject) jsonArray.get(k);
                                mWPZFEntity=new WPZFEntity();

                                if(o.has("ID") && o.get("ID")!=null){
                                    mWPZFEntity.setID(o.get("ID").toString());
                                }
                                if(o.has("SENDERID") && o.get("SENDERID")!=null){
                                    mWPZFEntity.setSENDERID(o.get("SENDERID").toString());
                                }
                                if(o.has("SENDERNAME") && o.get("SENDERNAME")!=null){
                                    mWPZFEntity.setSENDERNAME(o.get("SENDERNAME").toString());
                                }

                                if(o.has("RECIVERID") && o.get("RECIVERID")!=null){
                                    mWPZFEntity.setRECIVERID(o.get("RECIVERID").toString());
                                }
                                if(o.has("RECIVERNAME") && o.get("RECIVERNAME")!=null){
                                    mWPZFEntity.setRECIVERNAME(o.get("RECIVERNAME").toString());
                                }
                                if(o.has("WPURLID") && o.get("WPURLID")!=null){
                                    mWPZFEntity.setWPURLID(o.get("WPURLID").toString());
                                }
                                if(o.has("XZ") && o.get("XZ")!=null){
                                    mWPZFEntity.setXZ(o.get("XZ").toString());
                                }
                                if(o.has("STATE") && o.get("STATE")!=null){
                                    mWPZFEntity.setSTATE(o.get("STATE").toString());
                                }
                                if(o.has("TASKREMARK") && o.get("TASKREMARK")!=null){
                                    mWPZFEntity.setTASKREMARK(o.get("TASKREMARK").toString());
                                }
                                if(o.has("RESULTDATE") && o.get("RESULTDATE")!=null){
                                    if(o.get("RESULTDATE").toString().contains("Date")){
                                        int len=o.get("RESULTDATE").toString().length();
                                        String d=o.get("RESULTDATE").toString().substring(6,len-2);
                                        Date dat=new Date(Long.parseLong(d));
                                        GregorianCalendar gc = new GregorianCalendar();
                                        gc.setTime(dat);
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                        String sb=format.format(gc.getTime());
                                        mWPZFEntity.setRESULTDATE(sb);
                                    }else{
                                        mWPZFEntity.setRESULTDATE("无数据");
                                    }
                                }
                                if(o.has("RESULTREMARK") && o.get("RESULTREMARK")!=null){
                                    mWPZFEntity.setRESULTREMARK(o.get("RESULTREMARK").toString());
                                }
                                if(o.has("RESULTFILES") && o.get("RESULTFILES")!=null){
                                    mWPZFEntity.setRESULTFILES(o.get("RESULTFILES").toString());
                                }

                                if(o.has("TASKDATE") && o.get("TASKDATE")!=null){
                                    if(o.get("TASKDATE").toString().contains("Date")){
                                        int len=o.get("TASKDATE").toString().length();
                                        String d=o.get("TASKDATE").toString().substring(6,len-2);
                                        Date dat=new Date(Long.parseLong(d));
                                        GregorianCalendar gc = new GregorianCalendar();
                                        gc.setTime(dat);
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                        String sb=format.format(gc.getTime());
                                        mWPZFEntity.setTASKDATE(sb);
                                    }else{
                                        mWPZFEntity.setTASKDATE("无数据");
                                    }
                                }

                                WPZF_list.add(mWPZFEntity);
                                if(!wpzfDao.isExistEntity(o.get("ID").toString().trim())){
                                    wpzfDao.add(mWPZFEntity);
                                }
                            }
                        }else{
                            ToastUtil.show(WPZFQueryActivity.this,"服务器返回值为空");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        ToastUtil.show(WPZFQueryActivity.this,"连接服务器超时");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtil.show(WPZFQueryActivity.this,"解析JSON错误");
                    }
                }else{
                    ToastUtil.show(WPZFQueryActivity.this,"网络异常，无法查询");
                }


                if(WPZF_list!=null && WPZF_list.size()>0){
                    MyApp.setWPZF_list(null);
                    MyApp.setWPZF_list(WPZF_list);
                    Intent toListShowIntent=new Intent();
                    toListShowIntent.setClass(this,WPZFListShowActivity.class);
                    startActivity(toListShowIntent);
                }else{
                    ToastUtil.show(this,"没有该卫片数据");
                }
                break;
            case R.id.backtextview:
                WPZFQueryActivity.this.finish();
                break;
            default:
                break;
        }
    }

    public class GetXCRWDataSync extends AsyncTask<String, Integer, String> {
        String year;
        String rwxz;
        String zt;

        public GetXCRWDataSync(String year,String rwxz,String zt) {
            ksoap=new KsoapValidateHttp(WPZFQueryActivity.this);
            this.year=year;
            this.rwxz=rwxz;
            this.zt=zt;

        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetWeiChipTask(year,zt,rwxz);
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


    //获取任务状态
    public class GetRWZTDataSync extends AsyncTask<String, Integer, String> {

        public GetRWZTDataSync() {
            ksoap=new KsoapValidateHttp(WPZFQueryActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetWeiChipState();
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
            WPZFQueryActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}