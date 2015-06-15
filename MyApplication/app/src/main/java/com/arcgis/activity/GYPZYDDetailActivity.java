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
import com.arcgis.entity.GYPZYDEntity;
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

public class GYPZYDDetailActivity extends Activity implements View.OnClickListener{

    private Button queryBtn=null;

    private TextView backtextview;
    private TextView titletextview;
    private TextView mapBtn;

    //地块编号
    TextView editTextkqbh= null;
    //供应面积
    TextView editTextmj=null;
    //供地批复名称
    TextView editTextgdpfmc= null;
    //供地批复文号
    TextView EditTextgdpfwh=null;
    //供地批复时间
    TextView EditTextgdpfsj= null;
    //土地用途
    TextView spinnertdyt=null;
    //容积率
    TextView editTextrjl=null;
    //建设密度
    TextView editTextjsmd=null;
    //绿化比率
    TextView EditTextlhbl=null;
    //供应方式
    TextView spinnergyfs=null;
    //用地单位名称
    TextView editTextyddwmc=null;
    //用地项目名称
    TextView editTextydxmmc=null;
    //合同编号
    TextView EditTexthtbh=null;
    //成交款项
    TextView EditTextcjkx=null;
    //合同签订时间
    TextView EditTexthtjdsj=null;
    //实际交地时间
    TextView EditTextsjjdsj=null;
    //实际开工时间
    TextView EditTextsjkgsj=null;
    //竣工核验时间
    TextView EditTextjghysj=null;
    //合同约定交地时间
    TextView EditTexthtydjdsj=null;
    //合同约定动工时间
    TextView EditTexthtyddgsj=null;
    //合同约定竣工时间
    TextView EditTexthtydjgsj=null;
    //申请开工时间
    TextView EditTextsqkgsj=null;
    //实际竣工时间
    TextView EditTextsjjgsj=null;
    //竣工核验情况
    TextView spinnerjghyqk=null;


    private GYPZYDEntity mGYPZYDEntity;
    private Point point;
    private List<Point> gyyd_pt_list=new ArrayList<Point>();

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
        setContentView(R.layout.activity_gyyd_detail);
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



