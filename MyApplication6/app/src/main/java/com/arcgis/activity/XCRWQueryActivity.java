package com.arcgis.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.arcgis.dao.XCRWDao;
import com.arcgis.entity.XCRWEntity;
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

public class XCRWQueryActivity extends Activity implements View.OnClickListener{

    //任务类型
    Spinner spinnerrwlx=null;
    //任务状态
    Spinner spinnerrwzt= null;
    //用户名
    TextView TextViewYHM=null;
    //任务年份
    Spinner spinnerrwnf=null;
    //任务性质
    Spinner spinnerrwxz=null;

    private Button queryBtn=null;

    private TextView backtextview;
    private TextView titletextview;

    //任务类型
    ArrayAdapter<String> spinnerrwlxAdapter;
    //任务状态
    ArrayAdapter<String> spinnerrwztAdapter;
    //任务年份
    ArrayAdapter<String> spinnerrwnfAdapter;
    //任务性质
    ArrayAdapter<String> spinnerrwxzAdapter;

    private XCRWEntity mXCRWEntity;
    private List<XCRWEntity> XCRW_list=new ArrayList<XCRWEntity>();

    //全局变量存储位置
    private App MyApp;

    //调用webservice
    private KsoapValidateHttp ksoap;


    private boolean isNetwork=false;
    List<String> bpnf_List=new ArrayList<String>();
    List<String> rwzt_List=new ArrayList<String>();
    List<String> rwlx_List=new ArrayList<String>();

