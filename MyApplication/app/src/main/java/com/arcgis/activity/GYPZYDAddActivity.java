package com.arcgis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.dao.GYYDDao;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GYPZYDAddActivity extends Activity implements View.OnClickListener{

    //地块坐标
    TextView editTextzb= null;
    //地块编号
    EditText editTextkqbh= null;
    //供应面积
    EditText editTextmj=null;
    //供地批复名称
    EditText editTextgdpfmc= null;
    //供地批复文号
    EditText EditTextgdpfwh=null;
    //供地批复时间
  static   EditText EditTextgdpfsj= null;
    //土地用途
    Spinner spinnertdyt=null;
    //容积率
    EditText editTextrjl=null;
    //建设密度
    EditText editTextjsmd=null;
    //绿化比率
    EditText EditTextlhbl=null;
    //供应方式
    Spinner spinnergyfs=null;
    //用地单位名称
    EditText editTextyddwmc=null;
    //用地项目名称
    EditText editTextydxmmc=null;
    //合同编号
    EditText EditTexthtbh=null;
    //成交款项
    EditText EditTextcjkx=null;
    //合同签订时间
   static EditText EditTexthtjdsj=null;
    //实际交地时间
    static EditText EditTextsjjdsj=null;
    //实际开工时间
  static   EditText EditTextsjkgsj=null;
    //竣工核验时间
 static    EditText EditTextjghysj=null;
    //合同约定交地时间
    static    EditText EditTexthtydjdsj=null;
    //合同约定动工时间
    static   EditText EditTexthtyddgsj=null;
    //合同约定竣工时间
    static  EditText EditTexthtydjgsj=null;
    //申请开工时间
    static   EditText EditTextsqkgsj=null;
    //实际竣工时间
    static  EditText EditTextsjjgsj=null;
    //竣工核验情况
    Spinner spinnerjghyqk=null;

    private Button BackBtn=null;
    private Button OkBtn=null;

    private TextView backtextview;
    private TextView titletextview;

    //调用webservice
    private KsoapValidateHttp ksoap;

    //土地用途
    ArrayAdapter<String> spinnertdytAdapter;
    //供应方式
    ArrayAdapter<String> spinnergyfsAdapter;
    //竣工核验情况
    ArrayAdapter<String> spinnerjghyqkAdapter;

    //全局变量存储位置
    private App MyApp;

    //坐标
    String XY=null;
    //供应用地编号
    String gyydId=null;
    //地块面积
    String gyydArea=null;
    //容积率
    String rjl=null;
    //建设密度
    String jsmd=null;
    //绿化比率
    String lhbl=null;
    //成交款额
    String cjMoney=null;
    //供应地批复名称
    String gdpfName=null;
    //供应批复文化
    String gdpfCode=null;
    //供应地批复时间
    String gdpfTime=null;
    //土地用途
    String tdyt=null;
    //合同编号
    String htId=null;
    //供应方式
    String gyfs=null;
    //用地单位名称
    String yddwName=null;
    //用地项目名称
    String ydxmName=null;
    //合同签订时间
    String htqdTime=null;
    //合同约定时间
    String htydJDTime=null;
    //合同约定竣工时间
    String htydJGTime=null;
    //合同约定交地时间
    String htydDGTime=null;
    //实际竣工时间
    String SJJDSJ=null;
    //申请开工时间
    String SQKGSJ=null;
    //实际开工时间
    String SJKGSJ=null;
    //实际竣工时间
    String SJJGSJ=null;
    //竣工核验时间
    String JGHYSJ=null;
    //竣工核验情况
    String JGHYQK=null;

    SimpleDateFormat format=null;
    private boolean isNetwork=false;
    ProgressDialog progressdialog;

    private GYYDDao gyydDao=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addgypzyd);
        App.getInstance().addActivity(this);
        gyydDao=new GYYDDao(this);

        BackBtn= (Button) findViewById(R.id.BackBtn);
        BackBtn.setOnClickListener(this);

        OkBtn= (Button) findViewById(R.id.OkBtn);
        OkBtn.setOnClickListener(this);

        backtextview= (TextView) findViewById(R.id.backtextview);
        backtextview.setOnClickListener(this);
        titletextview= (TextView) findViewById(R.id.titletextview);
        titletextview.setText("添加供应批准用地");

        //地块坐标
        editTextzb= (TextView) this.findViewById(R.id.editTextzb);
        editTextzb.setOnClickListener(this);
        //地块编号
        editTextkqbh= (EditText) this.findViewById(R.id.editTextkqbh);
        //地块面积
        editTextmj= (EditText) findViewById(R.id.editTextmj);
        editTextgdpfmc= (EditText) findViewById(R.id.editTextgdpfmc);
        EditTextgdpfwh= (EditText) findViewById(R.id.EditTextgdpfwh);
        EditTextgdpfsj= (EditText) findViewById(R.id.EditTextgdpfsj);
        EditTextgdpfsj.setOnClickListener(this);
        spinnertdyt= (Spinner) findViewById(R.id.spinnertdyt);
        editTextrjl= (EditText) findViewById(R.id.editTextrjl);
        editTextjsmd= (EditText) findViewById(R.id.editTextjsmd);
        EditTextlhbl= (EditText) findViewById(R.id.EditTextlhbl);
        spinnergyfs= (Spinner) findViewById(R.id.spinnergyfs);
        editTextyddwmc= (EditText) findViewById(R.id.editTextyddwmc);
        editTextydxmmc= (EditText) findViewById(R.id.editTextydxmmc);
        EditTexthtbh= (EditText) findViewById(R.id.EditTexthtbh);
        EditTextcjkx= (EditText) findViewById(R.id.EditTextcjkx);
        EditTexthtjdsj= (EditText) findViewById(R.id.EditTexthtjdsj);
        EditTexthtjdsj.setOnClickListener(this);
        EditTextsjjdsj= (EditText) findViewById(R.id.EditTextsjjdsj);
        EditTextsjjdsj.setOnClickListener(this);
        EditTextsjkgsj= (EditText) findViewById(R.id.EditTextsjkgsj);
        EditTextsjkgsj.setOnClickListener(this);
        EditTextjghysj= (EditText) findViewById(R.id.EditTextjghysj);
        EditTextjghysj.setOnClickListener(this);
        EditTexthtydjdsj= (EditText) findViewById(R.id.EditTexthtydjdsj);
        EditTexthtydjdsj.setOnClickListener(this);
        EditTexthtyddgsj= (EditText) findViewById(R.id.EditTexthtyddgsj);
        EditTexthtyddgsj.setOnClickListener(this);
        EditTextsqkgsj= (EditText) findViewById(R.id.EditTextsqkgsj);
        EditTextsqkgsj.setOnClickListener(this);
        EditTextsjjgsj= (EditText) findViewById(R.id.EditTextsjjgsj);
        EditTextsjjgsj.setOnClickListener(this);
        EditTexthtydjgsj=(EditText) findViewById(R.id.EditTexthtydjgsj);
        EditTexthtydjgsj.setOnClickListener(this);
        spinnerjghyqk= (Spinner) findViewById(R.id.spinnerjghyqk);
        MyApp=(App) this.getApplication();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            GYPZYDAddActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        switch(viewId){
            //添加
            case R.id.OkBtn:

                isNetwork= NetUtils.isNetworkAvailable(this);

                XY=editTextzb.getText().toString();
                gyydId=editTextkqbh.getText().toString();
                gyydArea=editTextmj.getText().toString();
                rjl=editTextrjl.getText().toString();
                jsmd=editTextjsmd.getText().toString();
                cjMoney=EditTextcjkx.getText().toString();
                gdpfName=editTextgdpfmc.getText().toString();
                gdpfCode=EditTextgdpfwh.getText().toString();
                gdpfTime=EditTextgdpfsj.getText().toString();
                tdyt=spinnertdyt.getSelectedItem().toString();
                htId=EditTexthtbh.getText().toString();
                gyfs=spinnergyfs.getSelectedItem().toString();
                yddwName=editTextyddwmc.getText().toString();
                ydxmName=editTextydxmmc.getText().toString();
                htqdTime=EditTexthtjdsj.getText().toString();
                htydJDTime=EditTexthtydjdsj.getText().toString();
                htydDGTime=EditTexthtyddgsj.getText().toString();
                htydJGTime=EditTexthtydjgsj.getText().toString();
                SJJDSJ=EditTextsjjdsj.getText().toString();
                SQKGSJ=EditTextsqkgsj.getText().toString();
                SJKGSJ=EditTextsjkgsj.getText().toString();
                SJJGSJ=EditTextsjjgsj.getText().toString();
                JGHYSJ=EditTextjghysj.getText().toString();
                JGHYQK=spinnerjghyqk.getSelectedItem().toString();

                if(gyydId ==null || gyydId.isEmpty()){
                    ToastUtil.show(this,"地块编号不能为空!");
                    return;
                }
                if(gdpfName ==null || gdpfName.isEmpty()){
                    ToastUtil.show(this,"供地批复名称不能为空!");
                    return;
                }

                //添加批准用地
                if(isNetwork){
                    GYPZYDADDDataSync AddGYPZYDSync=new GYPZYDADDDataSync(XY,gyydId, gyydArea, rjl, jsmd,
                            cjMoney, gdpfName, gdpfCode, gdpfTime,
                            tdyt, htId, gyfs, yddwName,
                            ydxmName, htqdTime, htydJDTime, htydDGTime,
                            htydJGTime, SJJDSJ, SQKGSJ, SJKGSJ,
                            SJJGSJ, JGHYSJ, JGHYQK);
                    try {
                        String addRslt=AddGYPZYDSync.execute().get(300, TimeUnit.SECONDS);
                        if(addRslt!=null && !addRslt.isEmpty()){
                            if(addRslt.equals("1")){
                                addRslt="保存成功!";
                                ToastUtil.show(GYPZYDAddActivity.this,addRslt);
                                Intent toMapIntent=new Intent();
                                toMapIntent.setClass(GYPZYDAddActivity.this,MainMap5Activity.class);
                                setResult(RESULT_OK,toMapIntent);
                                GYPZYDAddActivity.this.finish();
                            }else if(addRslt.equals("0")){
                                addRslt="保存失败!";
                                ToastUtil.show(GYPZYDAddActivity.this,addRslt+"数据被保存至本地数据库");
                                GYPZYDEntity gypzydEntity=new GYPZYDEntity();

                                /**
                                 cjMoney=EditTextcjkx.getText().toString();
                                 htqdTime=EditTexthtjdsj.getText().toString();
                                 */
                                gypzydEntity.setCoords(XY);
                                gypzydEntity.setHTYDDGSJ(htydDGTime);
                                gypzydEntity.setHTYDJDSJ(htydJDTime);
                                gypzydEntity.setHTBH(htId);
                                gypzydEntity.setGYYDBH(gyydId);
                                gypzydEntity.setGYMJ(gyydArea);
                                gypzydEntity.setRJL(rjl);
                                gypzydEntity.setJZMD(jsmd);
                                gypzydEntity.setGDPFMC(gdpfName);
                                gypzydEntity.setGDPFWH(gdpfName);
                                gypzydEntity.setGDPFSJ(gdpfTime);
                                gypzydEntity.setTDYT(tdyt);
                                gypzydEntity.setGDFS(gyfs);
                                gypzydEntity.setYDDWMC(yddwName);
                                gypzydEntity.setYDXMMC(ydxmName);
                                gypzydEntity.setHTYDJGSJ(htydJGTime);
                                gypzydEntity.setSJJDSJ(SJJDSJ);
                                gypzydEntity.setSQKGSJ(SQKGSJ);
                                gypzydEntity.setSJKGSJ(SJKGSJ);
                                gypzydEntity.setSJJGSJ(SJJGSJ);
                                gypzydEntity.setJGTDHYSJ(JGHYSJ);
                                gypzydEntity.setJGTDHYQK(JGHYQK);
                                gyydDao.add(gypzydEntity);
                            }else{
                                ToastUtil.show(GYPZYDAddActivity.this,"服务器无返回值");
                            }

                        }else{
                            ToastUtil.show(this,"服务器无返回值");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        ToastUtil.show(GYPZYDAddActivity.this,"线程中断异常");
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        ToastUtil.show(GYPZYDAddActivity.this,"线程执行异常");
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        ToastUtil.show(GYPZYDAddActivity.this,"连接服务器超时");
                    }
                }else{
                    ToastUtil.show(this,"当前无网络,该数据被保存至本地数据库");
                    GYPZYDEntity gypzydEntity=new GYPZYDEntity();

                    /**
                     cjMoney=EditTextcjkx.getText().toString();
                     htqdTime=EditTexthtjdsj.getText().toString();
                     */
                    gypzydEntity.setCoords(XY);
                    gypzydEntity.setHTYDDGSJ(htydDGTime);
                    gypzydEntity.setHTYDJDSJ(htydJDTime);
                    gypzydEntity.setHTBH(htId);
                    gypzydEntity.setGYYDBH(gyydId);
                    gypzydEntity.setGYMJ(gyydArea);
                    gypzydEntity.setRJL(rjl);
                    gypzydEntity.setJZMD(jsmd);
                    gypzydEntity.setGDPFMC(gdpfName);
                    gypzydEntity.setGDPFWH(gdpfName);
                    gypzydEntity.setGDPFSJ(gdpfTime);
                    gypzydEntity.setTDYT(tdyt);
                    gypzydEntity.setGDFS(gyfs);
                    gypzydEntity.setYDDWMC(yddwName);
                    gypzydEntity.setYDXMMC(ydxmName);
                    gypzydEntity.setHTYDJGSJ(htydJGTime);
                    gypzydEntity.setSJJDSJ(SJJDSJ);
                    gypzydEntity.setSQKGSJ(SQKGSJ);
                    gypzydEntity.setSJKGSJ(SJKGSJ);
                    gypzydEntity.setSJJGSJ(SJJGSJ);
                    gypzydEntity.setJGTDHYSJ(JGHYSJ);
                    gypzydEntity.setJGTDHYQK(JGHYQK);
                    gyydDao.add(gypzydEntity);
                }

                break;
            case R.id.BackBtn:
                GYPZYDAddActivity.this.finish();
                break;
            case R.id.EditTextgdpfsj:
                DatePickerFragment datePicker = new DatePickerFragment();

                datePicker.show(getFragmentManager(), "datePicker");
                break;
            case R.id.EditTexthtjdsj:
                DatePickerFragment2 datePicker2 = new DatePickerFragment2();
                datePicker2.show(getFragmentManager(), "datePicker");
                break;
            case R.id.EditTextsjjdsj:
                DatePickerFragment3 datePicker3 = new DatePickerFragment3();
                datePicker3.show(getFragmentManager(), "datePicker");
                break;
            case R.id.EditTextsjkgsj:
                DatePickerFragment4 datePicker4 = new DatePickerFragment4();
                datePicker4.show(getFragmentManager(), "datePicker");
                break;
            case R.id.EditTextjghysj:
                DatePickerFragment5 datePicker5 = new DatePickerFragment5();
                datePicker5.show(getFragmentManager(), "datePicker");
                break;
            case R.id.EditTexthtydjdsj:
                DatePickerFragment6 datePicker6 = new DatePickerFragment6();
                datePicker6.show(getFragmentManager(), "datePicker");
                break;
            case R.id.EditTexthtyddgsj:
                DatePickerFragment7 datePicker7 = new DatePickerFragment7();
                datePicker7.show(getFragmentManager(), "datePicker");
                break;
            case R.id.EditTextsqkgsj:
                DatePickerFragment8 datePicker8 = new DatePickerFragment8();
                datePicker8.show(getFragmentManager(), "datePicker");
                break;
            case R.id.EditTextsjjgsj:
                DatePickerFragment9 datePicker9 = new DatePickerFragment9();
                datePicker9.show(getFragmentManager(), "datePicker");
                break;
            case R.id.EditTexthtydjgsj:
                DatePickerFragment10 datePicker10 = new DatePickerFragment10();
                datePicker10.show(getFragmentManager(), "datePicker");
                break;
            case R.id.backtextview:
                GYPZYDAddActivity.this.finish();
                break;
            case R.id.editTextzb:
                String coords=editTextzb.getText().toString();
                LayoutInflater factory = LayoutInflater.from(GYPZYDAddActivity.this);
                View view = factory.inflate(R.layout.activity_zbdetail, null);
                TextView editTextzbdetail = (TextView) view.findViewById(R.id.editTextzbdetail);
                editTextzbdetail.setText(coords);
                AlertDialog.Builder builder = new AlertDialog.Builder(GYPZYDAddActivity.this);
                builder.setTitle("坐标详情");
                builder.setView(view);
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String coordsStr=null;

        Intent fromMapIntent=this.getIntent();
        if(fromMapIntent!=null){
            coordsStr= fromMapIntent.getStringExtra("COORDSTR");
            editTextzb.setText(coordsStr);
        }

        //处理时间
        format = new SimpleDateFormat("yyyy-MM-dd");
        String dataStr = format.format(new Date());
        EditTextgdpfsj.setText(dataStr);
        EditTexthtjdsj.setText(dataStr);
        EditTexthtyddgsj.setText(dataStr);
        EditTexthtydjdsj.setText(dataStr);
        EditTexthtydjgsj.setText(dataStr);
        EditTextjghysj.setText(dataStr);
        EditTextsjjdsj.setText(dataStr);
        EditTextsjjgsj.setText(dataStr);
        EditTextsjkgsj.setText(dataStr);
        EditTextsqkgsj.setText(dataStr);

        isNetwork=NetUtils.isNetworkAvailable(this);
        if(isNetwork){
            //调用接口获取村信息
            GetBHDataSync getAreaDataSync=new GetBHDataSync(coordsStr);
            try {
                String rsltStr=getAreaDataSync.execute().get(300,TimeUnit.SECONDS);
                if(rsltStr!=null && !rsltStr.isEmpty()){
                    JSONArray jsonArray = new JSONArray(rsltStr);
                    //[{"MIANJI":"2258420.02005503","CUN":"大兰村","BH":"9353819","ZHEN":"流仓桥办事处"}]
                    for(int k=0;k<jsonArray.length();k++){
                        JSONObject o = (JSONObject) jsonArray.get(k);
                        if(o.has("MIANJI") && o.get("MIANJI")!=null){
                            editTextmj.setText(o.get("MIANJI").toString());
                        }
                        if(o.has("DKBH") && o.get("DKBH")!=null){
                            editTextkqbh.setText(o.get("DKBH").toString());
                        }
                        if(o.has("HTBH") && o.get("HTBH")!=null){
                            EditTexthtbh.setText(o.get("HTBH").toString());
                        }

                    }
                }else{
                    ToastUtil.show(GYPZYDAddActivity.this,"服务器无返回值");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                ToastUtil.show(this,"连接服务器超时");
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtil.show(this,"解析JSON错误");
            }
        }else{
            ToastUtil.show(GYPZYDAddActivity.this,"网络异常,无法获取供应用地信息");
        }


        if(MyApp.getGYYD_TDYT_list()!=null && MyApp.getGYYD_TDYT_list().size()>0){
            spinnertdytAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, MyApp.getGYYD_TDYT_list());
            spinnertdytAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnertdyt.setAdapter(spinnertdytAdapter);
        }

        if(MyApp.getGYYD_GYFS_list()!=null && MyApp.getGYYD_GYFS_list().size()>0){
            spinnergyfsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, MyApp.getGYYD_GYFS_list());
            spinnergyfsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnergyfs.setAdapter(spinnergyfsAdapter);
        }

        List<String> hyqk_list=new ArrayList<String>();
        hyqk_list.add("合格");
        hyqk_list.add("不合格");

        spinnerjghyqkAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, hyqk_list);
        spinnerjghyqkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerjghyqk.setAdapter(spinnerjghyqkAdapter);



    }

    public class GetBHDataSync extends AsyncTask<String, Integer, String> {
        private String coords;
        public GetBHDataSync(String coords) {
            this.coords=coords;
            ksoap=new KsoapValidateHttp(GYPZYDAddActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetBHIArea(coords);
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

    public class GYPZYDADDDataSync extends AsyncTask<String, Integer, String> {

        String XY=null;
        String gyydId=null;
        String gyydArea=null;
        String rjl=null;
        String jsmd=null;
        String cjMoney=null;
        String gdpfName=null;
        String gdpfCode=null;
        String gdpfTime=null;
        String tdyt=null;
        String htId=null;
        String gyfs=null;
        String yddwName=null;
        String ydxmName=null;
        String htqdTime=null;
        String htydJDTime=null;
        String htydDGTime=null;
        String htydJGTime=null;
        String SJJDSJ=null;
        String SQKGSJ=null;
        String SJKGSJ=null;
        String SJJGSJ=null;
        String JGHYSJ=null;
        String JGHYQK=null;


        ProgressDialog progressdialogSave=null;

        public GYPZYDADDDataSync(String XY,String gyydId,String gyydArea,String rjl,String jsmd,
                               String cjMoney,String gdpfName,String gdpfCode,String gdpfTime,
                               String tdyt,String htId,String gyfs,String yddwName,
                               String ydxmName,String htqdTime,String htydJDTime,String htydDGTime,
                               String htydJGTime,String SJJDSJ,String SQKGSJ,String SJKGSJ,
                               String SJJGSJ,String JGHYSJ,String JGHYQK) {
            this.XY = XY;
            this.gyydId=gyydId;
            this.gyydArea=gyydArea;
            this.rjl=rjl;
            this.jsmd=jsmd;
            this.cjMoney=cjMoney;
            this.gdpfName=gdpfName;
            this.gdpfCode=gdpfCode;
            this.gdpfTime=gdpfTime;
            this.tdyt=tdyt;
            this.htId=htId;
            this.gyfs=gyfs;
            this.yddwName=yddwName;
            this.ydxmName=ydxmName;
            this.htqdTime=htqdTime;
            this.htydJDTime=htydJDTime;
            this.htydDGTime=htydDGTime;
            this.htydJGTime=htydJGTime;
            this.SJJDSJ=SJJDSJ;
            this.SQKGSJ=SQKGSJ;
            this.SJKGSJ=SJKGSJ;
            this.SJJGSJ=SJJGSJ;
            this.JGHYSJ=JGHYSJ;
            this.JGHYQK=JGHYQK;


            ksoap=new KsoapValidateHttp(GYPZYDAddActivity.this);
        }

        @Override
        protected void onPreExecute() {
            progressdialogSave=new ProgressDialog(GYPZYDAddActivity.this);
            progressdialogSave.setCancelable(true);
            progressdialogSave.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressdialogSave.setMessage("保存供应用地...");
            progressdialogSave.setIndeterminate(true);
            progressdialogSave.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebAddLandProvision(XY,gyydId, gyydArea, rjl, jsmd,cjMoney,
                        gdpfName, gdpfCode, gdpfTime,tdyt, htId, gyfs, yddwName,ydxmName, htqdTime,
                        htydJDTime, htydDGTime,htydJGTime, SJJDSJ, SQKGSJ, SJKGSJ,SJJGSJ, JGHYSJ, JGHYQK);
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

        @Override
        protected void onPostExecute(String s) {
            progressdialogSave.dismiss();
            super.onPostExecute(s);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private String loadPhoneStatus(){
        TelephonyManager phoneMgr=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceNo=phoneMgr.getDeviceId();
        Log.i("deviceNo", deviceNo);
        return deviceNo;
    }

    /**
     * 日期选择器
     */
    public static   class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public DatePickerFragment() {
        }

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
            EditTextgdpfsj.setText(year+"-"+m+"-"+d);
        }
    }


    public static class  DatePickerFragment2 extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public DatePickerFragment2() {
        }

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

            EditTexthtjdsj.setText(year+"-"+m+"-"+d);

        }
    }

    public static class  DatePickerFragment3 extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public DatePickerFragment3() {
        }

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

            EditTextsjjdsj.setText(year+"-"+m+"-"+d);
        }
    }

    public static class  DatePickerFragment4 extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public DatePickerFragment4() {
        }

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

            EditTextsjkgsj.setText(year+"-"+m+"-"+d);
        }
    }

    public static class  DatePickerFragment5 extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public DatePickerFragment5() {
        }

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

            EditTextjghysj.setText(year+"-"+m+"-"+d);
        }
    }
    public static class  DatePickerFragment6 extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public DatePickerFragment6() {
        }

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

            EditTexthtydjdsj.setText(year+"-"+m+"-"+d);
        }
    }

    public static  class  DatePickerFragment7 extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public DatePickerFragment7() {
        }

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

            EditTexthtyddgsj.setText(year+"-"+m+"-"+d);
        }
    }
    public static  class  DatePickerFragment8 extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public DatePickerFragment8() {
        }

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

            EditTextsqkgsj.setText(year+"-"+m+"-"+d);
        }
    }

    public static  class  DatePickerFragment9 extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public DatePickerFragment9() {
        }

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

            EditTextsjjgsj.setText(year+"-"+m+"-"+d);
        }
    }

    public static class  DatePickerFragment10 extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public DatePickerFragment10() {
        }

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

            EditTexthtydjgsj.setText(year+"-"+m+"-"+d);
        }
    }

}