        //地块面积
        editTextmj= (TextView) findViewById(R.id.editTextmj);
        //地块编号
        editTextkqbh= (TextView) this.findViewById(R.id.editTextkqbh);
        editTextgdpfmc= (TextView) findViewById(R.id.editTextgdpfmc);
        EditTextgdpfwh= (TextView) findViewById(R.id.EditTextgdpfwh);
        EditTextgdpfsj= (TextView) findViewById(R.id.EditTextgdpfsj);
        spinnertdyt= (TextView) findViewById(R.id.spinnertdyt);
        editTextrjl= (TextView) findViewById(R.id.editTextrjl);
        editTextjsmd= (TextView) findViewById(R.id.editTextjsmd);
        EditTextlhbl= (TextView) findViewById(R.id.EditTextlhbl);
        spinnergyfs= (TextView) findViewById(R.id.spinnergyfs);
        editTextyddwmc= (TextView) findViewById(R.id.editTextyddwmc);
        editTextydxmmc= (TextView) findViewById(R.id.editTextydxmmc);
        EditTexthtbh= (TextView) findViewById(R.id.EditTexthtbh);
        EditTextcjkx= (TextView) findViewById(R.id.EditTextcjkx);
        EditTexthtjdsj= (TextView) findViewById(R.id.EditTexthtjdsj);
        EditTextsjjdsj= (TextView) findViewById(R.id.EditTextsjjdsj);
        EditTextsjkgsj= (TextView) findViewById(R.id.EditTextsjkgsj);
        EditTextjghysj= (TextView) findViewById(R.id.EditTextjghysj);
        EditTexthtydjdsj= (TextView) findViewById(R.id.EditTexthtydjdsj);
        EditTexthtyddgsj= (TextView) findViewById(R.id.EditTexthtyddgsj);
        EditTextsqkgsj= (TextView) findViewById(R.id.EditTextsqkgsj);
        EditTextsjjgsj= (TextView) findViewById(R.id.EditTextsjjgsj);
        EditTexthtydjgsj=(TextView) findViewById(R.id.EditTexthtydjgsj);
        spinnerjghyqk= (TextView) findViewById(R.id.spinnerjghyqk);

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
                toMapIntent.setClass(GYPZYDDetailActivity.this,MainMap5Activity.class);
                toMapIntent.putExtra("FROM","CENTERAT");
                toMapIntent.putExtra("PX",this.px);
                toMapIntent.putExtra("PY",this.py);
                toMapIntent.putExtra("TEXT",editTextkqbh.getText().toString());
                startActivity(toMapIntent);
                break;
            case R.id.backBtn:
                GYPZYDDetailActivity.this.finish();
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            GYPZYDDetailActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = this.getIntent();
        if(intent!=null){
            mGYPZYDEntity= (GYPZYDEntity) intent.getSerializableExtra("GYYD");
            if(mGYPZYDEntity!=null){
                this.px=mGYPZYDEntity.getX();
                this.py=mGYPZYDEntity.getY();
                editTextmj.setText(mGYPZYDEntity.getGYMJ());
                editTextkqbh.setText(mGYPZYDEntity.getGYYDBH());
                editTextgdpfmc.setText(mGYPZYDEntity.getGDPFMC());
                EditTextgdpfwh.setText(mGYPZYDEntity.getGDPFWH());
                EditTextgdpfsj.setText(mGYPZYDEntity.getGDPFSJ());
                spinnertdyt.setText(mGYPZYDEntity.getTDYT());
                editTextrjl.setText(mGYPZYDEntity.getRJL());
                editTextjsmd.setText(mGYPZYDEntity.getJZMD());
                EditTextlhbl.setText(mGYPZYDEntity.getLHBL());
                spinnergyfs.setText(mGYPZYDEntity.getGDFS());
                editTextyddwmc.setText(mGYPZYDEntity.getYDDWMC());
                editTextydxmmc.setText(mGYPZYDEntity.getYDXMMC());
                EditTexthtbh.setText(mGYPZYDEntity.getHTBH());
                EditTextcjkx.setText(mGYPZYDEntity.getCJJK());
                EditTexthtjdsj.setText(mGYPZYDEntity.getHTYDJDSJ());
                EditTextsjjdsj.setText(mGYPZYDEntity.getSJJDSJ());
                EditTextsjkgsj.setText(mGYPZYDEntity.getSJKGSJ());
                EditTextjghysj.setText(mGYPZYDEntity.getJGTDHYSJ());
                EditTexthtydjdsj.setText(mGYPZYDEntity.getHTYDJDSJ());
                EditTexthtyddgsj.setText(mGYPZYDEntity.getHTYDDGSJ());
                EditTextsqkgsj.setText(mGYPZYDEntity.getSJKGSJ());
                EditTextsjjgsj.setText(mGYPZYDEntity.getSJJGSJ());
                EditTexthtydjgsj.setText(mGYPZYDEntity.getHTYDJGSJ());
                spinnerjghyqk.setText(mGYPZYDEntity.getJGTDHYQK());
            }
        }
        if(!isNetwork){
            ToastUtil.show(this,"请检查网络连接");
        }


        //获取多边形坐标
        GetDZDataSync getDZDataSync=new GetDZDataSync(mGYPZYDEntity.getGYYDBH(),mGYPZYDEntity.getGDPFMC());
        try {
            String rsltStr=getDZDataSync.execute().get(50, TimeUnit.SECONDS);
            gyyd_pt_list=new ArrayList<Point>();
            gyyd_pt_list.clear();
            if(rsltStr!=null && rsltStr.contains("X") && rsltStr.contains("Y")){
                JSONArray jsonArray = new JSONArray(rsltStr);
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject o = (JSONObject) jsonArray.get(k);
                    point = new Point();
                    if(o.has("X") && o.has("Y") ){
                        point.setX(Double.valueOf(o.get("X").toString()));
                        point.setY(Double.valueOf(o.get("Y").toString()));
                    }
                    Point mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(4326), SpatialReference.create(102100));
                    gyyd_pt_list.add(mapPoint);
                }
            }else{
                ToastUtil.show(this,"服务器返回JSON格式不正确");
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

        if(gyyd_pt_list!=null && gyyd_pt_list.size()>0){
            MyApp.setGYYDpointList(null);
            MyApp.setGYYDpointList(gyyd_pt_list);
        }

    }

    public class GetDZDataSync extends AsyncTask<String, Integer, String> {
        private String bh;
        private String GDPFMC;


        public GetDZDataSync(String bh,String GDPFMC) {
            this.bh=bh;
            this.GDPFMC=GDPFMC;
            ksoap=new KsoapValidateHttp(GYPZYDDetailActivity.this);
        }


        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGYYDGetLandPosition(bh, GDPFMC);
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