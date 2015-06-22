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
import com.arcgis.entity.KCZYEntity;
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

public class KCZYQueryActivity extends Activity implements View.OnClickListener{

    //矿产名称
    EditText editTextkcmc=null;
    //矿产类型
    Spinner spinnerkclx= null;
    //所属镇
    Spinner spinnerssz=null;

    private Button queryBtn=null;

    private TextView backtextview;
    private TextView titletextview;

    ArrayAdapter<String> spinnerkclxAdapter;
    ArrayAdapter<String> spinnersszAdapter;
    //ArrayAdapter<String> spinnersscAdapter;

    private KCZYEntity mKCZYEntity;
    private List<KCZYEntity> KCZY_list=new ArrayList<KCZYEntity>();

    //全局变量存储位置
    private App MyApp;

    //调用webservice
    private KsoapValidateHttp ksoap;

    //矿产名称
    String kcmc=null;
    //所属镇
    String ssz=null;
    //所属村
   // String ssc=null;
    //矿产类型
    String kclx=null;

    private boolean isNetwork=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_querykczy);
        isNetwork= NetUtils.isNetworkAvailable(this);
        App.getInstance().addActivity(this);
        MyApp=(App) this.getApplication();

        queryBtn= (Button) findViewById(R.id.queryBtn);
        queryBtn.setOnClickListener(this);

        backtextview=(TextView)findViewById(R.id.backtextview);
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.titletextview);
        titletextview.setText("查询矿产资源");

        editTextkcmc= (EditText) this.findViewById(R.id.editTextkcmc);

        spinnerkclx= (Spinner) this.findViewById(R.id.spinnerkclx);
        spinnerssz= (Spinner) this.findViewById(R.id.spinnerssz);


        if(MyApp.getKclx_list()!=null && MyApp.getKclx_list().size()>0){
            spinnerkclxAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, MyApp.getKclx_list());
        }
        spinnerkclxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerkclx.setAdapter(spinnerkclxAdapter);

        if(MyApp.getXiangzhen_list()!=null && MyApp.getXiangzhen_list().size()>0){
            spinnersszAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, MyApp.getXiangzhen_list());
        }
        spinnersszAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerssz.setAdapter(spinnersszAdapter);

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
                if(KCZY_list!=null && KCZY_list.size()>0){
                    KCZY_list.clear();
                }
                kcmc=editTextkcmc.getText().toString();
                ssz=spinnerssz.getSelectedItem().toString();
//                if(ssz.equals("全部")){
//                    ssz="";
//                }
                kclx=spinnerkclx.getSelectedItem().toString();
//                if(kclx.equals("全部")){
//                    kclx="";
//                }
                GetDZDataSync getDZDataSync=new GetDZDataSync();
                try {
                    String dataRslt=getDZDataSync.execute().get(5000, TimeUnit.MILLISECONDS);
                    if(dataRslt!=null && dataRslt.contains("")){
                        JSONArray jsonArray = new JSONArray(dataRslt);
                        for(int k=0;k<jsonArray.length();k++){
                            JSONObject o = (JSONObject) jsonArray.get(k);
                            mKCZYEntity=new KCZYEntity();
                            if(o.has("X") && o.get("X")!=null){
                                mKCZYEntity.setPx(o.get("X").toString());
                            }
                            if(o.has("Y") && o.get("Y")!=null){
                                mKCZYEntity.setPy(o.get("Y").toString());
                            }
                            if(o.has("E")  && o.get("E")!=null ){
                                mKCZYEntity.setPe(o.get("E").toString());
                            }
                            if(o.has("N")  && o.get("N")!=null ){
                                mKCZYEntity.setPn(o.get("N").toString());
                            }
                            if(o.has("KSMC") && o.get("KSMC")!=null){
                                mKCZYEntity.setKcName(o.get("KSMC").toString());
                            }
                            if(o.has("KCLX") && o.get("KCLX")!=null){
                                mKCZYEntity.setKcType(o.get("KCLX").toString());
                            }
                            if(o.has("CKBH") && o.get("CKBH")!=null){
                                mKCZYEntity.setKcNo(o.get("CKBH").toString());
                            }
                            if(o.has("XZ") && o.get("XZ")!=null){
                                mKCZYEntity.setSsxz(o.get("XZ").toString());
                            }
                            if(o.has("CUN") && o.get("CUN")!=null){
                                mKCZYEntity.setSscun(o.get("CUN").toString());
                            }
                            if(o.has("TPLJ") && o.get("TPLJ")!=null){
                                mKCZYEntity.setPhotoPath(o.get("TPLJ").toString());
                            }
                            if(o.has("SPLJ") && o.get("SPLJ")!=null){
                                mKCZYEntity.setVideoPath(o.get("SPLJ").toString());
                            }
                            if(o.has("KSFZR") && o.get("KSFZR")!=null){
                                mKCZYEntity.setJianceren(o.get("KSFZR").toString());
                            }
                            if(o.has("LXDH") && o.get("LXDH")!=null){
                                mKCZYEntity.setPhone(o.get("LXDH").toString());
                            }
                            if(o.has("XXWZ") && o.get("XXWZ")!=null){
                                mKCZYEntity.setXxwz(o.get("XXWZ").toString());
                            }
                            if(o.has("KQMJ") && o.get("KQMJ")!=null){
                                mKCZYEntity.setKcmj(o.get("KQMJ").toString());
                            }
                            if(o.has("SFHF") && o.get("SFHF")!=null){
                                mKCZYEntity.setSFHF(o.get("SFHF").toString());
                            }
                            if(o.has("KCCL") && o.get("KCCL")!=null){
                                mKCZYEntity.setKccl(o.get("KCCL").toString());
                            }

                            if(o.has("ZCSJ") && o.get("ZCSJ")!=null){
                                if(o.get("ZCSJ").toString().contains("Date")){
                                    int len=o.get("ZCSJ").toString().length();
                                    String d=o.get("ZCSJ").toString().substring(6,len-2);
                                    Date dat=new Date(Long.parseLong(d));
                                    GregorianCalendar gc = new GregorianCalendar();
                                    gc.setTime(dat);
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    String sb=format.format(gc.getTime());
                                    mKCZYEntity.setAddtime(sb);
                                }else{
                                    mKCZYEntity.setAddtime("无数据");
                                }
                            }




                            KCZY_list.add(mKCZYEntity);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                    ToastUtil.show(KCZYQueryActivity.this,"连接服务器超时");
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.show(KCZYQueryActivity.this,"解析JSON错误");
                }

                if(KCZY_list!=null && KCZY_list.size()>0){
                    MyApp.setKCZY_list(null);
                    MyApp.setKCZY_list(KCZY_list);
                    Intent toListShowIntent=new Intent();
                    toListShowIntent.setClass(this,KCZYListShowActivity.class);
                    startActivity(toListShowIntent);
                }else{
                    ToastUtil.show(this,"没有该矿产数据");
                }
                break;
            case R.id.backtextview:
                KCZYQueryActivity.this.finish();
                break;
            default:
                break;
        }
    }

    public class GetDZDataSync extends AsyncTask<String, Integer, String> {

        public GetDZDataSync() {
            ksoap=new KsoapValidateHttp(KCZYQueryActivity.this);
        }


        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetMinieral(kcmc,kclx,ssz);
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
            KCZYQueryActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        String dataStr = format.format(new Date());
//        editTextsj.setText(dataStr);

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