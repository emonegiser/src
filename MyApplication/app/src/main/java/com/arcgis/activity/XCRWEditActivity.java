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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.entity.XCRWEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;
import com.esri.core.geometry.Point;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class XCRWEditActivity extends Activity implements View.OnClickListener{

    private Button queryBtn=null;

    private TextView backtextview;
    private TextView titletextview;
    private TextView mapBtn;

    //任务类型
    TextView TextViewrwlx=null;
    //任务主题
    TextView TextViewrwzt= null;
    //任务内容
    TextView TextViewrwnr= null;
    //任务地点
    TextView TextViewrwdd= null;
    //任务经度
    TextView TextViewrwjd=null;
    //任务纬度
    TextView TextViewrwwd= null;
    //任务附件
    TextView TextViewrwfj= null;
    //巡查结果内容
    TextView TextViewxcjgnr= null;
    //巡查结果附件
    TextView TextViewxcjgfj= null;
    //任务状态
    TextView TextViewrwztai= null;
    //任务开始时间
    TextView TextViewrwkssj=null;
    //任务结束时间
    static TextView TextViewrwjssj=null;
    //发送人
    TextView TextViewfsr=null;
    //接收人
    TextView TextViewjsr=null;
    //定位
    Button locationBtn=null;
    //上传附件
    Button uploadFJBtn=null;
    //上传巡查结果附件
    Button uploadJGBtn=null;


    private XCRWEntity mXCRWEntity;
    private Point point;
   // private List<Point> sbyd_pt_list=new ArrayList<Point>();

    //调用webservice
    private KsoapValidateHttp ksoap;

    private String kcbh;
    DecimalFormat df=null;
    //全局变量存储位置
    private App MyApp;

    private String px=null;
    private String py=null;
    private boolean isNetwork=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_xcrw_edit);
        isNetwork= NetUtils.isNetworkAvailable(this);
        App.getInstance().addActivity(this);

        backtextview=(TextView)findViewById(R.id.backBtn);
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.NavigateTitle);
        titletextview.setText("巡查任务编辑");

        mapBtn= (TextView) findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(this);

        df=(DecimalFormat) NumberFormat.getInstance();
        df.setMaximumFractionDigits(8);

        MyApp=(App) this.getApplication();

        locationBtn= (Button) findViewById(R.id.locationBtn);
        locationBtn.setOnClickListener(this);
        uploadFJBtn= (Button) findViewById(R.id.uploadFJBtn);
        uploadFJBtn.setOnClickListener(this);
        uploadJGBtn= (Button) findViewById(R.id.uploadJGBtn);
        uploadJGBtn.setOnClickListener(this);


        TextViewrwlx= (TextView) this.findViewById(R.id.TextViewrwlx);
        TextViewrwzt= (TextView) this.findViewById(R.id.TextViewrwzt);
        TextViewrwnr= (TextView) this.findViewById(R.id.TextViewrwnr);
        TextViewrwdd= (TextView) this.findViewById(R.id.TextViewrwdd);
        TextViewrwjd= (TextView) this.findViewById(R.id.TextViewrwjd);
        TextViewrwwd= (TextView) this.findViewById(R.id.TextViewrwwd);
        TextViewrwfj= (TextView) this.findViewById(R.id.TextViewrwfj);
        TextViewxcjgnr= (TextView) this.findViewById(R.id.TextViewxcjgnr);
        TextViewxcjgfj= (TextView) this.findViewById(R.id.TextViewxcjgfj);
        TextViewrwztai= (TextView) this.findViewById(R.id.TextViewrwztai);
        TextViewrwkssj= (TextView) this.findViewById(R.id.TextViewrwkssj);
        TextViewrwjssj= (TextView) this.findViewById(R.id.TextViewrwjssj);
        TextViewrwjssj.setOnClickListener(this);
        TextViewfsr= (TextView) this.findViewById(R.id.TextViewfsr);
        TextViewjsr= (TextView) this.findViewById(R.id.TextViewjsr);

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
                //保存编辑结果
                String bh=mXCRWEntity.getRWBH();
                String receiverid=mXCRWEntity.getRECEIVER_ID();
