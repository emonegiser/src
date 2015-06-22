package com.arcgis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.dao.WPURLDao;
import com.arcgis.entity.WPURLEntity;
import com.arcgis.entity.WPZFEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;
import com.esri.core.geometry.Point;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//卫片执法
public class WPZFDetailActivity extends Activity implements View.OnClickListener{

    private Button queryBtn=null;

    private TextView backtextview;
    private TextView titletextview;
    private TextView mapBtn;

    //发送人
    TextView TextViewfsr=null;
    //接收人
    TextView TextViewjsr=null;
    //卫片地址
    TextView TextViewwpurl=null;
    //任务状态
    TextView TextViewzt= null;
    //任务备注
    TextView TextViewrwbz=null;
    //下发日期
    TextView TextViewrwxfrq=null;
    //完成内容
    TextView TextViewwcnr=null;
    //完成附件
    TextView TextViewrwfj=null;
    //完成日期
    TextView TextViewwcrq=null;
    //下载附件
    Button downloadFJBtn=null;


    private WPZFEntity mWPZFEntity;
    private Point point;

    //调用webservice
    private KsoapValidateHttp ksoap;

    private String kcbh;
    DecimalFormat df=null;
    //全局变量存储位置
    private App MyApp;

    private WPURLEntity mWPURLEntity=null;
    List<WPURLEntity> WPURLEntity_list=new ArrayList<WPURLEntity>();

    private String px=null;
    private String py=null;
    private boolean isNetwork=false;
    private WPURLDao wpurlDao=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wpzf_detail);
        App.getInstance().addActivity(this);

        wpurlDao=new WPURLDao(this);

        backtextview=(TextView)findViewById(R.id.backBtn);
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.NavigateTitle);
        titletextview.setText("卫片执法详情");

        mapBtn= (TextView) findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(this);

        df=(DecimalFormat) NumberFormat.getInstance();
        df.setMaximumFractionDigits(8);

        MyApp=(App) this.getApplication();

        downloadFJBtn= (Button) findViewById(R.id.downloadFJBtn);
        downloadFJBtn.setOnClickListener(this);

        TextViewwpurl= (TextView) this.findViewById(R.id.TextViewwpurl);
        TextViewzt= (TextView) this.findViewById(R.id.TextViewzt);
        TextViewrwbz= (TextView) this.findViewById(R.id.TextViewrwbz);
        TextViewrwxfrq= (TextView) this.findViewById(R.id.TextViewrwxfrq);
        TextViewwcnr= (TextView) this.findViewById(R.id.TextViewwcnr);
        TextViewwcrq= (TextView) this.findViewById(R.id.TextViewwcrq);
        TextViewrwfj= (TextView) this.findViewById(R.id.TextViewrwfj);
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
                //进入卫片详情页面
                Intent toEditIntent=new Intent();
                toEditIntent.setClass(WPZFDetailActivity.this, WPURLListShowActivity.class);
                startActivity(toEditIntent);
                break;
            case R.id.backBtn:
                WPZFDetailActivity.this.finish();
                break;
            case R.id.downloadFJBtn:
                //下载附件
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            WPZFDetailActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = this.getIntent();
        isNetwork= NetUtils.isNetworkAvailable(this);
        if(intent!=null){
            mWPZFEntity= (WPZFEntity) intent.getSerializableExtra("WPZF");
            if(mWPZFEntity!=null){
                TextViewwpurl.setText(mWPZFEntity.getWPURLID());
                TextViewzt.setText(mWPZFEntity.getSTATE());
                TextViewrwbz.setText(mWPZFEntity.getTASKREMARK());
                TextViewrwxfrq.setText(mWPZFEntity.getTASKDATE());
                TextViewwcnr.setText(mWPZFEntity.getRESULTREMARK());
                TextViewwcrq.setText(mWPZFEntity.getRESULTDATE());
                TextViewrwfj.setText(mWPZFEntity.getRESULTFILES());
                TextViewfsr.setText(mWPZFEntity.getSENDERNAME());
                TextViewjsr.setText(mWPZFEntity.getRECIVERID());
            }
        }

        if(isNetwork){
            GetDZDataSync getDZDataSync=new GetDZDataSync(TextViewwpurl.getText().toString().trim());
            try {
                String rsltStr=getDZDataSync.execute().get(50, TimeUnit.SECONDS);
                if(rsltStr!=null){
                    WPURLEntity_list.clear();
                    /**
                     * {"ID":4,"XZ":"观音桥","MAPURL":"http://www.cnblogs.com/fengkuangshubiaodian/archive/2012/08/01/2609587.html",
                     * "FILES":"ArcGIS API for JS开发教程.pdf","REMARK":"DataColumn 是用来模拟物理数据库中的列。",
                     * "DATE":"\/Date(1423238400000)\/"}
                     */

                    JSONArray jsonArray = new JSONArray(rsltStr);
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject o = (JSONObject) jsonArray.get(k);
                        mWPURLEntity=new WPURLEntity();
                        if(o.has("ID") && o.has("ID") ){
                            mWPURLEntity.setID(o.get("ID").toString());
                        }
                        if(o.has("XZ") && o.has("XZ") ){
                            mWPURLEntity.setXZ(o.get("XZ").toString());
                        }
                        if(o.has("MAPURL") && o.has("MAPURL") ){
                            mWPURLEntity.setMAPURL(o.get("MAPURL").toString());
                        }
                        if(o.has("FILES") && o.has("FILES") ){
                            mWPURLEntity.setFILES(o.get("FILES").toString());
                        }
                        if(o.has("REMARK") && o.has("REMARK") ){
                            mWPURLEntity.setREMARK(o.get("REMARK").toString());
                        }
                        if(o.has("DATE") && o.has("DATE") ){
                            if(o.get("DATE").toString().contains("Date")){
                                int len=o.get("DATE").toString().length();
                                String d=o.get("DATE").toString().substring(6,len-2);
                                Date dat=new Date(Long.parseLong(d));
                                GregorianCalendar gc = new GregorianCalendar();
                                gc.setTime(dat);
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                String sb=format.format(gc.getTime());
                                mWPURLEntity.setDATE(sb);
                            }else{
                                mWPURLEntity.setDATE("无数据");
                            }
                        }
                        WPURLEntity_list.add(mWPURLEntity);
                        if(!wpurlDao.isExistEntity(o.get("ID").toString().trim())){
                            wpurlDao.add(mWPURLEntity);
                        }
                    }
                }else{
                    ToastUtil.show(WPZFDetailActivity.this,"服务器返回值为空");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
                ToastUtil.show(this,"执行异常");
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtil.show(this,"JSON解析错误");
            } catch (TimeoutException e) {
                e.printStackTrace();
                ToastUtil.show(this,"连接服务器超时");
            }
        }else{
            ToastUtil.show(this,"网络异常，无法加载详细卫片数据");
        }


        if(WPURLEntity_list!=null && WPURLEntity_list.size()>0){
            MyApp.setWPURLEntity_list(null);
            MyApp.setWPURLEntity_list(WPURLEntity_list);
        }
    }

    public class GetDZDataSync extends AsyncTask<String, Integer, String> {
        private String wpids;

        public GetDZDataSync(String wpids) {
            this.wpids=wpids;
            ksoap=new KsoapValidateHttp(WPZFDetailActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetWeiChip(wpids);
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