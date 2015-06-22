package com.arcgis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.badgeview.BadgeView;
import com.arcgis.entity.XCRWEntity;
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

public class SettingActivity extends Activity implements View.OnClickListener{

    private TextView xtdwTextView;
//    private TextView dwcrwTextView;
//    private TextView ywcrwTextView;
    private TextView titletextview;
    private TextView backtextview;
    private Button quitBtn;

//    private BadgeView DWCRWbadge;
//    private BadgeView YWCRWbadge;

    //调用webservice
    private KsoapValidateHttp ksoap;
    private App MyApp;

    SimpleDateFormat formatter =null;
    private String pid=null;
    private XCRWEntity mXCRWEntity;
    private List<XCRWEntity> NEWXCRW_list=new ArrayList<>();
    private List<XCRWEntity> YWCXCRW_list=new ArrayList<>();

    private boolean isNetwork=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);

        xtdwTextView= (TextView) findViewById(R.id.xtdwTextView);
//        dwcrwTextView= (TextView) findViewById(R.id.dwcrwTextView);
//        ywcrwTextView= (TextView) findViewById(R.id.ywcrwTextView);
        titletextview= (TextView) findViewById(R.id.titletextview);
        backtextview= (TextView) findViewById(R.id.backtextview);
        quitBtn= (Button) findViewById(R.id.quitBtn);

        App.getInstance().addActivity(this);

        MyApp=(App) this.getApplication();
        formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        titletextview.setText("系统设置");

//        DWCRWbadge=new BadgeView(this,dwcrwTextView);
//        DWCRWbadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
//
//        YWCRWbadge=new BadgeView(this,ywcrwTextView);
//        YWCRWbadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);

        xtdwTextView.setOnClickListener(this);
