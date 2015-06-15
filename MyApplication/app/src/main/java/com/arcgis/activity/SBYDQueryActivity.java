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
import com.arcgis.entity.SBYDEntity;
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

public class SBYDQueryActivity extends Activity implements View.OnClickListener{

    //报批名称
    Spinner spinnertdbpmc=null;
    //报批年份
    Spinner spinnerbpnf= null;
    //所属镇
    Spinner spinnerssz=null;
    //所属村
    Spinner spinnerssc=null;

    private Button queryBtn=null;

    private TextView backtextview;
    private TextView titletextview;

    //土地报批名称
    ArrayAdapter<String> spinnertdbpmcAdapter;
    //所属镇
    ArrayAdapter<String> spinnersszAdapter;
    //所属村
    ArrayAdapter<String> spinnersscAdapter;
    //报批年份
    ArrayAdapter<String> spinnerbpnfAdapter;

    private SBYDEntity mSBYDEntity;
    private List<SBYDEntity> SBYD_list=new ArrayList<SBYDEntity>();

    //全局变量存储位置
    private App MyApp;

    //调用webservice
    private KsoapValidateHttp ksoap;

    //报批批次
    String bppc=null;
    //报批年份
    String bpnf=null;
    //所属村
    String ssc=null;
    //所属镇
    String ssz=null;
    private boolean isNetwork=false;
    List<String> bpnf_List=new ArrayList<String>();
    SimpleDateFormat format=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_querysbyd);
        isNetwork= NetUtils.isNetworkAvailable(this);
        App.getInstance().addActivity(this);

        MyApp=(App) this.getApplication();

        queryBtn= (Button) findViewById(R.id.queryBtn);
        queryBtn.setOnClickListener(this);

        backtextview=(TextView)findViewById(R.id.backtextview);
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.titletextview);
        titletextview.setText("查询上报用地");


        spinnerssz= (Spinner) this.findViewById(R.id.spinnerssz);
        spinnerbpnf=(Spinner)findViewById(R.id.spinnerbpnf);
        spinnerssc=(Spinner)findViewById(R.id.spinnerssc);
        spinnertdbpmc=(Spinner)findViewById(R.id.spinnertdbpmc);

        if(MyApp.getXiangzhen_list()!=null && MyApp.getXiangzhen_list().size()>0){
            //MyApp.getXiangzhen_list().add(0,"全部");
            spinnersszAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, MyApp.getXiangzhen_list());
        }
        spinnersszAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerssz.setAdapter(spinnersszAdapter);


        if(MyApp.getCun_list()!=null && MyApp.getCun_list().size()>0){
            //MyApp.getCun_list().add(0,"全部");
            spinnersscAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, MyApp.getCun_list());
        }
        spinnersscAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerssc.setAdapter(spinnersscAdapter);

        if(MyApp.getSBYD_BPPC_list()!=null && MyApp.getSBYD_BPPC_list().size()>0){
            //MyApp.getSBYD_BPPC_list().add(0,"全部");
            spinnertdbpmcAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,MyApp.getSBYD_BPPC_list());
        }
        spinnertdbpmcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnertdbpmc.setAdapter(spinnertdbpmcAdapter);

        format = new SimpleDateFormat("yyyy");
        String dataStr = format.format(new Date());
        int dataInt=Integer.parseInt(dataStr);
        bpnf_List.clear();
        for(int i=0;i<15;i++){
            bpnf_List.add((dataInt-i)+"");
        }
        spinnerbpnfAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, bpnf_List);
        spinnerbpnfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerbpnf.setAdapter(spinnerbpnfAdapter);
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
                if(SBYD_list!=null && SBYD_list.size()>0){
                    SBYD_list.clear();
                }

                String bpnf=spinnerbpnf.getSelectedItem().toString();
                if(bpnf.equals("全部")){
                    bpnf="";
                }
                String ssz=spinnerssz.getSelectedItem().toString();
                if(ssz.equals("全部")){
                    ssz="";
                }
                String ssc=spinnerssc.getSelectedItem().toString();
                if(ssc.equals("全部")){
                    ssc="";
                }
                //"OBJECTID":3275,"BH":"6938029","DKZL":"1","DKMJ":14233378.73254800,"ZDBCFY":"123",
                // "DSFZWBCF":"2343","FFID":221,"SSSJ":null,"X":105.14485067,"Y":27.16151069,"QSLB":null,
                // "BSSJ":"\/Date(1421769600000)\/","PFSJ":null,"DJSJ":null,"TDZSMC":"ddasda","TDZSWH":null,
                // "CUN":"法朗村","XZ":"千溪乡","DTTF":"G48 G 020051,G48 G 020050,G48 G 021051","Shape":3212

                GetDZDataSync getDZDataSync=new GetDZDataSync(bpnf,ssz,ssc);
                try {
                    String dataRslt=getDZDataSync.execute().get(5000, TimeUnit.MILLISECONDS);
                    if(dataRslt!=null && dataRslt.contains("")){
                        JSONArray jsonArray = new JSONArray(dataRslt);
                        for(int k=0;k<jsonArray.length();k++){
                            JSONObject o = (JSONObject) jsonArray.get(k);
                            mSBYDEntity=new SBYDEntity();
                            if(o.has("X") && o.get("X")!=null){
                                mSBYDEntity.setX(o.get("X").toString());
                            }
                            if(o.has("Y") && o.get("Y")!=null){
                                mSBYDEntity.setY(o.get("Y").toString());
                            }
                            if(o.has("OBJECTID")  && o.get("OBJECTID")!=null ){
                                mSBYDEntity.setOBJECTID(o.get("OBJECTID").toString());
                            }
                            if(o.has("BH")  && o.get("BH")!=null ){
                                mSBYDEntity.setBH(o.get("BH").toString());
                            }
                            if(o.has("DKZL") && o.get("DKZL")!=null){
                                mSBYDEntity.setDKZL(o.get("DKZL").toString());
                            }
                            if(o.has("DKMJ") && o.get("DKMJ")!=null){
                                mSBYDEntity.setDKMJ(o.get("DKMJ").toString());
                            }
                            if(o.has("ZDBCFY") && o.get("ZDBCFY")!=null){
                                mSBYDEntity.setZDBCFY(o.get("ZDBCFY").toString());
                            }
                            if(o.has("DSFZWBCF") && o.get("DSFZWBCF")!=null){
                                mSBYDEntity.setDSFZWBCF(o.get("DSFZWBCF").toString());
                            }
                            if(o.has("FFID") && o.get("FFID")!=null){
                                mSBYDEntity.setFFID(o.get("FFID").toString());
                            }
                            if(o.has("SSSJ") && o.get("SSSJ")!=null){
                                mSBYDEntity.setSSSJ(o.get("SSSJ").toString());
                            }
                            if(o.has("QSLB") && o.get("QSLB")!=null){
                                mSBYDEntity.setQSLB(o.get("QSLB").toString());
                            }
                            if(o.has("PFSJ") && o.get("PFSJ")!=null){
                                mSBYDEntity.setPFSJ(o.get("PFSJ").toString());
                            }
                            if(o.has("DJSJ") && o.get("DJSJ")!=null){
                                mSBYDEntity.setDJSJ(o.get("DJSJ").toString());
                            }
                            if(o.has("TDZSWH") && o.get("TDZSWH")!=null){
                                mSBYDEntity.setTDZSWH(o.get("TDZSWH").toString());
                            }
                            if(o.has("TDZSMC") && o.get("TDZSMC")!=null){
                                mSBYDEntity.setTDZSMC(o.get("TDZSMC").toString());
                            }
                            if(o.has("CUN") && o.get("CUN")!=null){
                                mSBYDEntity.setCUN(o.get("CUN").toString());
                            }
                            if(o.has("XZ") && o.get("XZ")!=null){
                                mSBYDEntity.setXZ(o.get("XZ").toString());
                            }
                            if(o.has("DTTF") && o.get("DTTF")!=null){
                                mSBYDEntity.setDTTF(o.get("DTTF").toString());
                            }
                            if(o.has("Shape") && o.get("Shape")!=null){
                                mSBYDEntity.setShape(o.get("Shape").toString());
                            }

                            if(o.has("BSSJ") && o.get("BSSJ")!=null){
                                if(o.get("BSSJ").toString().contains("Date")){
                                    int len=o.get("BSSJ").toString().length();
                                    String d=o.get("BSSJ").toString().substring(6,len-2);
                                    Date dat=new Date(Long.parseLong(d));
                                    GregorianCalendar gc = new GregorianCalendar();
                                    gc.setTime(dat);
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    String sb=format.format(gc.getTime());
                                    mSBYDEntity.setBSSJ(sb);
                                }else{
                                    mSBYDEntity.setBSSJ("无数据");
                                }
                            }

                            SBYD_list.add(mSBYDEntity);
                        }
                    }else{
                        ToastUtil.show(SBYDQueryActivity.this,"服务器返回值为空");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                    ToastUtil.show(SBYDQueryActivity.this,"连接服务器超时");
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.show(SBYDQueryActivity.this,"解析JSON错误");
                }

                if(SBYD_list!=null && SBYD_list.size()>0){
                    MyApp.setSBYD_QUERY__list(null);
                    MyApp.setSBYD_QUERY__list(SBYD_list);
                    Intent toListShowIntent=new Intent();
                    toListShowIntent.setClass(this,SBYDListShowActivity.class);
                    startActivity(toListShowIntent);
                }else{
                    ToastUtil.show(this,"没有该用地数据");
                }
                break;
            case R.id.backtextview:
                SBYDQueryActivity.this.finish();
                break;
            default:
                break;
        }
    }

    public class GetDZDataSync extends AsyncTask<String, Integer, String> {

        String bpnf=null;
        String ssc=null;
        String ssz=null;

        public GetDZDataSync( String bpnf, String ssz, String ssc) {

            this.bpnf = bpnf;
            this.ssz = ssz;
            this.ssc = ssc;
            ksoap=new KsoapValidateHttp(SBYDQueryActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetLandReported(bpnf,ssc,ssz);
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
            SBYDQueryActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * 日期选择器
     */
//    public class DatePickerFragment extends DialogFragment implements
//            DatePickerDialog.OnDateSetListener {
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            final Calendar c = Calendar.getInstance();
//            int year = c.get(Calendar.YEAR);
//            int month = c.get(Calendar.MONTH);
//            int day = c.get(Calendar.DAY_OF_MONTH);
//            return new DatePickerDialog(getActivity(), this, year, month, day);
//        }
//        @Override
//        public void onDateSet(DatePicker view, int year, int month, int day) {
//            Log.d("OnDateSet", "select year:" + year + ";month:" + month + ";day:" + day);
//
//            String m="";
//            String d="";
//            if(month<10){
//                m="0"+(month+1)+"";
//            }else{
//                m=month+"";
//            }
//
//            if(day<10){
//                d="0"+day+"";
//            }else{
//                d=day+"";
//            }
//
//            editTextsj.setText(year+"-"+m+"-"+d);
//        }
//    }
}