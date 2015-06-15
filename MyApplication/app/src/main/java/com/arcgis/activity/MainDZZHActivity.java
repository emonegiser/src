package com.arcgis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.adapter.DZListAdapter;
import com.arcgis.drawtool.DrawTool;
import com.arcgis.entity.DZZHEntity;
import com.arcgis.entity.XCRWEntity;
import com.arcgis.gpsservice.Gps;
import com.arcgis.httputil.App;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;
import com.esri.core.geometry.Point;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class MainDZZHActivity extends Activity implements View.OnClickListener {

    private Spinner xzxzSpin;
    private  Spinner xzrwSpin;
    private   TextView backtextview;
    private TextView addzhdtextview;
    private  Button mapBtn;
    private ListView showList=null;
    private DZListAdapter dzListAdapter=null;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> xzxzSpinAdapter;

    //调用webservice
    private KsoapValidateHttp ksoap;
    //全局变量存储位置
    private App MyApp;
    List<String> cx = new ArrayList<>();
    private List<DZZHEntity> DZZH_list=new ArrayList<DZZHEntity>();
    private XCRWEntity mXCRWEntity;
    private List<XCRWEntity> XCRW_list=new ArrayList<XCRWEntity>();
    private DZZHEntity mDZZHEntity;
    private boolean isNetwork=false;
    String xz=null;
    String szxz=null;
    int i=0;
    private String Pid=null;
    View header;//顶部


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_maindzzh);
        App.getInstance().addActivity(this);
        MyApp=(App) this.getApplication();
        LayoutInflater inflater = LayoutInflater.from(this);
        isNetwork= NetUtils.isNetworkAvailable(this);

        Gps.Type=Gps.ZHDXC;
        SharedPreferences LOGIN_INFO = getSharedPreferences("LOGIN_INFO",MODE_PRIVATE);
        Pid = LOGIN_INFO.getString("PID",null);

        addzhdtextview= (TextView) this.findViewById(R.id.addzhd);
        addzhdtextview.setOnClickListener(this);
        backtextview=(TextView)this.findViewById(R.id.backtextview);
        backtextview.setOnClickListener(this);
        mapBtn=(Button)this.findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(this);

        if (MyApp.getmGPSLon() != null && !MyApp.getmGPSLon().equals("") && MyApp.getmGPSLat() != null && !MyApp.getmGPSLat().equals("")) {
            Point pt = new Point();
            pt.setXY(Double.valueOf(MyApp.getmGPSLon()), Double.valueOf(MyApp.getmGPSLat()));
            //定位所在乡镇
            GetXZDataSync getXZDataSync = new GetXZDataSync(pt.getX() + "", pt.getY() + "");
            try {
                String dwRslt = getXZDataSync.execute().get(15, TimeUnit.SECONDS);
                if (dwRslt != null && dwRslt.length() > 0) {
//                    String [] result=dwRslt.split(";");
                    szxz=dwRslt;
                }
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (ExecutionException e1) {
                e1.printStackTrace();
            } catch (TimeoutException e1) {
                e1.printStackTrace();
            }
        }else{
        ToastUtil.show(MainDZZHActivity.this, "无法定位当前坐标!");}

        cx=MyApp.getCx_list();
        if(szxz!=null) {
            for (; i < cx.size(); i++) {
                if (cx.get(i).substring(0, cx.get(i).indexOf("[")).equals(szxz)) {
                    break;
                }
            }
        }
        //乡镇选择
        xzxzSpin= (Spinner) this.findViewById(R.id.xzxzSpin);
        xzxzSpinAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,MyApp.getCx_list());
        xzxzSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        xzxzSpin.setAdapter(xzxzSpinAdapter);
        xzxzSpin.setSelection(i);
        xzxzSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String str = parent.getItemAtPosition(position).toString();
                if(!isNetwork){
                    ToastUtil.show(MainDZZHActivity.this, "请检查网络连接");
                    return;
                }
                if(DZZH_list!=null && DZZH_list.size()>0){
                    DZZH_list.clear();
                }
                xz = xzxzSpin.getSelectedItem().toString().substring(0,xzxzSpin.getSelectedItem().toString().indexOf("["));

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
                            DZZHGettimeandcs dzzhGettimeandcs=new DZZHGettimeandcs(o.get("DZPTBH").toString());
                            try{
                            String Timecs=dzzhGettimeandcs.execute().get(200, TimeUnit.SECONDS);
                            if(Timecs!=null && Timecs.contains("")) {
                                String[] result = Timecs.split(";");
                                mDZZHEntity.setCs(result[1]);
                                mDZZHEntity.setWhtime(result[0]);
                            }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (TimeoutException e) {
                                e.printStackTrace();
                                ToastUtil.show(MainDZZHActivity.this,"连接服务器超时");
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
                    ToastUtil.show(MainDZZHActivity.this,"连接服务器超时");
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.show(MainDZZHActivity.this,"解析JSON错误");
                }

                if(DZZH_list!=null && DZZH_list.size()>0){
                    MyApp.setDZZH_list(null);
                    MyApp.setDZZH_list(DZZH_list);
//                    Intent toListShowIntent=new Intent();
//                    toListShowIntent.setClass(this,DZZHListShowActivity.class);
//                    startActivity(toListShowIntent);
                    if(MyApp.getDZZH_list()!=null && MyApp.getDZZH_list().size()>0){
                        DZZH_list=MyApp.getDZZH_list();
                        showListView(DZZH_list);
                    }
                }else{
                    ToastUtil.show(MainDZZHActivity.this,"没有该灾害数据");
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //任务选择列表
        xzrwSpin = (Spinner) findViewById(R.id.xzrwSpin);
        ArrayList<String> zhlx=new ArrayList<String>();
        zhlx.add("执行下发任务");
        zhlx.add("普通任务");
        zhlx.add("预警任务");
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,zhlx);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        xzrwSpin.setAdapter(adapter);
        xzrwSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = parent.getItemAtPosition(position).toString();
                if(str.equals("普通任务")){
                    isNetwork= NetUtils.isNetworkAvailable(MainDZZHActivity.this);
                    if(!isNetwork){
                        ToastUtil.show(MainDZZHActivity.this,"请检查网络连接");
                        return;
                    }
                    XCRW_list.clear();
                    /**
                     * [{"ID":21,"RWBH":"130676043871617756","SENDER_ID":"zj","RECEIVER_ID":"2323",
                        * "SENDTIME":"\/Date(1423065600000)\/","TASKTITLE":"werwe","TASKCONTENT":"erter",
                        * "X":35550397.04379700,"Y":3054022.68020308,"E":105.51047862,"N":27.59821317,"TASKAddress":"亮岩镇,飞轮村",
                        * "TASKFILES":"","RESULTCONTENT":"","RESULTFILES":"arcengine10.1开发总结.docx","STATE":"已完成",
                        * "COMPLETETIME":"2015/2/6 17:43:10","TASKTYPE":"灾害点巡查"}]
                    */
                    if(isNetwork){

                        GetXCRWDataSync getXCRWDataSync=new GetXCRWDataSync(Pid);
                        try {
                            String dataRslt=getXCRWDataSync.execute().get(20, TimeUnit.SECONDS);
                            if(dataRslt!=null && dataRslt.contains("")){
                                JSONArray jsonArray = new JSONArray(dataRslt);
                                for(int k=0;k<jsonArray.length();k++){
                                    JSONObject o = (JSONObject) jsonArray.get(k);
                                    mXCRWEntity=new XCRWEntity();
                                    if(o.has("SENDTIME") && o.get("SENDTIME")!=null){
                                        mXCRWEntity.setSENDTIME(o.get("SENDTIME").toString());
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

                                }
                            }else{
                                ToastUtil.show(MainDZZHActivity.this,"服务器返回值为空");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                            ToastUtil.show(MainDZZHActivity.this,"连接服务器超时");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.show(MainDZZHActivity.this,"解析JSON错误");
                        }
                    }else{
                        ToastUtil.show(MainDZZHActivity.this,"网络异常，无法查询");
                    }

                    if(XCRW_list!=null && XCRW_list.size()>0){
                        MyApp.setXCRW_list(null);
                        MyApp.setXCRW_list(XCRW_list);
                        Intent toListShowIntent=new Intent();
                        toListShowIntent.setClass(MainDZZHActivity.this,DZZHRWListActivity.class);
                        startActivity(toListShowIntent);
                    }else{
                        ToastUtil.show(MainDZZHActivity.this,"没有该巡查数据");
                    }
                }else if (str.equals("预警任务")){
                    Intent toListShowIntent=new Intent();
                    toListShowIntent.setClass(MainDZZHActivity.this,DZZHYJActivity.class);
                    startActivity(toListShowIntent);
                }else{

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        showList= (ListView) findViewById(R.id.showList);
        header=inflater.inflate(R.layout.header_maindzzh, null);
        showList.addHeaderView(header);
        showList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    return;
                }
                DZZHEntity dzzhEntity=DZZH_list.get(position-1);
                if(dzzhEntity!=null){
                    String px=dzzhEntity.getX();
                    String py=dzzhEntity.getY();
                    Intent toMap1Intent=new Intent();
                    toMap1Intent.setClass(MainDZZHActivity.this,DZZHinfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("DZZH",dzzhEntity);
                    toMap1Intent.putExtras(bundle);
                    startActivity(toMap1Intent);
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        switch(viewId){
            case R.id.mapBtn:
                Intent map1Intent=new Intent();
                map1Intent.setClass(MainDZZHActivity.this, MainMap1Activity.class);
                startActivity(map1Intent);
                break;
            case R.id.backtextview:
                MainDZZHActivity.this.finish();
                break;
            case R.id.addzhd:
                new AlertDialog.Builder(MainDZZHActivity.this).setTitle("是否以当前坐标作为灾害点位置?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (MyApp.getmGPSLon() != null && !MyApp.getmGPSLon().equals("") && MyApp.getmGPSLat() != null && !MyApp.getmGPSLat().equals("")) {
                                    Point pt = new Point();
                                    pt.setXY(Double.valueOf(MyApp.getmGPSLon()), Double.valueOf(MyApp.getmGPSLat()));
                                    //地理坐标转西安坐标
                                    // Point pt = (Point) GeometryEngine.project(point, SpatialReference.create(4326),SpatialReference.create(2359));
                                    //定位所在乡镇
                                    GetXZDataSync getXZDataSync = new GetXZDataSync(pt.getX() + "", pt.getY() + "");
                                    try {
                                        String dwRslt = getXZDataSync.execute().get(15, TimeUnit.SECONDS);
                                        if (dwRslt != null && dwRslt.length() > 0) {
                                            Intent AddPointIntent = new Intent(MainDZZHActivity.this, DZZHAddActivity.class);
//                                            String [] result=dwRslt.split(";");
                                            AddPointIntent.putExtra("PX", pt.getX());
                                            AddPointIntent.putExtra("PY", pt.getY());
                                            AddPointIntent.putExtra("XZ", dwRslt);
                                            startActivity(AddPointIntent);
                                        } else {
                                            ToastUtil.show(MainDZZHActivity.this, "服务器返回为空,无法获取该点所属乡镇");
                                            Intent AddPointIntent = new Intent(MainDZZHActivity.this, DZZHAddActivity.class);
                                            AddPointIntent.putExtra("PX", pt.getX());
                                            AddPointIntent.putExtra("PY", pt.getY());
                                            startActivity(AddPointIntent);
                                        }
                                    } catch (InterruptedException e1) {
                                        e1.printStackTrace();
                                    } catch (ExecutionException e1) {
                                        e1.printStackTrace();
                                    } catch (TimeoutException e1) {
                                        e1.printStackTrace();
                                    }
                                }else{
                                ToastUtil.show(MainDZZHActivity.this, "无法定位当前坐标!");}
                            }
                        }).setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ToastUtil.show(MainDZZHActivity.this, "请打开地图，并绘制一个点！");
                    }
                }).show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            MainDZZHActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void showListView(List<DZZHEntity> DZZH_list) {
        if (dzListAdapter == null) {
            showList = (ListView) findViewById(R.id.showList);
            dzListAdapter = new DZListAdapter(DZZH_list,this);
            showList.setAdapter(dzListAdapter);
        } else {
            dzListAdapter.onDateChange(DZZH_list);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        xzrwSpin.setSelection(0);
//        if(MyApp.getDZZH_list()!=null && MyApp.getDZZH_list().size()>0){
//            this.DZZH_list=MyApp.getDZZH_list();
//            showListView(DZZH_list);
//        }
    }
    //


    //
    //通过乡镇查灾害点
    public class GetDZDataSync extends AsyncTask<String, Integer, String> {

        public GetDZDataSync() {
            ksoap=new KsoapValidateHttp(MainDZZHActivity.this);
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
    //定位所在乡镇
    public class GetXZDataSync extends AsyncTask<String, Integer, String> {

        String px=null;
        String py=null;


        public GetXZDataSync(String px, String py) {
            ksoap=new KsoapValidateHttp(MainDZZHActivity.this);
            this.px = px;
            this.py = py;
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String posRslt=ksoap.WebGetXZByXY(this.px,this.py);
                if(posRslt!=null){
                    return posRslt;
                }else{
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    //获取移动端需执行的任务
    public class GetXCRWDataSync extends AsyncTask<String, Integer, String> {
        String pid;
//        String rwxz;
//        String rwlx;
//        String zt;
//        String rwrq;

        public GetXCRWDataSync(String pid) {
            ksoap=new KsoapValidateHttp(MainDZZHActivity.this );
            this.pid=pid;
//            this.rwxz=rwxz;
//            this.rwlx=rwlx;
//            this.zt=zt;
//            this.rwrq=rwrq;
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetInspectionMission2(pid);
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
    //获取时间和巡查次数
    public class DZZHGettimeandcs extends AsyncTask<String, Integer, String> {
        String dzptbh;
//        String rwxz;DZPTBH


        public DZZHGettimeandcs(String dzptbh) {
            ksoap=new KsoapValidateHttp(MainDZZHActivity.this );
            this.dzptbh=dzptbh;

        }
        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebDZZHGettimeandcs(dzptbh);
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