//        dwcrwTextView.setOnClickListener(this);
//        ywcrwTextView.setOnClickListener(this);
        quitBtn.setOnClickListener(this);
        backtextview.setOnClickListener(this);

        SharedPreferences LOGIN_INFO = getSharedPreferences("LOGIN_INFO",MODE_PRIVATE);
        if(LOGIN_INFO.getString("PID",null)!=null){
            pid=LOGIN_INFO.getString("PID",null);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        switch(viewId){
            case R.id.xtdwTextView:
                //跳转到GPS位置上报页
                Intent intent=new Intent(this,XunChaSBActivity.class);
                startActivity(intent);
                //this.finish();
                break;
//            case R.id.dwcrwTextView:
//                //未完成
//                Intent WWCintent=new Intent(this,XCRWListShowActivity.class);
//                WWCintent.putExtra("FLAG","NO");
//                startActivity(WWCintent);
//                //this.finish();
//                break;
//            case R.id.ywcrwTextView:
//                //已完成
//                Intent YWCintent=new Intent(this,XCRWListShowActivity.class);
//                YWCintent.putExtra("FLAG","YES");
//                startActivity(YWCintent);
//                //this.finish();
//                break;
            case R.id.quitBtn:
                //退出系统
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("确认退出?");
                builder.setCancelable(false);
                builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        CommitStateSync commitStateSync=new CommitStateSync(pid);
                        try {
                            String rslt=commitStateSync.execute().get(20,TimeUnit.SECONDS);
                            ToastUtil.show(SettingActivity.this,rslt);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                        App.getInstance().exit();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

                break;
            case R.id.backtextview:
                SettingActivity.this.finish();
                break;
            default:
                break;
        }
    }

    public class CommitStateSync extends AsyncTask<String, Integer, String> {
        String pid;


        public CommitStateSync(String pid) {
            ksoap=new KsoapValidateHttp(SettingActivity.this);
            this.pid=pid;

        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebUserExitState(pid);
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

    public class GetNEWXCRWDataSync extends AsyncTask<String, Integer, String> {
        String pid;
        String rwxz;
        String rwlx;
        String zt;
        String rwrq;

        public GetNEWXCRWDataSync(String pid,String rwxz,String rwlx,String zt,String rwrq) {
            ksoap=new KsoapValidateHttp(SettingActivity.this);
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

    public class GetCOMPLETEXCRWDataSync extends AsyncTask<String, Integer, String> {
        String pid;
        String rwxz;
        String rwlx;
        String zt;
        String rwrq;

        public GetCOMPLETEXCRWDataSync(String pid,String rwxz,String rwlx,String zt,String rwrq) {
            ksoap=new KsoapValidateHttp(SettingActivity.this);
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

    @Override
    protected void onResume() {
        super.onResume();
        isNetwork= NetUtils.isNetworkAvailable(this);
        //我执行的任务
        String rwxz="1";
        //全部任务类型
        String rwlx="";
        //任务状态
        String rwztXrw="新任务";
        String rwztYwcrw="已完成";
        //任务年份
        String pznf="2015";

//        if(isNetwork){
//            GetNEWXCRWDataSync getNEWXCRWDataSync=new GetNEWXCRWDataSync(pid,rwxz,rwlx,rwztXrw,pznf);
//            try {
//                String dataRslt=getNEWXCRWDataSync.execute().get(20, TimeUnit.SECONDS);
//                if(dataRslt!=null && dataRslt.contains("")){
//                    NEWXCRW_list.clear();
//                    JSONArray jsonArray = new JSONArray(dataRslt);
//
//                    for(int k=0;k<jsonArray.length();k++){
//
//                        JSONObject o = (JSONObject) jsonArray.get(k);
//                        mXCRWEntity=new XCRWEntity();
//
//                        if(o.has("SENDTIME") && o.get("SENDTIME")!=null){
//                            if(o.get("SENDTIME").toString().contains("Date")){
//                                int len=o.get("SENDTIME").toString().length();
//                                String d=o.get("SENDTIME").toString().substring(6,len-2);
//                                Date dat=new Date(Long.parseLong(d));
//                                GregorianCalendar gc = new GregorianCalendar();
//                                gc.setTime(dat);
//                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//                                String sb=format.format(gc.getTime());
//                                mXCRWEntity.setSENDTIME(sb);
//                            }else{
//                                mXCRWEntity.setSENDTIME("无数据");
//                            }
//                        }
//                        if(o.has("TASKTYPE") && o.get("TASKTYPE")!=null){
//                            mXCRWEntity.setTASKTYPE(o.get("TASKTYPE").toString());
//                        }
//                        if(o.has("COMPLETETIME") && o.get("COMPLETETIME")!=null){
//                            mXCRWEntity.setCOMPLETETIME(o.get("COMPLETETIME").toString());
//                        }
//                        if(o.has("STATE") && o.get("STATE")!=null){
//                            mXCRWEntity.setSTATE(o.get("STATE").toString());
//                        }
//                        if(o.has("RESULTFILES") && o.get("RESULTFILES")!=null){
//                            mXCRWEntity.setRESULTFILES(o.get("RESULTFILES").toString());
//                        }
//                        if(o.has("RESULTCONTENT") && o.get("RESULTCONTENT")!=null){
//                            mXCRWEntity.setRESULTCONTENT(o.get("RESULTCONTENT").toString());
//                        }
//                        if(o.has("TASKFILES") && o.get("TASKFILES")!=null){
//                            mXCRWEntity.setTASKFILES(o.get("TASKFILES").toString());
//                        }
//                        if(o.has("TASKAddress") && o.get("TASKAddress")!=null){
//                            mXCRWEntity.setTASKAddress(o.get("TASKAddress").toString());
//                        }
//                        if(o.has("N") && o.get("N")!=null){
//                            mXCRWEntity.setN(o.get("N").toString());
//                        }
//                        if(o.has("E") && o.get("E")!=null){
//                            mXCRWEntity.setE(o.get("E").toString());
//                        }
//                        if(o.has("TASKCONTENT") && o.get("TASKCONTENT")!=null){
//                            mXCRWEntity.setTASKCONTENT(o.get("TASKCONTENT").toString());
//                        }
//                        if(o.has("TASKTITLE") && o.get("TASKTITLE")!=null){
//                            mXCRWEntity.setTASKTITLE(o.get("TASKTITLE").toString());
//                        }
//                        if(o.has("RECEIVER_ID") && o.get("RECEIVER_ID")!=null){
//                            mXCRWEntity.setRECEIVER_ID(o.get("RECEIVER_ID").toString());
//                        }
//                        if(o.has("SENDER_ID") && o.get("SENDER_ID")!=null){
//                            mXCRWEntity.setSENDER_ID(o.get("SENDER_ID").toString());
//                        }
//                        if(o.has("RWBH") && o.get("RWBH")!=null){
//                            mXCRWEntity.setRWBH(o.get("RWBH").toString());
//                        }
//                        if(o.has("ID") && o.get("ID")!=null){
//                            mXCRWEntity.setID(o.get("ID").toString());
//                        }
//                        if(o.has("X") && o.get("X")!=null){
//                            mXCRWEntity.setX(o.get("X").toString());
//                        }
//                        if(o.has("Y") && o.get("Y")!=null){
//                            mXCRWEntity.setY(o.get("Y").toString());
//                        }
//                        NEWXCRW_list.add(mXCRWEntity);
//                    }
//                }else{
//                    ToastUtil.show(this, "服务器返回值为空");
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (TimeoutException e) {
//                e.printStackTrace();
//                ToastUtil.show(this,"连接服务器超时");
//            } catch (JSONException e) {
//                e.printStackTrace();
//                ToastUtil.show(this,"解析JSON错误");
//            }
//
//            GetCOMPLETEXCRWDataSync getCOMPLETEXCRWDataSync=new GetCOMPLETEXCRWDataSync(pid,rwxz,rwlx,rwztYwcrw,pznf);
//            try {
//                String dataRslt=getCOMPLETEXCRWDataSync.execute().get(20, TimeUnit.SECONDS);
//                if(dataRslt!=null && dataRslt.contains("")){
//                    YWCXCRW_list.clear();
//                    JSONArray jsonArray = new JSONArray(dataRslt);
//
//                    for(int k=0;k<jsonArray.length();k++){
//
//                        JSONObject o = (JSONObject) jsonArray.get(k);
//                        mXCRWEntity=new XCRWEntity();
//
//                        if(o.has("SENDTIME") && o.get("SENDTIME")!=null){
//                            if(o.get("SENDTIME").toString().contains("Date")){
//                                int len=o.get("SENDTIME").toString().length();
//                                String d=o.get("SENDTIME").toString().substring(6,len-2);
//                                Date dat=new Date(Long.parseLong(d));
//                                GregorianCalendar gc = new GregorianCalendar();
//                                gc.setTime(dat);
//                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//                                String sb=format.format(gc.getTime());
//                                mXCRWEntity.setSENDTIME(sb);
//                            }else{
//                                mXCRWEntity.setSENDTIME("无数据");
//                            }
//                        }
//                        if(o.has("TASKTYPE") && o.get("TASKTYPE")!=null){
//                            mXCRWEntity.setTASKTYPE(o.get("TASKTYPE").toString());
//                        }
//                        if(o.has("COMPLETETIME") && o.get("COMPLETETIME")!=null){
//                            mXCRWEntity.setCOMPLETETIME(o.get("COMPLETETIME").toString());
//                        }
//                        if(o.has("STATE") && o.get("STATE")!=null){
//                            mXCRWEntity.setSTATE(o.get("STATE").toString());
//                        }
//                        if(o.has("RESULTFILES") && o.get("RESULTFILES")!=null){
//                            mXCRWEntity.setRESULTFILES(o.get("RESULTFILES").toString());
//                        }
//                        if(o.has("RESULTCONTENT") && o.get("RESULTCONTENT")!=null){
//                            mXCRWEntity.setRESULTCONTENT(o.get("RESULTCONTENT").toString());
//                        }
//                        if(o.has("TASKFILES") && o.get("TASKFILES")!=null){
//                            mXCRWEntity.setTASKFILES(o.get("TASKFILES").toString());
//                        }
//                        if(o.has("TASKAddress") && o.get("TASKAddress")!=null){
//                            mXCRWEntity.setTASKAddress(o.get("TASKAddress").toString());
//                        }
//                        if(o.has("N") && o.get("N")!=null){
//                            mXCRWEntity.setN(o.get("N").toString());
//                        }
//                        if(o.has("E") && o.get("E")!=null){
//                            mXCRWEntity.setE(o.get("E").toString());
//                        }
//                        if(o.has("TASKCONTENT") && o.get("TASKCONTENT")!=null){
//                            mXCRWEntity.setTASKCONTENT(o.get("TASKCONTENT").toString());
//                        }
//                        if(o.has("TASKTITLE") && o.get("TASKTITLE")!=null){
//                            mXCRWEntity.setTASKTITLE(o.get("TASKTITLE").toString());
//                        }
//                        if(o.has("RECEIVER_ID") && o.get("RECEIVER_ID")!=null){
//                            mXCRWEntity.setRECEIVER_ID(o.get("RECEIVER_ID").toString());
//                        }
//                        if(o.has("SENDER_ID") && o.get("SENDER_ID")!=null){
//                            mXCRWEntity.setSENDER_ID(o.get("SENDER_ID").toString());
//                        }
//                        if(o.has("RWBH") && o.get("RWBH")!=null){
//                            mXCRWEntity.setRWBH(o.get("RWBH").toString());
//                        }
//                        if(o.has("ID") && o.get("ID")!=null){
//                            mXCRWEntity.setID(o.get("ID").toString());
//                        }
//                        if(o.has("X") && o.get("X")!=null){
//                            mXCRWEntity.setX(o.get("X").toString());
//                        }
//                        if(o.has("Y") && o.get("Y")!=null){
//                            mXCRWEntity.setY(o.get("Y").toString());
//                        }
//                        YWCXCRW_list.add(mXCRWEntity);
//                    }
//                }else{
//                    ToastUtil.show(this, "服务器返回值为空");
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (TimeoutException e) {
//                e.printStackTrace();
//                ToastUtil.show(this,"连接服务器超时");
//            } catch (JSONException e) {
//                e.printStackTrace();
//                ToastUtil.show(this,"解析JSON错误");
//            }
//        }
//
//        if(NEWXCRW_list!=null && NEWXCRW_list.size()>0){
//            MyApp.setWWCXCRW_list(null);
//            MyApp.setWWCXCRW_list(NEWXCRW_list);
//        }
//
//        if(YWCXCRW_list!=null && YWCXCRW_list.size()>0){
//            MyApp.setYWCXCRW_list(null);
//            MyApp.setYWCXCRW_list(YWCXCRW_list);
//        }
//
//        int ywcSize=YWCXCRW_list.size();
//        YWCRWbadge.setText(ywcSize+"");
//        YWCRWbadge.show();
//
//        int xrwSize=NEWXCRW_list.size();
//        DWCRWbadge.setText(xrwSize+"");
//        DWCRWbadge.show();
    }
}