//                String rq=mXCRWEntity.getCOMPLETETIME();
                String savecontent=mXCRWEntity.getRESULTCONTENT();
                String files=mXCRWEntity.getRESULTFILES();
                GetDZDataSync saveEditResult=new GetDZDataSync(bh,receiverid,savecontent,files);
                try {
                    String resultStr=saveEditResult.execute().get(20, TimeUnit.SECONDS);
                    if(resultStr!=null && !resultStr.isEmpty()){
                        if(resultStr.equals("1")){
                            ToastUtil.show(this,"任务修改成功");
                        }
                        if(resultStr.equals("0")){
                            ToastUtil.show(this,"任务修改失败");
                        }
                    }else{
                        ToastUtil.show(this,"服务器没有返回值");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }


                break;
            case R.id.backBtn:
                XCRWEditActivity.this.finish();
                break;
            case R.id.locationBtn:
                //定位任务点
                Intent toMapIntent=new Intent();
                toMapIntent.setClass(XCRWEditActivity.this,MainMap7Activity.class);
                toMapIntent.putExtra("FROM","CENTERAT");
                toMapIntent.putExtra("PX",this.px);
                toMapIntent.putExtra("PY",this.py);
                toMapIntent.putExtra("TEXT",mXCRWEntity.getRWBH());
                startActivity(toMapIntent);
                break;
            case R.id.uploadFJBtn:
                //上传附件
                break;
            case R.id.uploadJGBtn:
                //上传结果附件
                break;
            case R.id.TextViewrwjssj:
                //编辑任务结束时间
                DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.show(getFragmentManager(), "datePicker");
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            XCRWEditActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = this.getIntent();
        if(intent!=null){
            mXCRWEntity= (XCRWEntity) intent.getSerializableExtra("XCRW");
            if(mXCRWEntity!=null){
                this.px=mXCRWEntity.getX();
                this.py=mXCRWEntity.getY();

                TextViewrwlx.setText(mXCRWEntity.getTASKTYPE());
                TextViewrwzt.setText(mXCRWEntity.getTASKTITLE());
                TextViewrwnr.setText(mXCRWEntity.getTASKCONTENT());
                TextViewrwdd.setText(mXCRWEntity.getTASKAddress());
                TextViewrwjd.setText(mXCRWEntity.getE());
                TextViewrwwd.setText(mXCRWEntity.getN());
                TextViewrwfj.setText(mXCRWEntity.getTASKFILES());
                TextViewxcjgnr.setText(mXCRWEntity.getRESULTCONTENT());
                TextViewxcjgfj.setText(mXCRWEntity.getRESULTFILES());
                TextViewrwztai.setText(mXCRWEntity.getSTATE());
                TextViewrwkssj.setText(mXCRWEntity.getSENDTIME());
                TextViewrwjssj.setText(mXCRWEntity.getCOMPLETETIME());
                TextViewfsr.setText(mXCRWEntity.getSENDER_ID());
                TextViewjsr.setText(mXCRWEntity.getRECEIVER_ID());
            }
        }
        if(!isNetwork){
            ToastUtil.show(this,"请检查网络连接");
            //return;
        }

    }
//保存任务信息
    public class GetDZDataSync extends AsyncTask<String, Integer, String> {
        String bh;
        String receiverid;
//        String rq;
        String savecontent;
        String files;

        public GetDZDataSync(String bh,String receiverid,String content,String files) {
            this.bh=bh;
            this.receiverid=receiverid;
//            this.rq=rq;
            this.savecontent=content;
            this.files=files;
            ksoap=new KsoapValidateHttp(XCRWEditActivity.this);
        }


        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebUpdateInspectionMission(bh,receiverid,savecontent, files);
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

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

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

            TextViewrwjssj.setText(year+"-"+m+"-"+d);
        }
    }
}