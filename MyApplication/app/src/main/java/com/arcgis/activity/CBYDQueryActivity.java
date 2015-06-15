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
import com.arcgis.dao.CBYDDao;
import com.arcgis.entity.CBYDEntity;
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

public class CBYDQueryActivity extends Activity implements View.OnClickListener{

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
    //所属村l
    ArrayAdapter<String> spinnersscAdapter;
    //报批年份
    ArrayAdapter<String> spinnerbpnfAdapter;

    private CBYDEntity mCBYDEntity;
    private List<CBYDEntity> CBYD_list=new ArrayList<>();

    //全局变量存储位置
    private App MyApp;

    //调用webservice
    private KsoapValidateHttp ksoap;

    ///批准年份 pznf,tdzsmc,dz
    String pznf=null;
    //地址
    String dz=null;
    //土地征收名称
    String tdzsmc=null;

    private boolean isNetwork=false;
    List<String> bpnf_List=new ArrayList<>();
    SimpleDateFormat format=null;

    private CBYDDao cbydDao=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_querycbyd);

        App.getInstance().addActivity(this);

        cbydDao=new CBYDDao(this);

        MyApp=(App) this.getApplication();

        queryBtn= (Button) findViewById(R.id.queryBtn);
        queryBtn.setOnClickListener(this);

        backtextview=(TextView)findViewById(R.id.backtextview);
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.titletextview);
        titletextview.setText("查询储备用地");


        spinnerbpnf=(Spinner)findViewById(R.id.spinnerbpnf);
        spinnerssc=(Spinner)findViewById(R.id.spinnerssc);
        spinnertdbpmc=(Spinner)findViewById(R.id.spinnertdbpmc);

        //获取储备用地村信息
        if(MyApp.getCBYDCUN_List()!=null && MyApp.getCBYDCUN_List().size()>0){
            spinnersscAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, MyApp.getCBYDCUN_List());
        }
        spinnersscAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if(spinnersscAdapter!=null){
            spinnerssc.setAdapter(spinnersscAdapter);
        }


        //获取报批批次
        if(MyApp.getSBYD_BPPC_list()!=null && MyApp.getSBYD_BPPC_list().size()>0){
            spinnertdbpmcAdapter= new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,MyApp.getSBYD_BPPC_list());
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
        spinnerbpnfAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, bpnf_List);
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
                isNetwork= NetUtils.isNetworkAvailable(this);

                if(CBYD_list!=null && CBYD_list.size()>0){
                    CBYD_list.clear();
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

                if(isNetwork){
                    GetDZDataSync getDZDataSync=new GetDZDataSync(pznf,tdzsmc,dz);
                    try {
                        String dataRslt=getDZDataSync.execute().get(20, TimeUnit.SECONDS);
                        if(dataRslt!=null && dataRslt.contains("")){
                            JSONArray jsonArray = new JSONArray(dataRslt);
                            for(int k=0;k<jsonArray.length();k++){
                                JSONObject o = (JSONObject) jsonArray.get(k);
                                mCBYDEntity=new CBYDEntity();
                                if(o.has("X") && o.get("X")!=null){
                                    mCBYDEntity.setX(o.get("X").toString());
                                }
                                if(o.has("Y") && o.get("Y")!=null){
                                    mCBYDEntity.setY(o.get("Y").toString());
                                }
                                if(o.has("OBJECTID")  && o.get("OBJECTID")!=null ){
                                    mCBYDEntity.setOBJECTID(o.get("OBJECTID").toString());
                                }
                                if(o.has("OBJECTID_1")  && o.get("OBJECTID_1")!=null ){
                                    mCBYDEntity.setOBJECTID_1(o.get("OBJECTID_1").toString());
                                }
                                if(o.has("BH")  && o.get("BH")!=null ){
                                    mCBYDEntity.setBH(o.get("BH").toString());
                                }
                                if(o.has("DKZL") && o.get("DKZL")!=null){
                                    mCBYDEntity.setDKZL(o.get("DKZL").toString());
                                }
                                if(o.has("DKMJ") && o.get("DKMJ")!=null){
                                    mCBYDEntity.setDKMJ(o.get("DKMJ").toString());
                                }
                                if(o.has("ZDBCFY") && o.get("ZDBCFY")!=null){
                                    mCBYDEntity.setZDBCFY(o.get("ZDBCFY").toString());
                                }
                                if(o.has("DSFZWBCFY") && o.get("DSFZWBCFY")!=null){
                                    mCBYDEntity.setDSFZWBCFY(o.get("DSFZWBCFY").toString());
                                }
                                if(o.has("FFID") && o.get("FFID")!=null){
                                    mCBYDEntity.setFFID(o.get("FFID").toString());
                                }
                                if(o.has("PZWH") && o.get("PZWH")!=null){
                                    mCBYDEntity.setPZWH(o.get("PZWH").toString());
                                }
                                if(o.has("TDZSWH") && o.get("TDZSWH")!=null){
                                    mCBYDEntity.setTDZSWH(o.get("TDZSWH").toString());
                                }
                                if(o.has("TDZSMC") && o.get("TDZSMC")!=null){
                                    mCBYDEntity.setTDZSMC(o.get("TDZSMC").toString());
                                }
                                if(o.has("CUN") && o.get("CUN")!=null){
                                    mCBYDEntity.setCUN(o.get("CUN").toString());
                                }
                                if(o.has("XZ") && o.get("XZ")!=null){
                                    mCBYDEntity.setXZ(o.get("XZ").toString());
                                }
                                if(o.has("DTTF") && o.get("DTTF")!=null){
                                    mCBYDEntity.setDTTF(o.get("DTTF").toString());
                                }
                                if(o.has("Shape") && o.get("Shape")!=null){
                                    mCBYDEntity.setShape(o.get("Shape").toString());
                                }
                                if(o.has("BPID") && o.get("BPID")!=null){
                                    mCBYDEntity.setBPID(o.get("BPID").toString());
                                }
                                if(o.has("BPMJ") && o.get("BPMJ")!=null){
                                    mCBYDEntity.setBPMJ(o.get("BPMJ").toString());
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
                                        mCBYDEntity.setPZSJ(sb);
                                    }else{
                                        mCBYDEntity.setPZSJ("无数据");
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
                                        mCBYDEntity.setLGGYDJSJ(sb);
                                    }else{
                                        mCBYDEntity.setLGGYDJSJ("无数据");
                                    }
                                }
                                CBYD_list.add(mCBYDEntity);
                                if(!cbydDao.isExistEntity(o.get("BH").toString().trim())){
                                    cbydDao.add(mCBYDEntity);
                                }
                            }
                        }else{
                            ToastUtil.show(CBYDQueryActivity.this,"服务器返回值为空");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        ToastUtil.show(CBYDQueryActivity.this,"连接服务器超时");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtil.show(CBYDQueryActivity.this,"解析JSON错误");
                    }
                }else{
                    ToastUtil.show(CBYDQueryActivity.this,"网络异常，无法查询");
                }


                if(CBYD_list!=null && CBYD_list.size()>0){
                    MyApp.setCBYD_QUERY_list(null);
                    MyApp.setCBYD_QUERY_list(CBYD_list);
                    Intent toListShowIntent=new Intent();
                    toListShowIntent.setClass(this,CBYDListShowActivity.class);
                    startActivity(toListShowIntent);
                }else{
                    ToastUtil.show(this,"没有该用地数据");
                }
                break;
            case R.id.backtextview:
                CBYDQueryActivity.this.finish();
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
            ksoap=new KsoapValidateHttp(CBYDQueryActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetLandReserve(pznf, tdzsmc, dz);
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
            CBYDQueryActivity.this.finish();
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