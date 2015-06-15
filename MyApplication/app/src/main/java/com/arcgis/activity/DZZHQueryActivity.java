package com.arcgis.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.entity.DZZHEntity;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DZZHQueryActivity extends Activity implements View.OnClickListener{

    Spinner spinnerxzh=null; //所在乡镇
    Spinner spinnerdzlx= null;//地灾类型
    EditText editTextcfsj =null;//发生时间


    private TextView backtextview;
    private TextView titletextview;

    private Button queryBtn=null;

    ArrayAdapter<String> spinnerDzlxAdapter;
    ArrayAdapter<String> spinnerxzhAdapter;

    private DZZHEntity mDZZHEntity;
    private List<DZZHEntity> DZZH_list=new ArrayList<DZZHEntity>();

    //全局变量存储位置
    private App MyApp;

    //调用webservice
    private KsoapValidateHttp ksoap;

    //乡镇
    String xz=null;

    private boolean isNetwork=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_querydzzh);
        isNetwork= NetUtils.isNetworkAvailable(this);
        App.getInstance().addActivity(this);
        MyApp=(App) this.getApplication();

        queryBtn= (Button) findViewById(R.id.queryBtn);//查询
        queryBtn.setOnClickListener(this);

        backtextview= (TextView) findViewById(R.id.backtextview);//返回
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.titletextview);//标题
        titletextview.setText("查询地质灾害点");

        spinnerxzh= (Spinner) this.findViewById(R.id.spinnerxzh);

        spinnerxzhAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,MyApp.getCx_list());
        spinnerxzhAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerxzh.setAdapter(spinnerxzhAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * {"OBJECTID":162,"Id":162,"X":35550405.92577200,"Y":3070509.05299000,"E":105.51126100,"N":27.74698200,
     * "TBNAME":"sde.SDE.BJS_DZZH_PT","ZHDBH":"162","XZ":"生机镇","CUN":"镇江村","XXWZ":"镇江村李家坡","ZHLX":"滑坡",
     * "DZDJ":" ","GMMS":"16(万m3)","DZWDX":"不稳定","ZHTMJ":0.00000000,"ZHTTJ":0.00000000,"YXFW":0.00000000,
     * "ZJJJSS":20.00000000,"JJJJSS":0.00000000,"YFYS":"自然","WXHS":10,"WXRS":45,"JCR":"吴文高",
     * "JCDH":"15086319600","CLYJ":" ","FSSJ":null,"SBSJ":null,"TPLJ":"upLoadFile/dzzh/pic/P6090012.JPG","SPLJ":" ",
     * "MXLJ":"http://bjsgt/BJGT2010_PIC/zhen.gif","IP":"http://cqtk/BJGT2010_PIC/","MXMC":"zhen.gif","FFID":162,"Shape":162}
     *
     * "X":35550405.92577200,"Y":3070509.05299000,"E":105.51126100,"N":27.74698200,
     * "ZHDBH":"162","XZ":"生机镇","CUN":"镇江村","XXWZ":"镇江村李家坡","ZHLX":"滑坡",
     * "GMMS":"16(万m3)","DZWDX":"不稳定"
     * "TPLJ":"upLoadFile/dzzh/pic/P6090012.JPG","SPLJ":" "
     * "JCR":"吴文高","JCDH":"15086319600"
     *
     * @param v
     */

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        switch(viewId){
            case R.id.queryBtn:
                if(!isNetwork){
                    ToastUtil.show(this,"请检查网络连接");
                    return;
                }
                if(DZZH_list!=null && DZZH_list.size()>0){
                        DZZH_list.clear();
                }
                xz = spinnerxzh.getSelectedItem().toString().substring(0,spinnerxzh.getSelectedItem().toString().indexOf("["));

                GetDZDataSync getDZDataSync=new GetDZDataSync();
                try {
                    String dataRslt=getDZDataSync.execute().get(200, TimeUnit.SECONDS);
                    if(dataRslt!=null && dataRslt.contains("")){
                        JSONArray jsonArray = new JSONArray(dataRslt);
                        for(int k=0;k<jsonArray.length();k++){
                            JSONObject o = (JSONObject) jsonArray.get(k);
                            mDZZHEntity=new DZZHEntity();
                            if(o.has("x") && o.get("x")!=null){
                                mDZZHEntity.setX(o.get("x").toString());
                            }
                            if(o.has("y") && o.get("y")!=null){
                                mDZZHEntity.setY(o.get("y").toString());
                            }
                            if(o.has("jd")  && o.get("jd")!=null ){
                                mDZZHEntity.setJd(o.get("jd").toString());
                            }
                            if(o.has("wd")  && o.get("wd")!=null ){
                                mDZZHEntity.setWd(o.get("wd").toString());
                            }
                            if(o.has("DZPTBH") && o.get("DZPTBH")!=null){
                                mDZZHEntity.setDZPTBH(o.get("DZPTBH").toString());
                            }
                            if(o.has("XQ") && o.get("XQ")!=null){
                                mDZZHEntity.setXQ(o.get("XQ").toString());
                            }
                            if(o.has("XZH") && o.get("XZH")!=null){
                                mDZZHEntity.setXZH(o.get("XZH").toString());
                            }
                            if(o.has("CUN") && o.get("CUN")!=null){
                                mDZZHEntity.setCUN(o.get("CUN").toString());
                            }
                            if(o.has("ZU") && o.get("ZU")!=null){
                                mDZZHEntity.setZU(o.get("ZU").toString());
                            }
                            if(o.has("DNAME") && o.get("DNAME")!=null){
                                mDZZHEntity.setDNAME(o.get("DNAME").toString());
                            }
                            if(o.has("DZTYPE") && o.get("DZTYPE")!=null){
                                mDZZHEntity.setDZTYPE(o.get("DZTYPE").toString());
                            }
                            if(o.has("GM") && o.get("GM")!=null){
                                mDZZHEntity.setGM(o.get("GM").toString());
                            }
                            if(o.has("GMDJ") && o.get("GMDJ")!=null){
                                mDZZHEntity.setGMDJ(o.get("GMDJ").toString());
                            }
                            if(o.has("WXDX") && o.get("WXDX")!=null){
                                mDZZHEntity.setWXDX(o.get("WXDX").toString());
                            }

                            if(o.has("WXHS") && o.get("WXHS")!=null){
                                mDZZHEntity.setWXHS(o.get("WXHS").toString());
                            }
                            if(o.has("WXRK") && o.get("WXRK")!=null){
                                mDZZHEntity.setWXRK(o.get("WXRK").toString());
                            }
                            if(o.has("QZJJSS") && o.get("QZJJSS")!=null){
                                mDZZHEntity.setQZJJSS(o.get("QZJJSS").toString());
                            }
                            if(o.has("XQDJ") && o.get("XQDJ")!=null){
                                mDZZHEntity.setXQDJ(o.get("XQDJ").toString());
                            }
                            if(o.has("CSFSSJ") && o.get("CSFSSJ")!=null){
                                mDZZHEntity.setCSFSSJ(o.get("CSFSSJ").toString());
                            }
                            if(o.has("YXYS") && o.get("YXYS")!=null){
                                mDZZHEntity.setYXYS(o.get("YXYS").toString());
                            }
                            if(o.has("FZZRNAME") && o.get("FZZRNAME")!=null){
                                mDZZHEntity.setFZZRNAME(o.get("FZZRNAME").toString());
                            }
                            if(o.has("FZZRTEL") && o.get("FZZRTEL")!=null){
                                mDZZHEntity.setFZZRTEL(o.get("FZZRTEL").toString());
                            }

                            if(o.has("JCZRNAME") && o.get("JCZRNAME")!=null){
                                mDZZHEntity.setJCZRNAME(o.get("JCZRNAME").toString());
                            }
                            if(o.has("JCZRTEL") && o.get("JCZRTEL")!=null){
                                mDZZHEntity.setJCZRTEL(o.get("JCZRTEL").toString());
                            }
                            if(o.has("DJRKYEAR") && o.get("DJRKYEAR")!=null){
                                mDZZHEntity.setDJRKYEAR(o.get("DJRKYEAR").toString());
                            }
                            if(o.has("NCCS") && o.get("NCCS")!=null){
                                mDZZHEntity.setNCCS(o.get("NCCS").toString());
                            }
                            if(o.has("BZ") && o.get("BZ")!=null){
                                mDZZHEntity.setBZ(o.get("BZ").toString());
                            }
                            if(o.has("picture")&&o.get("picture")!=null){
                                mDZZHEntity.setPicture(o.get("picture").toString());
                            }
                            DZZH_list.add(mDZZHEntity);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                    ToastUtil.show(DZZHQueryActivity.this,"连接服务器超时");
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.show(DZZHQueryActivity.this,"解析JSON错误");
                }

                if(DZZH_list!=null && DZZH_list.size()>0){
                    MyApp.setDZZH_list(null);
                    MyApp.setDZZH_list(DZZH_list);
                    Intent toListShowIntent=new Intent();
                    toListShowIntent.setClass(this,DZZHListShowActivity.class);
                    startActivity(toListShowIntent);
                }else{
                    ToastUtil.show(this,"没有该灾害数据");
                }
                break;
            case R.id.backtextview:
                DZZHQueryActivity.this.finish();
                break;
            default:
                break;
        }
    }

    public class GetDZDataSync extends AsyncTask<String, Integer, String> {

        public GetDZDataSync() {
            ksoap=new KsoapValidateHttp(DZZHQueryActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetTB_FIELDSINDEX(xz);
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            DZZHQueryActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 日期选择器
     */
    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Log.d("OnDateSet", "select year:" + year + ";month:" + month + ";day:" + day);

            String m="";
            String d="";
            if(month<10){
                m="0"+(month+1)+"";
            }else{
                m=month+"";
            }

            if(day<10){
                d="0"+day+"";
            }else{
                d=day+"";
            }

            //editTextsj.setText(year+"-"+m+"-"+d);
        }
    }
}