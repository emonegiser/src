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
import com.arcgis.entity.PZYDEntity;
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

public class PZYDQueryActivity extends Activity implements View.OnClickListener{

    //土地征收名称
    Spinner spinnertdbpmc=null;
    //批准年份
    Spinner spinnerbpnf= null;
    //地址(所属村)
    Spinner spinnerssc=null;

    private Button queryBtn=null;

    private TextView backtextview;
    private TextView titletextview;

    //土地报批名称
    ArrayAdapter<String> spinnertdbpmcAdapter;
    //所属镇
//    ArrayAdapter<String> spinnersszAdapter;
    //所属村
    ArrayAdapter<String> spinnersscAdapter;
    //报批年份
    ArrayAdapter<String> spinnerbpnfAdapter;

    private PZYDEntity mPZYDEntity;
    private List<PZYDEntity> PZYD_list=new ArrayList<PZYDEntity>();

    //全局变量存储位置
    private App MyApp;

    //调用webservice
    private KsoapValidateHttp ksoap;


    ///批准年份String pznf,String tdzsmc,String dz
    String pznf=null;
    //地址
    String dz=null;
    //土地征收名称
    String tdzsmc=null;

    private boolean isNetwork=false;
    List<String> bpnf_List=new ArrayList<String>();
    SimpleDateFormat format=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_querypzyd);
        isNetwork= NetUtils.isNetworkAvailable(this);
        App.getInstance().addActivity(this);

        MyApp=(App) this.getApplication();

        queryBtn= (Button) findViewById(R.id.queryBtn);
        queryBtn.setOnClickListener(this);

        backtextview=(TextView)findViewById(R.id.backtextview);
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.titletextview);
        titletextview.setText("查询批准用地");


        spinnerbpnf=(Spinner)findViewById(R.id.spinnerbpnf);
        spinnerssc=(Spinner)findViewById(R.id.spinnerssc);
        spinnertdbpmc=(Spinner)findViewById(R.id.spinnertdbpmc);

        if(MyApp.getCun_list()!=null && MyApp.getCun_list().size()>0){
            spinnersscAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, MyApp.getCun_list());
        }
        spinnersscAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if(spinnersscAdapter!=null){
            spinnerssc.setAdapter(spinnersscAdapter);
        }


        //获取报批批次
        if(MyApp.getSBYD_BPPC_list()!=null && MyApp.getSBYD_BPPC_list().size()>0){
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
                if(PZYD_list!=null && PZYD_list.size()>0){
                    PZYD_list.clear();
                }

                String pznf=spinnerbpnf.getSelectedItem().toString();
                if(pznf.equals("全部")){
                    pznf="";
                }

                String dz=spinnerssc.getSelectedItem().toString();
                if(dz.equals("全部")){
                    dz="";
                }
                tdzsmc=spinnertdbpmc.getSelectedItem().toString();
                if(tdzsmc.equals("全部")){
                    tdzsmc="";
                }



                GetDZDataSync getDZDataSync=new GetDZDataSync(pznf,tdzsmc,dz);
                try {
                    String dataRslt=getDZDataSync.execute().get(5000, TimeUnit.MILLISECONDS);
                    if(dataRslt!=null && dataRslt.contains("")){
                        JSONArray jsonArray = new JSONArray(dataRslt);
                        for(int k=0;k<jsonArray.length();k++){
                            JSONObject o = (JSONObject) jsonArray.get(k);
                            mPZYDEntity=new PZYDEntity();
                            if(o.has("X") && o.get("X")!=null){
                                mPZYDEntity.setX(o.get("X").toString());
                            }
                            if(o.has("Y") && o.get("Y")!=null){
                                mPZYDEntity.setY(o.get("Y").toString());
                            }
                            if(o.has("OBJECTID")  && o.get("OBJECTID")!=null ){
                                mPZYDEntity.setOBJECTID(o.get("OBJECTID").toString());
                            }
                            if(o.has("OBJECTID_1")  && o.get("OBJECTID_1")!=null ){
                                mPZYDEntity.setOBJECTID_1(o.get("OBJECTID_1").toString());
                            }
                            if(o.has("BH")  && o.get("BH")!=null ){
                                mPZYDEntity.setBH(o.get("BH").toString());
                            }
                            if(o.has("DKZL") && o.get("DKZL")!=null){
                                mPZYDEntity.setDKZL(o.get("DKZL").toString());
                            }
                            if(o.has("DKMJ") && o.get("DKMJ")!=null){
                                mPZYDEntity.setDKMJ(o.get("DKMJ").toString());
                            }
                            if(o.has("ZDBCFY") && o.get("ZDBCFY")!=null){
                                mPZYDEntity.setZDBCFY(o.get("ZDBCFY").toString());
                            }
                            if(o.has("DSFZWBCFY") && o.get("DSFZWBCFY")!=null){
                                mPZYDEntity.setDSFZWBCFY(o.get("DSFZWBCFY").toString());
                            }



                            if(o.has("FFID") && o.get("FFID")!=null){
                                mPZYDEntity.setFFID(o.get("FFID").toString());
                            }
                            if(o.has("PZWH") && o.get("PZWH")!=null){
                                mPZYDEntity.setPZWH(o.get("PZWH").toString());
                            }
                            if(o.has("TDZSWH") && o.get("TDZSWH")!=null){
                                mPZYDEntity.setTDZSWH(o.get("TDZSWH").toString());
                            }
                            if(o.has("TDZSMC") && o.get("TDZSMC")!=null){
                                mPZYDEntity.setTDZSMC(o.get("TDZSMC").toString());
                            }
                            if(o.has("CUN") && o.get("CUN")!=null){
                                mPZYDEntity.setCUN(o.get("CUN").toString());
                            }
                            if(o.has("XZ") && o.get("XZ")!=null){
                                mPZYDEntity.setXZ(o.get("XZ").toString());
                            }
                            if(o.has("DTTF") && o.get("DTTF")!=null){
                                mPZYDEntity.setDTTF(o.get("DTTF").toString());
                            }
                            if(o.has("Shape") && o.get("Shape")!=null){
                                mPZYDEntity.setShape(o.get("Shape").toString());
                            }

                            if(o.has("BPID") && o.get("BPID")!=null){
                                mPZYDEntity.setBPID(o.get("BPID").toString());
                            }
                            if(o.has("BPMJ") && o.get("BPMJ")!=null){
                                mPZYDEntity.setBPMJ(o.get("BPMJ").toString());
                            }
                            if(o.has("PZSJ") && o.get("PZSJ")!=null){
                                if(o.get("PZSJ").toString().contains("Date")){
                                    int len=o.get("PZSJ").toString().length();
                                    String d=o.get("PZSJ").toString().substring(6,len-2);
                                    Date dat=new Date(Long.parseLong(d));
                                    GregorianCalendar gc = new GregorianCalendar();
                                    gc.setTime(dat);
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    String sb=format.format(gc.getTime());
                                    mPZYDEntity.setPZSJ(sb);
                                }else{
                                    mPZYDEntity.setPZSJ("无数据");
                                }
                            }
                            if(o.has("LGGYDJSJ") && o.get("LGGYDJSJ")!=null){
                                if(o.get("LGGYDJSJ").toString().contains("Date")){
                                    int len=o.get("LGGYDJSJ").toString().length();
                                    String d=o.get("LGGYDJSJ").toString().substring(6,len-2);
                                    Date dat=new Date(Long.parseLong(d));
                                    GregorianCalendar gc = new GregorianCalendar();
                                    gc.setTime(dat);
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    String sb=format.format(gc.getTime());
                                    mPZYDEntity.setLGGYDJSJ(sb);
                                }else{
                                    mPZYDEntity.setLGGYDJSJ("无数据");
                                }
                            }

                            PZYD_list.add(mPZYDEntity);
                        }
                    }else{
                        ToastUtil.show(PZYDQueryActivity.this,"服务器返回值为空");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                    ToastUtil.show(PZYDQueryActivity.this,"连接服务器超时");
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.show(PZYDQueryActivity.this,"解析JSON错误");
                }

                if(PZYD_list!=null && PZYD_list.size()>0){
                    MyApp.setPZYD_QUERY_list(null);
                    MyApp.setPZYD_QUERY_list(PZYD_list);
                    Intent toListShowIntent=new Intent();
                    toListShowIntent.setClass(this,PZYDListShowActivity.class);
                    startActivity(toListShowIntent);
                }else{
                    ToastUtil.show(this,"没有该用地数据");
                }
                break;
            case R.id.backtextview:
                PZYDQueryActivity.this.finish();
                break;
            default:
                break;
        }
    }

    public class GetDZDataSync extends AsyncTask<String, Integer, String> {
        String pznf=null;
        String tdzsmc=null;
        String dz=null;

        public GetDZDataSync( String pznf,String tdzsmc,String dz) {

            this.pznf = pznf;
            this.tdzsmc = tdzsmc;
            this.dz = dz;
            ksoap=new KsoapValidateHttp(PZYDQueryActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetLandRatify( pznf, tdzsmc, dz);
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
            PZYDQueryActivity.this.finish();
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