    SimpleDateFormat format=null;
    //用户名
    String yhm=null;
    private DZSYSDICEntityDao dzsysdicEntityDao=null;
    private XCRWDao xcrwDao=null;
    String objId=null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_queryxcrw);
        App.getInstance().addActivity(this);

        isNetwork= NetUtils.isNetworkAvailable(this);
        dzsysdicEntityDao=new DZSYSDICEntityDao(this);
        xcrwDao=new XCRWDao(this);

        MyApp=(App) this.getApplication();

        queryBtn= (Button) findViewById(R.id.queryBtn);
        queryBtn.setOnClickListener(this);

        backtextview=(TextView)findViewById(R.id.backtextview);
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.titletextview);
        titletextview.setText("巡查任务");


        TextViewYHM= (TextView) findViewById(R.id.TextViewYHM);
        spinnerrwlx=(Spinner)findViewById(R.id.spinnerrwlx);
        spinnerrwzt=(Spinner)findViewById(R.id.spinnerrwzt);
        spinnerrwnf=(Spinner)findViewById(R.id.spinnerrwnf);
        spinnerrwxz=(Spinner)findViewById(R.id.spinnerrwxz);

        SharedPreferences LOGIN_INFO = getSharedPreferences("LOGIN_INFO",MODE_PRIVATE);
        objId = LOGIN_INFO.getString("PID",null);
        if(LOGIN_INFO.getString("NAME",null)!=null){
            yhm=LOGIN_INFO.getString("NAME",null);
        }
        TextViewYHM.setText(yhm);

        if(isNetwork){
            DZSYSDICEntity dzsysdicEntity=new DZSYSDICEntity();
            GetRWZTDataSync getRWZTDataSync=new GetRWZTDataSync();
            try {
                String dataRslt=getRWZTDataSync.execute().get(20, TimeUnit.SECONDS);
                if(dataRslt!=null && dataRslt.contains("")){
                    JSONArray jsonArray = new JSONArray(dataRslt);
                    rwzt_List.clear();
                    for(int k=0;k<jsonArray.length();k++){
                        JSONObject o = (JSONObject) jsonArray.get(k);
                        mXCRWEntity=new XCRWEntity();
                        if(o.has("state") && o.get("state")!=null){
                            rwzt_List.add(o.get("state").toString());
                            dzsysdicEntity.setName(o.get("state").toString());
                            dzsysdicEntity.setType("XCRWSTATE");

                            if(!dzsysdicEntityDao.isEntityExist(o.get("state").toString())){
                                dzsysdicEntityDao.add(dzsysdicEntity);
                            }
                        }
                    }
                }else{
                    ToastUtil.show(XCRWQueryActivity.this,"服务器返回值为空");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                ToastUtil.show(XCRWQueryActivity.this,"连接服务器超时");
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtil.show(XCRWQueryActivity.this,"解析JSON错误");
            }

            GetRWLXDataSync getRWLXDataSync=new GetRWLXDataSync();
            try {
                String dataRslt=getRWLXDataSync.execute().get(25, TimeUnit.SECONDS);
                if(dataRslt!=null && dataRslt.contains("")){
                    rwlx_List.clear();
                    JSONArray jsonArray = new JSONArray(dataRslt);
                    for(int k=0;k<jsonArray.length();k++){
                        JSONObject o = (JSONObject) jsonArray.get(k);
                        if(o.has("type") && o.get("type")!=null){
                            rwlx_List.add(o.get("type").toString());
                            dzsysdicEntity.setName(o.get("type").toString());
                            dzsysdicEntity.setType("XCRWTYPE");

                            if(!dzsysdicEntityDao.isEntityExist(o.get("type").toString())){
                                dzsysdicEntityDao.add(dzsysdicEntity);
                            }
                        }
                    }
                }else{
                    ToastUtil.show(XCRWQueryActivity.this,"服务器返回值为空");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                ToastUtil.show(XCRWQueryActivity.this,"连接服务器超时");
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtil.show(XCRWQueryActivity.this,"解析JSON错误");
            }
        }else {
            //没有网络，从数据库中读取
            rwzt_List.clear();
            List<DZSYSDICEntity> XCRWSTATEList =dzsysdicEntityDao.queryByType("XCRWSTATE");
            for(DZSYSDICEntity dz:XCRWSTATEList){
                rwzt_List.add(dz.getName());
            }

            rwlx_List.clear();
            List<DZSYSDICEntity> XCRWTYPEList =dzsysdicEntityDao.queryByType("XCRWTYPE");
            for(DZSYSDICEntity dz:XCRWTYPEList){
                rwlx_List.add(dz.getName());
            }
        }


        //巡查任务类型
        if(rwlx_List!=null && rwlx_List.size()>0){
            spinnerrwlxAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, rwlx_List);
        }
        spinnerrwlxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if(spinnerrwlxAdapter!=null){
            spinnerrwlx.setAdapter(spinnerrwlxAdapter);
        }

        //任务状态
        if(rwzt_List!=null && rwzt_List.size()>0){
            spinnerrwztAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,rwzt_List);
        }
        spinnerrwztAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerrwzt.setAdapter(spinnerrwztAdapter);

        //任务性质
        List<String> RWXZ_list=new ArrayList<String>();
        RWXZ_list.add("我发送的任务");
        RWXZ_list.add("我执行的任务");
        spinnerrwxzAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,RWXZ_list);
        spinnerrwxzAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerrwxz.setAdapter(spinnerrwxzAdapter);

        format = new SimpleDateFormat("yyyy");
        String dataStr = format.format(new Date());
        int dataInt=Integer.parseInt(dataStr);
        bpnf_List.clear();
        for(int i=0;i<15;i++){
            bpnf_List.add((dataInt-i)+"");
        }
        spinnerrwnfAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, bpnf_List);
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

                XCRW_list.clear();

                //巡查年份
                String pznf=spinnerrwnf.getSelectedItem().toString();

                //任务性质
                String rwxz=spinnerrwxz.getSelectedItem().toString();
                if(rwxz.equals("我发送的任务")){
                    rwxz="0";
                }
                if(rwxz.equals("我执行的任务")){
                    rwxz="1";
                }
                //任务类型
                String rwlx=spinnerrwlx.getSelectedItem().toString();
                //任务状态
                String rwzt=spinnerrwzt.getSelectedItem().toString();
                /**
                 * [{"ID":21,"RWBH":"130676043871617756","SENDER_ID":"zj","RECEIVER_ID":"2323",
                 * "SENDTIME":"\/Date(1423065600000)\/","TASKTITLE":"werwe","TASKCONTENT":"erter",
                 * "X":35550397.04379700,"Y":3054022.68020308,"E":105.51047862,"N":27.59821317,"TASKAddress":"亮岩镇,飞轮村",
                 * "TASKFILES":"","RESULTCONTENT":"","RESULTFILES":"arcengine10.1开发总结.docx","STATE":"已完成",
                 * "COMPLETETIME":"2015/2/6 17:43:10","TASKTYPE":"灾害点巡查"}]
                 */

                if(isNetwork){

                    GetXCRWDataSync getXCRWDataSync=new GetXCRWDataSync(objId,rwxz,rwlx,rwzt,pznf);
                    try {
                        String dataRslt=getXCRWDataSync.execute().get(20, TimeUnit.SECONDS);
                        if(dataRslt!=null && dataRslt.contains("")){
                            JSONArray jsonArray = new JSONArray(dataRslt);
                            for(int k=0;k<jsonArray.length();k++){
                                JSONObject o = (JSONObject) jsonArray.get(k);
                                mXCRWEntity=new XCRWEntity();
                                if(o.has("SENDTIME") && o.get("SENDTIME")!=null){
                                    if(o.get("SENDTIME").toString().contains("Date")){
                                        int len=o.get("SENDTIME").toString().length();
                                        String d=o.get("SENDTIME").toString().substring(6,len-2);
                                        Date dat=new Date(Long.parseLong(d));
                                        GregorianCalendar gc = new GregorianCalendar();
                                        gc.setTime(dat);
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                        String sb=format.format(gc.getTime());
                                        mXCRWEntity.setSENDTIME(sb);
                                    }else{
                                        mXCRWEntity.setSENDTIME("无数据");
                                    }
                                }
                                if(o.has("TASKTYPE") && o.get("TASKTYPE")!=null){
                                    mXCRWEntity.setTASKTYPE(o.get("TASKTYPE").toString());
                                }
                                if(o.has("COMPLETETIME") && o.get("COMPLETETIME")!=null){
                                    mXCRWEntity.setCOMPLETETIME(o.get("COMPLETETIME").toString());
                                }
                                if(o.has("STATE") && o.get("STATE")!=null){
                                    mXCRWEntity.setSTATE(o.get("STATE").toString());
                                }
                                if(o.has("RESULTFILES") && o.get("RESULTFILES")!=null){
                                    mXCRWEntity.setRESULTFILES(o.get("RESULTFILES").toString());
                                }
                                if(o.has("RESULTCONTENT") && o.get("RESULTCONTENT")!=null){
                                    mXCRWEntity.setRESULTCONTENT(o.get("RESULTCONTENT").toString());
                                }
                                if(o.has("TASKFILES") && o.get("TASKFILES")!=null){
                                    mXCRWEntity.setTASKFILES(o.get("TASKFILES").toString());
                                }
                                if(o.has("TASKAddress") && o.get("TASKAddress")!=null){
                                    mXCRWEntity.setTASKAddress(o.get("TASKAddress").toString());
                                }
                                if(o.has("N") && o.get("N")!=null){
                                    mXCRWEntity.setN(o.get("N").toString());
                                }
                                if(o.has("E") && o.get("E")!=null){
                                    mXCRWEntity.setE(o.get("E").toString());
                                }
                                if(o.has("TASKCONTENT") && o.get("TASKCONTENT")!=null){
                                    mXCRWEntity.setTASKCONTENT(o.get("TASKCONTENT").toString());
                                }
                                if(o.has("TASKTITLE") && o.get("TASKTITLE")!=null){
                                    mXCRWEntity.setTASKTITLE(o.get("TASKTITLE").toString());
                                }
                                if(o.has("RECEIVER_ID") && o.get("RECEIVER_ID")!=null){
                                    mXCRWEntity.setRECEIVER_ID(o.get("RECEIVER_ID").toString());
                                }
                                if(o.has("SENDER_ID") && o.get("SENDER_ID")!=null){
                                    mXCRWEntity.setSENDER_ID(o.get("SENDER_ID").toString());
                                }
                                if(o.has("RWBH") && o.get("RWBH")!=null){
                                    mXCRWEntity.setRWBH(o.get("RWBH").toString());
                                }
                                if(o.has("ID") && o.get("ID")!=null){
                                    mXCRWEntity.setID(o.get("ID").toString());
                                }
                                if(o.has("X") && o.get("X")!=null){
                                    mXCRWEntity.setX(o.get("X").toString());
                                }
                                if(o.has("Y") && o.get("Y")!=null){
                                    mXCRWEntity.setY(o.get("Y").toString());
                                }

                                XCRW_list.add(mXCRWEntity);
                                if(!xcrwDao.isExistEntity(o.get("RWBH").toString().trim())){
                                    xcrwDao.add(mXCRWEntity);
                                }
                            }
                        }else{
                            ToastUtil.show(XCRWQueryActivity.this,"服务器返回值为空");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        ToastUtil.show(XCRWQueryActivity.this,"连接服务器超时");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtil.show(XCRWQueryActivity.this,"解析JSON错误");
                    }
                }else{
                    ToastUtil.show(XCRWQueryActivity.this,"网络异常，无法查询");
                }


                if(XCRW_list!=null && XCRW_list.size()>0){
                    MyApp.setXCRW_list(null);
                    MyApp.setXCRW_list(XCRW_list);
                    Intent toListShowIntent=new Intent();
                    toListShowIntent.setClass(this,XCRWListShowActivity.class);
                    startActivity(toListShowIntent);
                }else{
                    ToastUtil.show(this,"没有该巡查数据");
                }
                break;
            case R.id.backtextview:
                XCRWQueryActivity.this.finish();
                break;
            default:
                break;
        }
    }

    public class GetXCRWDataSync extends AsyncTask<String, Integer, String> {
        String pid;
        String rwxz;
        String rwlx;
        String zt;
        String rwrq;

        public GetXCRWDataSync(String pid,String rwxz,String rwlx,String zt,String rwrq) {
            ksoap=new KsoapValidateHttp(XCRWQueryActivity.this);
            this.pid=pid;
            this.rwxz=rwxz;
            this.rwlx=rwlx;
            this.zt=zt;
            this.rwrq=rwrq;
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetInspectionMission(pid,rwxz,rwlx,zt,rwrq);
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
            ksoap=new KsoapValidateHttp(XCRWQueryActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetTaskState();
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

    //获取任务类型
    public class GetRWLXDataSync extends AsyncTask<String, Integer, String> {


        public GetRWLXDataSync() {
            ksoap=new KsoapValidateHttp(XCRWQueryActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetTaskType();
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
            XCRWQueryActivity.this.finish();
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