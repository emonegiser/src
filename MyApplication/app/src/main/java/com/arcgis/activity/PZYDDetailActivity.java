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
import com.arcgis.entity.PZYDEntity;
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

public class PZYDDetailActivity extends Activity implements View.OnClickListener{

    private Button queryBtn=null;

    private TextView backtextview;
    private TextView titletextview;
    private TextView mapBtn;

    //编号
    TextView editTextsbydbh=null;
    //坐落
    TextView editTextzl=null;
    //地块地址
    TextView editTexttdzsdz=null;
    //地块面积
    TextView editTextmj= null;
    //农用地名称
    TextView editTextnydzymc=null;
    //农用地文号
    TextView TextViewwh=null;
    //批准时间
    TextView TextViewpzsj=null;
    //两公告时间
    TextView TextViewdjsj=null;
    //征地补偿费用
    TextView TextViewzdbcfy= null;
    //地上附着物补偿费用
    TextView TextViewdsfzwbcfy= null;
    //是否纳入储备
    TextView TextViewsfzfcb=null;
    //地图图幅编号
    TextView TextViewtfbh= null;


    private PZYDEntity mPZYDEntity;

    private Point point;
    private List<Point> pzyd_pt_list=new ArrayList<Point>();

    //调用webservice
    private KsoapValidateHttp ksoap;

    private String dkbh;
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
        setContentView(R.layout.activity_pzyd_detail);
        isNetwork= NetUtils.isNetworkAvailable(this);
        App.getInstance().addActivity(this);

        backtextview=(TextView)findViewById(R.id.backBtn);
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.NavigateTitle);
        titletextview.setText("批准用地详情");

        mapBtn= (TextView) findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(this);

        df=(DecimalFormat) NumberFormat.getInstance();
        df.setMaximumFractionDigits(8);

        MyApp=(App) this.getApplication();

        editTextsbydbh= (TextView) this.findViewById(R.id.editTextsbydbh);
        editTextmj= (TextView) this.findViewById(R.id.editTextmj);
        editTextzl= (TextView) this.findViewById(R.id.editTextzl);
        editTexttdzsdz= (TextView) this.findViewById(R.id.editTexttdzsdz);
        editTextnydzymc= (TextView) this.findViewById(R.id.editTextnydzymc);
        TextViewwh= (TextView) this.findViewById(R.id.TextViewwh);
        TextViewpzsj= (TextView) this.findViewById(R.id.TextViewpzsj);
        TextViewdjsj= (TextView) this.findViewById(R.id.TextViewdjsj);
        TextViewzdbcfy= (TextView) this.findViewById(R.id.TextViewzdbcfy);
        TextViewdsfzwbcfy= (TextView) this.findViewById(R.id.TextViewdsfzwbcfy);
        TextViewsfzfcb= (TextView) this.findViewById(R.id.TextViewsfzfcb);
        TextViewtfbh= (TextView) this.findViewById(R.id.TextViewtfbh);

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
                toMapIntent.setClass(PZYDDetailActivity.this,MainMap4Activity.class);
                toMapIntent.putExtra("FROM","CENTERAT");
                toMapIntent.putExtra("PX",this.px);
                toMapIntent.putExtra("PY",this.py);
                toMapIntent.putExtra("TEXT",editTextsbydbh.getText().toString());
                startActivity(toMapIntent);
                break;
            case R.id.backBtn:
                PZYDDetailActivity.this.finish();
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            PZYDDetailActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = this.getIntent();
        if(intent!=null){
            mPZYDEntity= (PZYDEntity) intent.getSerializableExtra("PZYD");
            if(mPZYDEntity!=null){
                this.px=mPZYDEntity.getX();
                this.py=mPZYDEntity.getY();
                editTextsbydbh.setText(mPZYDEntity.getBH());
                editTextmj.setText(mPZYDEntity.getDKMJ());
                editTextzl.setText(mPZYDEntity.getDKZL());
                editTexttdzsdz.setText(mPZYDEntity.getXZ()+","+mPZYDEntity.getCUN());
                editTextnydzymc.setText(mPZYDEntity.getTDZSMC());
                TextViewwh.setText(mPZYDEntity.getPZWH());
                TextViewpzsj.setText(mPZYDEntity.getPZSJ());
                TextViewzdbcfy.setText(mPZYDEntity.getZDBCFY());
                TextViewdsfzwbcfy.setText(mPZYDEntity.getDSFZWBCFY());
                TextViewsfzfcb.setText(mPZYDEntity.getSFNRZFCB());
                TextViewtfbh.setText(mPZYDEntity.getDTTF());
            }
        }
        if(!isNetwork){
            ToastUtil.show(this,"请检查网络连接");
        }


        //获取多边形坐标
        GetDZDataSync getDZDataSync=new GetDZDataSync(mPZYDEntity.getBH(),mPZYDEntity.getTDZSMC());
        try {
            String rsltStr=getDZDataSync.execute().get(50, TimeUnit.SECONDS);
            if(rsltStr!=null){
                pzyd_pt_list=new ArrayList<Point>();
                JSONArray jsonArray = new JSONArray(rsltStr);
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject o = (JSONObject) jsonArray.get(k);
                    point = new Point();
                    if(o.has("X") && o.has("Y") ){
                        point.setX(Double.valueOf(o.get("X").toString()));
                        point.setY(Double.valueOf(o.get("Y").toString()));
                    }
                    Point mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(4326), SpatialReference.create(102100));
                    pzyd_pt_list.add(mapPoint);
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

        if(pzyd_pt_list!=null && pzyd_pt_list.size()>0){
            MyApp.setPZYDpointList(null);
            MyApp.setPZYDpointList(pzyd_pt_list);
        }

    }

    public class GetDZDataSync extends AsyncTask<String, Integer, String> {
        private String kcbh;
        private String mc;

        public GetDZDataSync(String kcbh,String mc) {
            this.kcbh=kcbh;
            this.mc=mc;
            ksoap=new KsoapValidateHttp(PZYDDetailActivity.this);
        }


        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.PZYDWebGetLandPosition(kcbh,mc);
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