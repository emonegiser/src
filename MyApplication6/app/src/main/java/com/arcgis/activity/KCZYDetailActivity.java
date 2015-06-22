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
import com.arcgis.entity.KCZYEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class KCZYDetailActivity extends Activity implements View.OnClickListener{

    private Button queryBtn=null;

    private TextView backtextview;
    private TextView titletextview;
    private TextView mapBtn;

    TextView editTextxzb= null;
    TextView editTextyzb= null;
    TextView editTextkqbh= null;
    TextView editTextname=null;
    TextView editTextclsj= null;
    TextView editTextkccl= null;
    TextView kcdm= null;
    TextView editTextkcmj= null;
    TextView editTextkcfzr= null;
    TextView editTextlxdh= null;
    TextView editTextbz=null;
    TextView spinnerkclx= null;
    TextView spinnerclzt= null;
    TextView spinnerssc= null;
    TextView spinnerhfx=null;
    TextView spinnerssxz=null;
    TextView editTexttplj=null;
    TextView editTextsplj=null;


    private KCZYEntity mKCZYEntity;
    private Point point;
    private List<Point> pt_list=new ArrayList<Point>();

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
        setContentView(R.layout.activity_kczy_detail);
        isNetwork= NetUtils.isNetworkAvailable(this);
        App.getInstance().addActivity(this);
        backtextview=(TextView)findViewById(R.id.backBtn);
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.NavigateTitle);
        titletextview.setText("矿产资源详情");

        mapBtn= (TextView) findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(this);

        df=(DecimalFormat) NumberFormat.getInstance();
        df.setMaximumFractionDigits(8);

        MyApp=(App) this.getApplication();

        //矿区坐标
        editTextxzb= (TextView) this.findViewById(R.id.editTextxzb);
        editTextyzb= (TextView) this.findViewById(R.id.editTextyzb);
        //矿区编号
        editTextkqbh= (TextView) this.findViewById(R.id.editTextkqbh);
        //处理时间
        editTextclsj= (TextView) this.findViewById(R.id.editTextclsj);
        //矿产名称
        editTextname= (TextView) this.findViewById(R.id.editTextname);
        //矿产储量
        editTextkccl= (TextView) this.findViewById(R.id.editTextkccl);
        //地名
        kcdm= (TextView) this.findViewById(R.id.kcdm);
        //矿产面积
        editTextkcmj= (TextView) this.findViewById(R.id.editTextkcmj);
        //负责人
        editTextkcfzr= (TextView) this.findViewById(R.id.editTextkcfzr);
        //联系电话
        editTextlxdh= (TextView) this.findViewById(R.id.editTextlxdh);
        //备注
        editTextbz= (TextView) this.findViewById(R.id.editTextbz);
        //矿产类型
        spinnerkclx= (TextView) this.findViewById(R.id.spinnerkclx);
        //处理状态
        spinnerclzt= (TextView) this.findViewById(R.id.spinnerclzt);
        //所属村
        spinnerssc= (TextView) this.findViewById(R.id.spinnerssc);
        //所属乡镇
        spinnerssxz=(TextView)this.findViewById(R.id.spinnerssxz);
        //合法性
        spinnerhfx= (TextView) this.findViewById(R.id.spinnerhfx);
        //图片路径
        editTexttplj= (TextView) this.findViewById(R.id.editTexttplj);
        //视频路径
        editTextsplj= (TextView) this.findViewById(R.id.editTextsplj);

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
                Intent toMapIntent=new Intent();
                toMapIntent.setClass(KCZYDetailActivity.this,MainMap2Activity.class);
                toMapIntent.putExtra("FROM","CENTERAT");
                toMapIntent.putExtra("PX",this.px);
                toMapIntent.putExtra("PY",this.py);
                toMapIntent.putExtra("TEXT",editTextkqbh.getText().toString());
                startActivity(toMapIntent);
                break;
            case R.id.backBtn:
                KCZYDetailActivity.this.finish();
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            KCZYDetailActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = this.getIntent();
        if(intent!=null){
            mKCZYEntity= (KCZYEntity) intent.getSerializableExtra("KCZY");
            if(mKCZYEntity!=null){
                this.px=mKCZYEntity.getPx();
                this.py=mKCZYEntity.getPy();
                editTextxzb.setText(df.format(Double.valueOf(mKCZYEntity.getPx())));
                editTextyzb.setText(df.format(Double.valueOf(mKCZYEntity.getPy())));
                editTextkqbh.setText(mKCZYEntity.getKcNo());
                editTextname.setText(mKCZYEntity.getKcName());
                editTextclsj.setText(mKCZYEntity.getAddtime());
                editTextkccl.setText(mKCZYEntity.getKccl());
                kcdm.setText(mKCZYEntity.getXxwz());
                editTextkcmj.setText(mKCZYEntity.getKcmj());
                editTextkcfzr.setText(mKCZYEntity.getJianceren());
                editTextlxdh.setText(mKCZYEntity.getPhone());
                spinnerkclx.setText(mKCZYEntity.getKcType());
                spinnerssc.setText(mKCZYEntity.getSscun());
                spinnerhfx.setText(mKCZYEntity.getSFHF());
                spinnerssxz.setText(mKCZYEntity.getSsxz());
                editTexttplj.setText(mKCZYEntity.getPhotoPath());
                editTextsplj.setText(mKCZYEntity.getVideoPath());
            }
        }
        if(!isNetwork){
            ToastUtil.show(this,"请检查网络连接");
            //return;
        }


        //获取多边形坐标
        GetDZDataSync getDZDataSync=new GetDZDataSync(mKCZYEntity.getKcNo());
        try {
            String rsltStr=getDZDataSync.execute().get(5000, TimeUnit.MILLISECONDS);
            if(rsltStr!=null){
                pt_list=new ArrayList<Point>();
                JSONArray jsonArray = new JSONArray(rsltStr);
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject o = (JSONObject) jsonArray.get(k);
                    point = new Point();
                    if(o.has("X") && o.has("Y") ){
                        point.setX(Double.valueOf(o.get("X").toString()));
                        point.setY(Double.valueOf(o.get("Y").toString()));
                    }
                    Point mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(2359), SpatialReference.create(102100));
                    pt_list.add(mapPoint);
                }
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
            ToastUtil.show(this,"JSON解析错误");
        }

        if(pt_list!=null && pt_list.size()>0){
            MyApp.setPointList(null);
            MyApp.setPointList(pt_list);
        }

    }

    public class GetDZDataSync extends AsyncTask<String, Integer, String> {
        private String kcbh;

        public GetDZDataSync(String kcbh) {
            this.kcbh=kcbh;
            ksoap=new KsoapValidateHttp(KCZYDetailActivity.this);
        }


        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetLandPosition(kcbh);
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