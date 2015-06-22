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
import com.arcgis.entity.SBYDEntity;
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

public class SBYDDetailActivity extends Activity implements View.OnClickListener{

    private Button queryBtn=null;

    private TextView backtextview;
    private TextView titletextview;
    private TextView mapBtn;

    //编号
    TextView editTextsbydbh=null;
    //地块面积
    TextView editTextmj= null;
    //土地征收名称
    TextView editTexttdzsmc= null;
    //土地征收批文号
    TextView editTextpwh= null;
    //报送时间
    TextView editTextbssj=null;
    //登记时间
    TextView spinnerdjsj= null;
    //批复时间
    TextView spinnerpfsj= null;
    //征地补偿费用
    TextView spinnerzdbcfy= null;
    //地上附着物补偿费用
    TextView spinnerdsfzwbcf= null;
    //地图图幅编号
    TextView editTextdttfbh= null;


    private SBYDEntity mKCZYEntity;
    private Point point;
    private List<Point> sbyd_pt_list=new ArrayList<Point>();

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
        setContentView(R.layout.activity_sbyd_detail);
        isNetwork= NetUtils.isNetworkAvailable(this);
        App.getInstance().addActivity(this);

        backtextview=(TextView)findViewById(R.id.backBtn);
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.NavigateTitle);
        titletextview.setText("上报用地详情");

        mapBtn= (TextView) findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(this);

        df=(DecimalFormat) NumberFormat.getInstance();
        df.setMaximumFractionDigits(8);

        MyApp=(App) this.getApplication();


        editTextsbydbh= (TextView) this.findViewById(R.id.editTextsbydbh);
        editTextmj= (TextView) this.findViewById(R.id.editTextmj);
        editTexttdzsmc= (TextView) this.findViewById(R.id.editTexttdzsmc);
        editTextpwh= (TextView) this.findViewById(R.id.editTextpwh);
        editTextbssj= (TextView) this.findViewById(R.id.editTextbssj);
        spinnerdjsj= (TextView) this.findViewById(R.id.spinnerdjsj);
        spinnerpfsj= (TextView) this.findViewById(R.id.spinnerpfsj);
        spinnerzdbcfy= (TextView) this.findViewById(R.id.spinnerzdbcfy);
        spinnerdsfzwbcf= (TextView) this.findViewById(R.id.spinnerdsfzwbcf);
        editTextdttfbh= (TextView) this.findViewById(R.id.editTextdttfbh);

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
                toMapIntent.setClass(SBYDDetailActivity.this,MainMap3Activity.class);
                toMapIntent.putExtra("FROM","CENTERAT");
                toMapIntent.putExtra("PX",this.px);
                toMapIntent.putExtra("PY",this.py);
                toMapIntent.putExtra("TEXT",editTextsbydbh.getText().toString());
                startActivity(toMapIntent);
                break;
            case R.id.backBtn:
                SBYDDetailActivity.this.finish();
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            SBYDDetailActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = this.getIntent();
        if(intent!=null){
            mKCZYEntity= (SBYDEntity) intent.getSerializableExtra("SBYD");
            if(mKCZYEntity!=null){
                this.px=mKCZYEntity.getX();
                this.py=mKCZYEntity.getY();
                editTextsbydbh.setText(mKCZYEntity.getBH());
                editTextmj.setText(mKCZYEntity.getDKMJ());
                editTexttdzsmc.setText(mKCZYEntity.getTDZSMC());
                editTextpwh.setText(mKCZYEntity.getTDZSWH());
                editTextbssj.setText(mKCZYEntity.getBSSJ());
                spinnerdjsj.setText(mKCZYEntity.getDJSJ());
                spinnerdsfzwbcf.setText(mKCZYEntity.getDSFZWBCF());
                spinnerpfsj.setText(mKCZYEntity.getPFSJ());
                spinnerzdbcfy.setText(mKCZYEntity.getZDBCFY());
            }
        }
        if(!isNetwork){
            ToastUtil.show(this,"请检查网络连接");
            //return;
        }


        //获取多边形坐标
        GetDZDataSync getDZDataSync=new GetDZDataSync(mKCZYEntity.getBH(),mKCZYEntity.getTDZSMC());
        try {
            String rsltStr=getDZDataSync.execute().get(50, TimeUnit.SECONDS);
            if(rsltStr!=null){
                sbyd_pt_list=new ArrayList<Point>();
                JSONArray jsonArray = new JSONArray(rsltStr);
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject o = (JSONObject) jsonArray.get(k);
                    point = new Point();
                    if(o.has("X") && o.has("Y") ){
                        point.setX(Double.valueOf(o.get("X").toString()));
                        point.setY(Double.valueOf(o.get("Y").toString()));
                    }
                    Point mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(4326), SpatialReference.create(102100));
                    sbyd_pt_list.add(mapPoint);
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

        if(sbyd_pt_list!=null && sbyd_pt_list.size()>0){
            MyApp.setSbydpointList(null);
            MyApp.setSbydpointList(sbyd_pt_list);
        }

    }

    public class GetDZDataSync extends AsyncTask<String, Integer, String> {
        private String kcbh;
        private String mc;

        public GetDZDataSync(String kcbh,String mc) {
            this.kcbh=kcbh;
            this.mc=mc;
            ksoap=new KsoapValidateHttp(SBYDDetailActivity.this);
        }


        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetLandPosition(kcbh,mc);
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